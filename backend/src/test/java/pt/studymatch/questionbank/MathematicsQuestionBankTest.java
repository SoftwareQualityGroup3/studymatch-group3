package pt.studymatch.questionbank;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MathematicsQuestionBankTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static JsonNode root;

    @BeforeAll
    static void loadFile() throws Exception {
        try (InputStream input = MathematicsQuestionBankTest.class
            .getResourceAsStream("/question-bank/mathematics.json")) {

            assertNotNull(input, "mathematics.json não encontrado");
            root = MAPPER.readTree(input);
        }
    }

    @Test
    void hasTwentyLevelsWithTwoVariantsEach() {
        assertEquals("MATHEMATICS", root.path("competency").asText());

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
            assertEquals(
                String.format("mathematics-l%02d-v%02d", level, variant),
                id,
                "O ID não corresponde ao nível e à variante: " + id
            );
            assertTrue(level >= 1 && level <= 20, "Nível inválido em " + id);
            assertTrue(variant == 1 || variant == 2, "Variante inválida em " + id);
            assertEquals(
                "MATHEMATICS",
                question.path("competency").asText(),
                "Competência inválida em " + id
            );

            variantsPerLevel
                .computeIfAbsent(level, ignored -> new HashSet<>())
                .add(variant);
        }

        for (int level = 1; level <= 20; level++) {
            assertEquals(
                Set.of(1, 2),
                variantsPerLevel.get(level),
                "O nível " + level + " deve ter as variantes 1 e 2"
            );
        }
    }

    @Test
    void everyQuestionHasFourDistinctOptionsAndExactlyOneCorrect() {
        for (JsonNode question : root.path("questions")) {
            String id = question.path("id").asText();

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
            assertEquals(4, options.size(), "Número de opções inválido em " + id);

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
                    optionTexts.add(optionText.trim()),
                    "Opções com o mesmo texto em " + id
                );
            }

            assertEquals(
                Set.of("A", "B", "C", "D"),
                optionIds,
                "As opções devem ser A, B, C e D em " + id
            );

            JsonNode correct = question.path("correctOption");

            assertTrue(
                correct.isTextual() && correct.asText().matches("[A-D]"),
                "correctOption deve ser uma única letra A–D em " + id
            );
            assertTrue(
                optionIds.contains(correct.asText()),
                "correctOption não existe nas opções em " + id
            );
        }
    }

    @Test
    void topicsMatchTheLevelRanges() {
        for (JsonNode question : root.path("questions")) {
            String id = question.path("id").asText();
            int level = question.path("level").asInt();
            String type = question.path("type").asText();

            String allowed;

            if (level <= 5) {
                allowed = "ALGEBRA|FUNCTIONS";
            } else if (level <= 10) {
                allowed = "DERIVATIVES|SYSTEMS";
            } else if (level <= 15) {
                allowed = "INTEGRALS|DETERMINANTS|EIGENVALUES";
            } else {
                allowed = "SERIES|LIMITS|MATRICES";
            }

            assertTrue(
                type.matches(allowed),
                "Tema '" + type + "' fora da faixa do nível " + level + " em " + id
            );
        }
    }
}
