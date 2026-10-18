package pt.studymatch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.studymatch.domain.CourseUnit;

// repository das ucs
// o spring data gera a implementação sozinho e o findall e findbyid já vêm do jparepository
// a chave primaria é o código da uc que é uma string e por isso o segundo tipo é string
public interface CourseUnitRepository extends JpaRepository<CourseUnit, String> {
}
