package pt.studymatch.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pt.studymatch.domain.Student;

public interface StudentRepository extends JpaRepository<Student, Long> {

    boolean existsByNumber(String number);

    boolean existsByNumberAndIdNot(String number, Long id);

    @Query("SELECT s FROM Student s WHERE " +
        "(:course IS NULL OR s.course = :course) AND " +
        "(:q IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', :q, '%')) " +
        "OR LOWER(s.email) LIKE LOWER(CONCAT('%', :q, '%')) " +
        "OR LOWER(s.number) LIKE LOWER(CONCAT('%', :q, '%')))")
    List<Student> searchStudents(@Param("q") String q, @Param("course") String course);
}
