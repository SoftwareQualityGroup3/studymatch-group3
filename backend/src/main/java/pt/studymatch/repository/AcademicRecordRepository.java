package pt.studymatch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.studymatch.domain.AcademicRecord;
import java.util.List;

public interface AcademicRecordRepository extends JpaRepository<AcademicRecord, Long> {
    List<AcademicRecord> findByStudentId(Long studentId);
}
