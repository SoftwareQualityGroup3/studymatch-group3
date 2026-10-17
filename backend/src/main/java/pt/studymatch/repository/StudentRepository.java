package pt.studymatch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.studymatch.domain.Student;

public interface StudentRepository extends JpaRepository<Student, Long> {

    boolean existsByNumber(String number);

    boolean existsByNumberAndIdNot(String number, Long id);
}
