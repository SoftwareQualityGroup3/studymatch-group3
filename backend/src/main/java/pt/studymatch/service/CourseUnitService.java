package pt.studymatch.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.studymatch.dto.CourseUnitResponse;
import pt.studymatch.repository.CourseUnitRepository;
import java.util.Comparator;
import java.util.List;

// service das ucs
// é aqui que fica a lógica de negócio entre o controller e o repository
@Service
public class CourseUnitService {
    private final CourseUnitRepository courseUnitRepository;

    public CourseUnitService(CourseUnitRepository courseUnitRepository) {
        this.courseUnitRepository = courseUnitRepository;
    }

    // readonly porque só lemos e a base de dados pode otimizar
    @Transactional(readOnly = true)
    public List<CourseUnitResponse> findAll() {
        return courseUnitRepository.findAll()
            .stream()
            // converte cada entidade em dto
            .map(CourseUnitResponse::from)
            // ordena por código para a lista vir sempre pela mesma ordem
            // o findall não garante nenhuma ordem
            .sorted(Comparator.comparing(CourseUnitResponse::code))
            .toList();
    }
}
