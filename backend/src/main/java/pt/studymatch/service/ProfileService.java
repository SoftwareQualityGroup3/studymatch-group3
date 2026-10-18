package pt.studymatch.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import pt.studymatch.domain.AcademicRecord;
import pt.studymatch.domain.StudentProfile;
import pt.studymatch.repository.AcademicRecordRepository;
import pt.studymatch.repository.StudentRepository;

import java.util.List;

@Service
public class ProfileService {

    private final StudentRepository studentRepository;
    private final AcademicRecordRepository academicRecordRepository;
    private final ProfileCalculator profileCalculator;

    public ProfileService(StudentRepository studentRepository,
                          AcademicRecordRepository academicRecordRepository,
                          ProfileCalculator profileCalculator) {
        this.studentRepository = studentRepository;
        this.academicRecordRepository = academicRecordRepository;
        this.profileCalculator = profileCalculator;
    }

    public StudentProfile getProfileForStudent(Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new EntityNotFoundException("Student not found with id: " + studentId);
        }

        List<AcademicRecord> records = academicRecordRepository.findByStudentId(studentId);
        return profileCalculator.calculate(records);
    }
}
