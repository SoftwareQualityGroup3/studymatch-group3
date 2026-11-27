package pt.studymatch.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.List;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pt.studymatch.domain.Competency;
import pt.studymatch.domain.Question;
import pt.studymatch.repository.QuestionRepository;
import tools.jackson.databind.ObjectMapper;

/**
 * Carrega question-bank/*.json para a tabela question no arranque.
 * Perguntas que já existem são ignoradas.
 */
@Component
public class QuestionBankLoader implements ApplicationRunner {

    private static final String BANK_FILES = "classpath*:question-bank/*.json";

    private final QuestionRepository questionRepository;
    private final ObjectMapper objectMapper;

    public QuestionBankLoader(QuestionRepository questionRepository, ObjectMapper objectMapper) {
        this.questionRepository = questionRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        load();
    }

    void load() {
        try {
            Resource[] files = new PathMatchingResourcePatternResolver().getResources(BANK_FILES);
            for (Resource file : files) {
                loadFile(file);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Could not read the question bank", e);
        }
    }

    private void loadFile(Resource file) throws IOException {
        try (InputStream input = file.getInputStream()) {
            BankFile bank = objectMapper.readValue(input, BankFile.class);

            for (BankQuestion question : bank.questions()) {
                if (!questionRepository.existsById(question.id())) {
                    questionRepository.save(toEntity(question));
                }
            }
        }
    }

    private Question toEntity(BankQuestion question) {
        return new Question(
            question.id(),
            question.competency(),
            question.level(),
            question.variant(),
            question.statement(),
            optionText(question, "A"),
            optionText(question, "B"),
            optionText(question, "C"),
            optionText(question, "D"),
            question.correctOption()
        );
    }

    // Devolve null nos problemas sem opções (Programação).
    private String optionText(BankQuestion question, String optionId) {
        if (question.options() == null) {
            return null;
        }
        return question.options().stream()
            .filter(option -> option.id().equals(optionId))
            .map(BankOption::text)
            .findFirst()
            .orElse(null);
    }

    // Formato dos ficheiros; campos extra (explanation, verification) são ignorados.
    @JsonIgnoreProperties(ignoreUnknown = true)
    record BankFile(List<BankQuestion> questions) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record BankQuestion(String id, Competency competency, int level, int variant,
                        String statement, List<BankOption> options, String correctOption) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record BankOption(String id, String text) {
    }
}
