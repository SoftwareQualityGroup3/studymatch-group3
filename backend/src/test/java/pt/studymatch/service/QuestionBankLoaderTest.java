package pt.studymatch.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import pt.studymatch.domain.Question;
import pt.studymatch.repository.QuestionRepository;
import tools.jackson.databind.json.JsonMapper;

class QuestionBankLoaderTest {

    private final QuestionRepository repository = mock(QuestionRepository.class);

    private final QuestionBankLoader loader =
        new QuestionBankLoader(repository, JsonMapper.builder().build());

    @Test
    void savesTheQuestionsFromTheBank() {
        loader.load();

        // databases.json tem 40 perguntas; os outros bancos vão aumentar o número
        verify(repository, atLeast(40)).save(any(Question.class));
    }

    @Test
    void skipsQuestionsThatAlreadyExist() {
        when(repository.existsById(anyString())).thenReturn(true);

        loader.load();

        verify(repository, never()).save(any(Question.class));
    }
}
