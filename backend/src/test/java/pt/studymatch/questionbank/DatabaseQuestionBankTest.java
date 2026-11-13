package pt.studymatch.questionbank;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.sql.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseQuestionBankTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static JsonNode root;

    // carrega o banco de perguntas uma única vez antes dos testes
    @BeforeAll
    static void loadFile() throws Exception {
        try (InputStream input = DatabaseQuestionBankTest.class
            .getResourceAsStream("/question-bank/databases.json")) {

            assertNotNull(input, "databases.json não encontrado");
            root = MAPPER.readTree(input);
        }
    }

    // confirma a quantidade de perguntas, níveis, variantes e opções
    @Test
    void validatesQuestionBankStructure() {
        assertEquals("DATABASES", root.path("competency").asText());

        JsonNode questions = root.path("questions");
        assertTrue(questions.isArray());
        assertEquals(40, questions.size());

        Set<String> ids = new HashSet<>();
        Map<Integer, Integer> questionsPerLevel = new HashMap<>();

        for (JsonNode question : questions) {
            String id = question.path("id").asText();
            int level = question.path("level").asInt();
            int variant = question.path("variant").asInt();

            assertTrue(ids.add(id), "ID repetido: " + id);
            assertTrue(level >= 1 && level <= 20);
            assertTrue(variant == 1 || variant == 2);
            assertEquals("DATABASES", question.path("competency").asText());
            assertFalse(question.path("statement").asText().isBlank());
            assertFalse(question.path("explanation").asText().isBlank());

            JsonNode options = question.path("options");
            assertEquals(4, options.size());

            Set<String> optionIds = new HashSet<>();

            for (JsonNode option : options) {
                String optionId = option.path("id").asText();

                assertTrue(optionId.matches("[A-D]"));
                assertFalse(option.path("text").asText().isBlank());
                assertTrue(optionIds.add(optionId));
            }

            String correctOption = question.path("correctOption").asText();
            assertTrue(optionIds.contains(correctOption));

            questionsPerLevel.merge(level, 1, Integer::sum);
        }

        for (int level = 1; level <= 20; level++) {
            assertEquals(
                2,
                questionsPerLevel.getOrDefault(level, 0),
                "Cada nível deve ter duas variantes"
            );
        }
    }

    // garante que todas as perguntas podem ser verificadas através de SQL
    @Test
    void everyQuestionHasSqlVerification() {
        for (JsonNode question : root.path("questions")) {
            JsonNode verification = question.path("verification");

            assertFalse(
                verification.isMissingNode(),
                "Pergunta sem verification: " + question.path("id").asText()
            );

            assertTrue(verification.path("tables").isArray());
            assertFalse(verification.path("tables").isEmpty());
            assertFalse(verification.path("query").asText().isBlank());
            assertTrue(verification.path("expectedResult").isArray());
        }
    }

    // executa todas as queries contra a base de dados MySQL de testes
    @Test
    void executesAllQueriesAgainstMySql() throws Exception {
        String host = environment("DB_HOST", "localhost");
        String port = environment("DB_PORT", "3306");
        String database = environment("DB_NAME", "studymatch_test");
        String user = requiredEnvironment("DB_USER");
        String password = requiredEnvironment("DB_PASSWORD");

        String url = "jdbc:mysql://" + host + ":" + port + "/" + database
            + "?useSSL=false&allowPublicKeyRetrieval=true"
            + "&serverTimezone=UTC";

        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            for (JsonNode question : root.path("questions")) {
                executeQuestionVerification(connection, question);
            }
        }
    }

    private static void executeQuestionVerification(
        Connection connection,
        JsonNode question
    ) throws SQLException {
        // cada pergunta usa tabelas temporárias com nomes únicos
        String suffix = UUID.randomUUID()
            .toString()
            .replace("-", "")
            .substring(0, 12);

        Map<String, String> tableNames = new LinkedHashMap<>();
        JsonNode tables = question.path("verification").path("tables");

        try {
            for (JsonNode table : tables) {
                String originalName = table.path("name").asText();
                String testName = originalName + "_" + suffix;
                tableNames.put(originalName, testName);

                createTable(connection, table, testName);
                insertRows(connection, table, testName);
            }

            String query = question.path("verification")
                .path("query")
                .asText();

            // substitui os placeholders do JSON pelos nomes temporários reais
            for (Map.Entry<String, String> entry : tableNames.entrySet()) {
                query = query.replace(
                    "{{" + entry.getKey() + "}}",
                    entry.getValue()
                );

            }

            List<List<Object>> actual = executeQuery(connection, query);

            List<List<Object>> expected = MAPPER.convertValue(
                question.path("verification").path("expectedResult"),
                new TypeReference<>() {}
            );

            assertRowsEqual(
                expected,
                actual,
                question.path("id").asText()
            );
        } finally {
            // remove as tabelas temporárias mesmo quando a verificação falha
            for (String tableName : tableNames.values()) {
                try (Statement statement = connection.createStatement()) {
                    statement.execute("DROP TABLE IF EXISTS " + tableName);
                }
            }
        }
    }

    // cria a tabela temporária definida na verificação da pergunta
    private static void createTable(
        Connection connection,
        JsonNode table,
        String tableName
    ) throws SQLException {
        List<String> columns = new ArrayList<>();

        for (JsonNode column : table.path("columns")) {
            columns.add(
                column.path("name").asText()
                    + " "
                    + column.path("type").asText()
            );
        }

        String sql = "CREATE TABLE " + tableName
            + " (" + String.join(", ", columns) + ")";

        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }

    // insere os dados de exemplo na tabela temporária
    private static void insertRows(
        Connection connection,
        JsonNode table,
        String tableName
    ) throws SQLException {
        JsonNode columns = table.path("columns");
        String placeholders = String.join(
            ", ",
            Collections.nCopies(columns.size(), "?")
        );

        String sql = "INSERT INTO " + tableName
            + " VALUES (" + placeholders + ")";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (JsonNode row : table.path("rows")) {
                for (int index = 0; index < row.size(); index++) {
                    JsonNode value = row.get(index);

                    if (value.isNull()) {
                        statement.setNull(index + 1, Types.NULL);
                    } else if (value.isInt()) {
                        statement.setInt(index + 1, value.asInt());
                    } else if (value.isBoolean()) {
                        statement.setBoolean(index + 1, value.asBoolean());
                    } else {
                        statement.setString(index + 1, value.asText());
                    }
                }

                statement.addBatch();
            }

            statement.executeBatch();
        }
    }

    private static void assertRowsEqual(
        List<List<Object>> expected,
        List<List<Object>> actual,
        String questionId
    ) {
        assertEquals(
            expected.size(),
            actual.size(),
            "Número de linhas incorreto: " + questionId
        );

        for (int rowIndex = 0; rowIndex < expected.size(); rowIndex++) {
            List<Object> expectedRow = expected.get(rowIndex);
            List<Object> actualRow = actual.get(rowIndex);

            assertEquals(
                expectedRow.size(),
                actualRow.size(),
                "Número de colunas incorreto: " + questionId
            );

            for (int columnIndex = 0; columnIndex < expectedRow.size(); columnIndex++) {
                Object expectedValue = expectedRow.get(columnIndex);
                Object actualValue = actualRow.get(columnIndex);

                if (expectedValue instanceof Number
                    && actualValue instanceof Number) {

                    assertEquals(
                        0,
                        new java.math.BigDecimal(expectedValue.toString())
                            .compareTo(new java.math.BigDecimal(actualValue.toString())),
                        "Valor incorreto na pergunta " + questionId
                    );
                } else {
                    assertEquals(
                        expectedValue,
                        actualValue,
                        "Valor incorreto na pergunta " + questionId
                    );
                }
            }
        }
    }

    // executa a query e converte o resultado para uma estrutura comparável
    private static List<List<Object>> executeQuery(
        Connection connection,
        String sql
    ) throws SQLException {
        List<List<Object>> result = new ArrayList<>();

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            int columnCount = resultSet.getMetaData().getColumnCount();

            while (resultSet.next()) {
                List<Object> row = new ArrayList<>();

                for (int column = 1; column <= columnCount; column++) {
                    row.add(resultSet.getObject(column));
                }

                result.add(row);
            }
        }

        return result;
    }

    private static String environment(String name, String fallback) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? fallback : value;
    }

    private static String requiredEnvironment(String name) {
        String value = System.getenv(name);

        assertNotNull(value, "Variável de ambiente em falta: " + name);
        assertFalse(value.isBlank(), "Variável de ambiente vazia: " + name);

        return value;
    }
}
