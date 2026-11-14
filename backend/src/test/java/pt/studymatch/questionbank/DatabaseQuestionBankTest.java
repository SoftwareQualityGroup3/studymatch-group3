package pt.studymatch.questionbank;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseQuestionBankTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static JsonNode root;

    @BeforeAll
    static void loadFile() throws Exception {
        try (InputStream input = DatabaseQuestionBankTest.class
            .getResourceAsStream("/question-bank/databases.json")) {

            assertNotNull(input, "databases.json não encontrado");
            root = MAPPER.readTree(input);
        }
    }

    @Test
    void validatesQuestionBankStructure() {
        assertEquals("DATABASES", root.path("competency").asText());

        JsonNode questions = root.path("questions");

        assertTrue(questions.isArray());
        assertEquals(40, questions.size());

        Set<String> ids = new HashSet<>();
        Map<Integer, Set<Integer>> variantsPerLevel = new HashMap<>();

        for (JsonNode question : questions) {
            String id = question.path("id").asText();
            int level = question.path("level").asInt();
            int variant = question.path("variant").asInt();

            assertFalse(id.isBlank(), "Pergunta sem ID");
            assertTrue(ids.add(id), "ID repetido: " + id);

            assertTrue(
                level >= 1 && level <= 20,
                "Nível inválido em " + id
            );

            assertTrue(
                variant == 1 || variant == 2,
                "Variante inválida em " + id
            );

            assertEquals(
                "DATABASES",
                question.path("competency").asText(),
                "Competência inválida em " + id
            );

            assertTrue(
                question.path("type").asText().matches(
                    "QUERY_RESULT|CORRECT_QUERY|FIND_ERROR"
                ),
                "Tipo inválido em " + id
            );

            assertFalse(
                question.path("statement").asText().isBlank(),
                "Enunciado vazio em " + id
            );

            assertFalse(
                question.path("explanation").asText().isBlank(),
                "Explicação vazia em " + id
            );

            JsonNode options = question.path("options");

            assertTrue(options.isArray(), "Opções inválidas em " + id);
            assertEquals(
                4,
                options.size(),
                "Número de opções inválido em " + id
            );

            Set<String> optionIds = new HashSet<>();
            Set<String> optionTexts = new HashSet<>();

            for (JsonNode option : options) {
                String optionId = option.path("id").asText();
                String optionText = option.path("text").asText();

                assertTrue(
                    optionId.matches("[A-D]"),
                    "ID de opção inválido em " + id
                );

                assertTrue(
                    optionIds.add(optionId),
                    "ID de opção repetido em " + id
                );

                assertFalse(
                    optionText.isBlank(),
                    "Texto de opção vazio em " + id
                );

                assertTrue(
                    optionTexts.add(optionText),
                    "Opções repetidas em " + id
                );
            }

            assertEquals(
                Set.of("A", "B", "C", "D"),
                optionIds,
                "As opções devem ser A, B, C e D em " + id
            );

            String correctOption = question.path("correctOption").asText();

            assertTrue(
                optionIds.contains(correctOption),
                "correctOption inválido em " + id
            );

            variantsPerLevel
                .computeIfAbsent(level, ignored -> new HashSet<>())
                .add(variant);
        }

        assertEquals(
            40,
            ids.size(),
            "Devem existir exatamente 40 perguntas diferentes"
        );

        for (int level = 1; level <= 20; level++) {
            assertEquals(
                Set.of(1, 2),
                variantsPerLevel.get(level),
                "O nível " + level + " deve ter as variantes 1 e 2"
            );
        }
    }

    @Test
    void everyCorrectOptionExistsAmongQuestionOptions() {
        for (JsonNode question : root.path("questions")) {
            String questionId = question.path("id").asText();
            String correctOption = question.path("correctOption").asText();

            boolean found = false;

            for (JsonNode option : question.path("options")) {
                if (correctOption.equals(option.path("id").asText())) {
                    found = true;
                    break;
                }
            }

            assertTrue(
                found,
                "A resposta correta não existe nas opções: " + questionId
            );
        }
    }

    @Test
    void everyQuestionHasSqlVerification() {
        for (JsonNode question : root.path("questions")) {
            String questionId = question.path("id").asText();
            JsonNode verification = question.path("verification");

            assertTrue(
                verification.isObject(),
                "Verification inválida: " + questionId
            );

            JsonNode tables = verification.path("tables");

            assertTrue(
                tables.isArray(),
                "Tables inválido: " + questionId
            );

            assertTrue(
                tables.size() >= 1 && tables.size() <= 3,
                "Cada pergunta deve ter entre 1 e 3 tabelas: " + questionId
            );

            assertFalse(
                verification.path("query").asText().isBlank(),
                "Query vazia: " + questionId
            );

            assertTrue(
                verification.path("expectedResult").isArray(),
                "expectedResult inválido: " + questionId
            );

            Set<String> tableNames = new HashSet<>();

            for (JsonNode table : tables) {
                String tableName = table.path("name").asText();

                assertFalse(
                    tableName.isBlank(),
                    "Tabela sem nome: " + questionId
                );

                assertTrue(
                    tableNames.add(tableName),
                    "Tabela repetida: " + questionId
                );

                assertTrue(
                    table.path("columns").isArray(),
                    "Colunas inválidas: " + questionId
                );

                assertTrue(
                    table.path("columns").size() > 0,
                    "Tabela sem colunas: " + questionId
                );

                assertTrue(
                    table.path("rows").isArray(),
                    "Linhas inválidas: " + questionId
                );
            }
        }
    }

    @Test
    void correctQueryOptionsAreCompleteQueries() {
        for (JsonNode question : root.path("questions")) {
            if (!"CORRECT_QUERY".equals(question.path("type").asText())) {
                continue;
            }

            String questionId = question.path("id").asText();

            for (JsonNode option : question.path("options")) {
                String text = option.path("text").asText().trim();

                assertFalse(
                    text.isBlank(),
                    "Opção vazia: " + questionId
                );

                assertTrue(
                    isCompleteSqlQuery(text),
                    "Opção não é uma query completa em "
                        + questionId + ": " + text
                );
            }
        }
    }

    @Test
    void queriesWithMultipleRowsDefineTheirOrder() {
        for (JsonNode question : root.path("questions")) {
            JsonNode expectedResult = question
                .path("verification")
                .path("expectedResult");

            if (expectedResult.size() <= 1) {
                continue;
            }

            String query = question
                .path("verification")
                .path("query")
                .asText()
                .toUpperCase(Locale.ROOT);

            assertTrue(
                query.contains("ORDER BY"),
                "A query deve definir ORDER BY quando devolve "
                    + "várias linhas: " + question.path("id").asText()
            );
        }
    }

    @Test
    void executesAllQueriesAgainstMySql() throws Exception {
        try (Connection connection = openConnection()) {
            for (JsonNode question : root.path("questions")) {
                executeQuestionVerification(connection, question);
            }
        }
    }

    private static void executeQuestionVerification(
        Connection connection,
        JsonNode question
    ) throws SQLException {
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

            String query = replaceTableNames(
                question.path("verification").path("query").asText(),
                tableNames
            );

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
            dropTables(connection, tableNames.values());
        }
    }

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

        try (PreparedStatement statement =
                 connection.prepareStatement(sql)) {

            for (JsonNode row : table.path("rows")) {
                for (int index = 0; index < row.size(); index++) {
                    JsonNode value = row.get(index);

                    if (value.isNull()) {
                        statement.setNull(index + 1, Types.NULL);
                    } else if (value.isInt()) {
                        statement.setInt(index + 1, value.asInt());
                    } else if (value.isLong()) {
                        statement.setLong(index + 1, value.asLong());
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

    private static List<List<Object>> executeQuery(
        Connection connection,
        String sql
    ) throws SQLException {
        List<List<Object>> result = new ArrayList<>();

        try (
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql)
        ) {
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

            for (
                int columnIndex = 0;
                columnIndex < expectedRow.size();
                columnIndex++
            ) {
                Object expectedValue = expectedRow.get(columnIndex);
                Object actualValue = actualRow.get(columnIndex);

                if (
                    expectedValue instanceof Number
                        && actualValue instanceof Number
                ) {
                    assertEquals(
                        0,
                        new BigDecimal(expectedValue.toString())
                            .compareTo(
                                new BigDecimal(actualValue.toString())
                            ),
                        "Valor incorreto: " + questionId
                    );
                } else {
                    assertEquals(
                        expectedValue,
                        actualValue,
                        "Valor incorreto: " + questionId
                    );
                }
            }
        }
    }

    private static boolean isCompleteSqlQuery(String text) {
        String upper = text.toUpperCase(Locale.ROOT);

        boolean startsWithSelect = upper.startsWith("SELECT ");
        boolean hasFrom = upper.contains(" FROM ");
        boolean hasWhere = upper.contains(" WHERE ");
        boolean hasGroupBy = upper.contains(" GROUP BY ");
        boolean hasJoin = upper.contains(" JOIN ");

        if (!startsWithSelect || !hasFrom) {
            return false;
        }

        return hasWhere || hasGroupBy || hasJoin || upper.contains(" HAVING ");
    }

    private static String replaceTableNames(
        String query,
        Map<String, String> tableNames
    ) {
        String result = query;

        for (Map.Entry<String, String> entry : tableNames.entrySet()) {
            result = result.replace(
                "{{" + entry.getKey() + "}}",
                entry.getValue()
            );
        }

        return result;
    }

    private static void dropTables(
        Connection connection,
        Collection<String> tableNames
    ) {
        List<String> names = new ArrayList<>(tableNames);
        Collections.reverse(names);

        for (String tableName : names) {
            try (Statement statement = connection.createStatement()) {
                statement.execute("DROP TABLE IF EXISTS " + tableName);
            } catch (SQLException ignored) {
                // A limpeza falhada não deve esconder o erro principal.
            }
        }
    }

    private static Connection openConnection() throws SQLException {
        String host = environment("DB_HOST", "localhost");
        String port = environment("DB_PORT", "3306");
        String database = environment("DB_NAME", "studymatch_test");
        String user = requiredEnvironment("DB_USER");
        String password = requiredEnvironment("DB_PASSWORD");

        String url = "jdbc:mysql://" + host + ":" + port + "/"
            + database
            + "?useSSL=false"
            + "&allowPublicKeyRetrieval=true"
            + "&serverTimezone=UTC";

        return DriverManager.getConnection(url, user, password);
    }

    private static String environment(
        String name,
        String fallback
    ) {
        String value = System.getenv(name);

        return value == null || value.isBlank()
            ? fallback
            : value;
    }

    private static String requiredEnvironment(String name) {
        String value = System.getenv(name);

        assertNotNull(
            value,
            "Variável de ambiente em falta: " + name
        );

        assertFalse(
            value.isBlank(),
            "Variável de ambiente vazia: " + name
        );

        return value;
    }
}
