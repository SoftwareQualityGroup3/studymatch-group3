package pt.studymatch.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.studymatch.domain.Competency;
import pt.studymatch.domain.CourseUnit;
import pt.studymatch.dto.CourseUnitResponse;
import pt.studymatch.repository.CourseUnitRepository;

// teste unitário puro do service sem spring e sem base de dados
// o repository é um mock por isso o teste é rápido
@ExtendWith(MockitoExtension.class)
class CourseUnitServiceTest {
    // mock do repository
    @Mock
    private CourseUnitRepository courseUnitRepository;
    // o injectmocks cria o service e mete lá o mock automaticamente
    @InjectMocks
    private CourseUnitService courseUnitService;

    // o mock devolve web antes de bd e o teste confirma que o service reordena por código
    // confirma também que os pesos passam corretamente para o dto
    @Test
    void returnsCourseUnitsWithWeightsOrderedByCode() {
        CourseUnit web = new CourseUnit("WEB", "Tecnologias Web", 6,
            Map.of(Competency.PROGRAMMING, 0.8, Competency.SOFTWARE_ENGINEERING, 0.2));
        CourseUnit bd = new CourseUnit("BD", "Bases de Dados", 6,
            Map.of(Competency.DATABASES, 1.0));
        when(courseUnitRepository.findAll()).thenReturn(List.of(web, bd));
        List<CourseUnitResponse> responses = courseUnitService.findAll();
        assertEquals(2, responses.size());
        assertEquals("BD", responses.getFirst().code());
        assertEquals("WEB", responses.getLast().code());
        assertEquals(0.8, responses.getLast().competencyWeights().get(Competency.PROGRAMMING));
        assertEquals(0.2, responses.getLast().competencyWeights().get(Competency.SOFTWARE_ENGINEERING));
    }

    // sem ucs devolve lista vazia e não dá erro
    @Test
    void returnsEmptyListWhenThereAreNoCourseUnits() {
        when(courseUnitRepository.findAll()).thenReturn(List.of());
        assertEquals(0, courseUnitService.findAll().size());
    }
}
