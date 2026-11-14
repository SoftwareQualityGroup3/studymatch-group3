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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    void correctQueryQuestionsHaveExactlyOneCorrectOption() throws Exception {
        try (Connection connection = openConnection()) {
            for (JsonNode question : root.path("questions")) {
                if (!"CORRECT_QUERY".equals(question.path("type").asText())) {
                    continue;
                }

                String questionId = question.path("id").asText();
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

                    List<List<Object>> expected = MAPPER.convertValue(
                        question.path("verification").path("expectedResult"),
                        new TypeReference<>() {}
                    );

                    int matchingOptions = 0;
                    String matchingOption = null;

                    for (JsonNode option : question.path("options")) {
                        String optionId = option.path("id").asText();
                        String query = replaceTableNames(
                            option.path("text").asText(),
                            tableNames
                        );

                        List<List<Object>> actual = executeQuery(
                            connection,
                            query
                        );

                        if (rowsEqual(expected, actual)) {
                            matchingOptions++;
                            matchingOption = optionId;
                        }
                    }

                    assertEquals(
                        1,
                        matchingOptions,
                        "Deve existir exatamente uma opção correta em "
                            + questionId
                    );

                    assertEquals(
                        question.path("correctOption").asText(),
                        matchingOption,
                        "correctOption incorreto em " + questionId
                    );
                } finally {
                    dropTables(connection, tableNames.values());
                }
            }
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

            for (JsonNode index : table.path("indexes")) {
                List<String> indexColumns = new ArrayList<>();

                for (JsonNode column : index.path("columns")) {
                    indexColumns.add(column.asText());
                }

                statement.execute(
                    "CREATE INDEX " + index.path("name").asText()
                        + " ON " + tableName
                        + " (" + String.join(", ", indexColumns) + ")"
                );
            }
        }
    }

    private static JsonNode findQuestion(String questionId) {
        for (JsonNode question : root.path("questions")) {
            if (questionId.equals(question.path("id").asText())) {
                return question;
            }
        }

        fail("Pergunta não encontrada: " + questionId);
        return null;
    }

    private static void applyConstraints(
        Connection connection,
        JsonNode table,
        Map<String, String> tableNames
    ) throws SQLException {
        String tableName = tableNames.get(table.path("name").asText());
        JsonNode constraints = table.path("constraints");

        if (constraints.has("primaryKey")) {
            List<String> columns = new ArrayList<>();

            for (JsonNode column : constraints.path("primaryKey")) {
                columns.add(column.asText());
            }

            executeSql(
                connection,
                "ALTER TABLE " + tableName
                    + " ADD PRIMARY KEY (" + String.join(", ", columns) + ")"
            );
        }

        for (JsonNode foreignKey : constraints.path("foreignKeys")) {
            List<String> columns = new ArrayList<>();
            List<String> referencedColumns = new ArrayList<>();

            for (JsonNode column : foreignKey.path("columns")) {
                columns.add(column.asText());
            }

            for (JsonNode column : foreignKey.path("referencedColumns")) {
                referencedColumns.add(column.asText());
            }

            String referencedTable = tableNames.get(
                foreignKey.path("referencedTable").asText()
            );

            executeSql(
                connection,
                "ALTER TABLE " + tableName
                    + " ADD FOREIGN KEY ("
                    + String.join(", ", columns)
                    + ") REFERENCES "
                    + referencedTable
                    + " ("
                    + String.join(", ", referencedColumns)
                    + ")"
            );
        }
    }

    private static void insertRawRow(
        Connection connection,
        String tableName,
        List<?> values
    ) throws SQLException {
        String placeholders = String.join(
            ", ",
            Collections.nCopies(values.size(), "?")
        );

        String sql = "INSERT INTO " + tableName
            + " VALUES (" + placeholders + ")";

        try (PreparedStatement statement =
                 connection.prepareStatement(sql)) {

            for (int index = 0; index < values.size(); index++) {
                statement.setObject(index + 1, values.get(index));
            }

            statement.executeUpdate();
        }
    }

    private static void executeSql(
        Connection connection,
        String sql
    ) throws SQLException {
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

    private static boolean rowsEqual(
        List<List<Object>> expected,
        List<List<Object>> actual
    ) {
        if (expected.size() != actual.size()) {
            return false;
        }

        for (int rowIndex = 0; rowIndex < expected.size(); rowIndex++) {
            List<Object> expectedRow = expected.get(rowIndex);
            List<Object> actualRow = actual.get(rowIndex);

            if (expectedRow.size() != actualRow.size()) {
                return false;
            }

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
                    if (
                        new BigDecimal(expectedValue.toString())
                            .compareTo(
                                new BigDecimal(actualValue.toString())
                            ) != 0
                    ) {
                        return false;
                    }
                } else if (!Objects.equals(expectedValue, actualValue)) {
                    return false;
                }
            }
        }

        return true;
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

            result = result.replaceAll(
                "(?i)\\b"
                    + Pattern.quote(entry.getKey())
                    + "\\b",
                Matcher.quoteReplacement(entry.getValue())
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

    @Test
    void primaryAndForeignKeysAreEnforced() throws Exception {
        JsonNode question = findQuestion("databases-l05-v01");

        try (Connection connection = openConnection()) {
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
                }

                for (JsonNode table : tables) {
                    applyConstraints(connection, table, tableNames);
                    insertRows(
                        connection,
                        table,
                        tableNames.get(table.path("name").asText())
                    );
                }

                String enrollmentTable = tableNames.get("enrollment");

                SQLException exception = assertThrows(
                    SQLException.class,
                    () -> insertRawRow(
                        connection,
                        enrollmentTable,
                        List.of(99)
                    )
                );

                assertTrue(
                    exception.getMessage().toLowerCase(Locale.ROOT)
                        .contains("foreign"),
                    "A violação deveria ser causada pela foreign key"
                );
            } finally {
                dropTables(connection, tableNames.values());
            }
        }
    }

    @Test
    void commitAndRollbackAreActuallyEnforced() throws Exception {
        JsonNode rollbackQuestion = findQuestion("databases-l16-v01");
        JsonNode commitQuestion = findQuestion("databases-l16-v02");

        try (Connection connection = openConnection()) {
            String suffix = UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 12);

            String accountTable = "account_" + suffix;
            String logTable = "log_entry_" + suffix;

            try {
                JsonNode account = rollbackQuestion
                    .path("verification")
                    .path("tables")
                    .get(0);

                JsonNode logEntry = commitQuestion
                    .path("verification")
                    .path("tables")
                    .get(0);

                createTable(connection, account, accountTable);
                insertRows(connection, account, accountTable);

                connection.setAutoCommit(false);

                try (Statement statement = connection.createStatement()) {
                    statement.executeUpdate(
                        "UPDATE " + accountTable
                            + " SET balance = 50 WHERE id = 1"
                    );
                }

                connection.rollback();

                assertEquals(
                    List.of(List.of(100)),
                    executeQuery(
                        connection,
                        "SELECT balance FROM " + accountTable
                            + " WHERE id = 1"
                    )
                );

                connection.setAutoCommit(true);

                createTable(connection, logEntry, logTable);
                insertRows(connection, logEntry, logTable);

                connection.setAutoCommit(false);

                try (Statement statement = connection.createStatement()) {
                    statement.executeUpdate(
                        "INSERT INTO " + logTable
                            + " VALUES (3, 'committed')"
                    );
                }

                connection.commit();
                connection.setAutoCommit(true);

                assertEquals(
                    List.of(List.of(3L)),
                    executeQuery(
                        connection,
                        "SELECT COUNT(*) FROM " + logTable
                    )
                );
            } finally {
                if (!connection.getAutoCommit()) {
                    connection.rollback();
                    connection.setAutoCommit(true);
                }

                dropTables(connection, List.of(accountTable, logTable));
            }
        }
    }

    @Test
    void transactionIsolationScenariosAreActuallyExecuted() throws Exception {
        JsonNode account = findQuestion("databases-l17-v01")
            .path("verification")
            .path("tables")
            .get(0);

        String suffix = UUID.randomUUID()
            .toString()
            .replace("-", "")
            .substring(0, 12);

        String tableName = "account_" + suffix;

        try (
            Connection first = openConnection();
            Connection second = openConnection()
        ) {
            try {
                createTable(first, account, tableName);
                insertRows(first, account, tableName);

                first.setTransactionIsolation(
                    Connection.TRANSACTION_READ_COMMITTED
                );
                first.setAutoCommit(false);

                int firstRead = ((Number) executeQuery(
                    first,
                    "SELECT balance FROM " + tableName + " WHERE id = 1"
                ).get(0).get(0)).intValue();

                assertEquals(100, firstRead);

                second.setAutoCommit(false);

                try (Statement statement = second.createStatement()) {
                    statement.executeUpdate(
                        "UPDATE " + tableName
                            + " SET balance = 150 WHERE id = 1"
                    );
                }

                second.commit();

                int secondRead = ((Number) executeQuery(
                    first,
                    "SELECT balance FROM " + tableName + " WHERE id = 1"
                ).get(0).get(0)).intValue();

                assertEquals(
                    150,
                    secondRead,
                    "READ_COMMITTED deve permitir observar a nova leitura"
                );

                first.rollback();
                first.setAutoCommit(true);

                try (Statement statement = first.createStatement()) {
                    statement.executeUpdate(
                        "UPDATE " + tableName
                            + " SET balance = 100 WHERE id = 1"
                    );
                }

                first.setAutoCommit(false);
                second.setAutoCommit(false);

                int firstSnapshot = ((Number) executeQuery(
                    first,
                    "SELECT balance FROM " + tableName + " WHERE id = 1"
                ).get(0).get(0)).intValue();

                int secondSnapshot = ((Number) executeQuery(
                    second,
                    "SELECT balance FROM " + tableName + " WHERE id = 1"
                ).get(0).get(0)).intValue();

                assertEquals(100, firstSnapshot);
                assertEquals(100, secondSnapshot);

                try (Statement statement = first.createStatement()) {
                    statement.executeUpdate(
                        "UPDATE " + tableName
                            + " SET balance = 110 WHERE id = 1"
                    );
                }

                first.commit();

                try (Statement statement = second.createStatement()) {
                    statement.executeUpdate(
                        "UPDATE " + tableName
                            + " SET balance = 120 WHERE id = 1"
                    );
                }

                second.commit();

                int finalBalance = ((Number) executeQuery(
                    first,
                    "SELECT balance FROM " + tableName + " WHERE id = 1"
                ).get(0).get(0)).intValue();

                assertEquals(
                    120,
                    finalBalance,
                    "A segunda gravação substitui a primeira"
                );
            } finally {
                if (!first.getAutoCommit()) {
                    first.rollback();
                    first.setAutoCommit(true);
                }

                if (!second.getAutoCommit()) {
                    second.rollback();
                    second.setAutoCommit(true);
                }

                dropTables(first, List.of(tableName));
            }
        }
    }

    @Test
    void declaredIndexesAreCreatedInMySql() throws Exception {
        assertIndexExists(
            "databases-l18-v01",
            "customer",
            "idx_customer_email"
        );

        assertIndexExists(
            "databases-l18-v02",
            "order_table",
            "idx_order_customer_created"
        );

        assertIndexExists(
            "databases-l18-v02",
            "order_table",
            "idx_order_created_customer"
        );

        assertIndexExists(
            "databases-l18-v02",
            "order_table",
            "idx_order_id"
        );
    }

    private static void assertIndexExists(
        String questionId,
        String originalTableName,
        String expectedIndex
    ) throws Exception {
        JsonNode question = findQuestion(questionId);
        JsonNode table = null;

        for (JsonNode candidate :
            question.path("verification").path("tables")) {
            if (originalTableName.equals(candidate.path("name").asText())) {
                table = candidate;
                break;
            }
        }

        assertNotNull(table, "Tabela não encontrada: " + originalTableName);

        String suffix = UUID.randomUUID()
            .toString()
            .replace("-", "")
            .substring(0, 12);

        String testTableName = originalTableName + "_" + suffix;

        try (Connection connection = openConnection()) {
            try {
                createTable(connection, table, testTableName);
                insertRows(connection, table, testTableName);

                List<List<Object>> indexes = executeQuery(
                    connection,
                    "SHOW INDEX FROM " + testTableName
                );

                boolean found = false;

                for (List<Object> index : indexes) {
                    if (expectedIndex.equals(String.valueOf(index.get(2)))) {
                        found = true;
                        break;
                    }
                }

                assertTrue(
                    found,
                    "Índice não criado: " + expectedIndex
                );
            } finally {
                dropTables(connection, List.of(testTableName));
            }
        }
    }

    @Test
    void denormalizedCourseNameProducesUpdateAnomaly() throws Exception {
        JsonNode table = findQuestion("databases-l15-v01")
            .path("verification")
            .path("tables")
            .get(0);

        String tableName = "student_course_" + randomSuffix();

        try (Connection connection = openConnection()) {
            try {
                createTable(connection, table, tableName);
                insertRows(connection, table, tableName);

                try (Statement statement = connection.createStatement()) {
                    int updated = statement.executeUpdate(
                        "UPDATE " + tableName
                            + " SET course_name = 'BD2'"
                            + " WHERE student_id = 1 AND course_id = 10"
                    );

                    assertEquals(1, updated);
                }

                List<List<Object>> inconsistentNames = executeQuery(
                    connection,
                    "SELECT COUNT(DISTINCT course_name) FROM "
                        + tableName
                        + " WHERE course_id = 10"
                );

                assertEquals(
                    List.of(List.of(2L)),
                    inconsistentNames,
                    "A redundância permite nomes inconsistentes"
                );
            } finally {
                dropTables(connection, List.of(tableName));
            }
        }
    }

    private static String randomSuffix() {
        return UUID.randomUUID()
            .toString()
            .replace("-", "")
            .substring(0, 12);
    }

    @Test
    void equivalentQueriesAndIndexPlanAreVerified() throws Exception {
        JsonNode equivalentQuestion = findQuestion("databases-l20-v02");
        JsonNode studentTable = equivalentQuestion
            .path("verification")
            .path("tables")
            .get(0);

        String studentName = "student_" + randomSuffix();

        try (Connection connection = openConnection()) {
            try {
                createTable(connection, studentTable, studentName);
                insertRows(connection, studentTable, studentName);

                Map<String, String> studentNames = Map.of(
                    "student",
                    studentName
                );

                String optionQuery = replaceTableNames(
                    equivalentQuestion
                        .path("options")
                        .get(0)
                        .path("text")
                        .asText(),
                    studentNames
                );

                String verificationQuery = replaceTableNames(
                    equivalentQuestion
                        .path("verification")
                        .path("query")
                        .asText(),
                    studentNames
                );

                assertTrue(
                    rowsEqual(
                        executeQuery(connection, optionQuery),
                        executeQuery(connection, verificationQuery)
                    ),
                    "As consultas equivalentes devem produzir o mesmo resultado"
                );

                JsonNode orderTable = findQuestion("databases-l18-v02")
                    .path("verification")
                    .path("tables")
                    .get(0);

                String orderName = "order_table_" + randomSuffix();

                try {
                    createTable(connection, orderTable, orderName);
                    insertRows(connection, orderTable, orderName);

                    String explain = explainQuery(
                        connection,
                        "SELECT id FROM " + orderName
                            + " USE INDEX (idx_order_customer_created)"
                            + " WHERE customer_id = 10"
                            + " ORDER BY created_at, id"
                    );

                    assertEquals(
                        "idx_order_customer_created",
                        explain,
                        "O plano deve usar o índice composto correto"
                    );
                } finally {
                    dropTables(connection, List.of(orderName));
                }
            } finally {
                dropTables(connection, List.of(studentName));
            }
        }
    }

    private static String explainQuery(
        Connection connection,
        String sql
    ) throws SQLException {
        try (
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("EXPLAIN " + sql)
        ) {
            ResultSetMetaData metadata = resultSet.getMetaData();
            int keyColumn = -1;

            for (int column = 1; column <= metadata.getColumnCount(); column++) {
                if ("key".equalsIgnoreCase(metadata.getColumnName(column))) {
                    keyColumn = column;
                    break;
                }
            }

            assertTrue(keyColumn > 0, "EXPLAIN não devolveu a coluna key");
            assertTrue(resultSet.next(), "EXPLAIN não devolveu linhas");

            return resultSet.getString(keyColumn);
        }
    }
}
