package pt.studymatch.service;

import org.springframework.stereotype.Service;
import pt.studymatch.domain.AcademicRecord;
import pt.studymatch.domain.StudentProfile;
import pt.studymatch.exception.StudentNotFoundException; // <-- Importa a vossa exceção
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
            // Lança a StudentNotFoundException que já tem o @ResponseStatus(HttpStatus.NOT_FOUND)
            throw new StudentNotFoundException(studentId);
        }

        List<AcademicRecord> records = academicRecordRepository.findByStudentId(studentId);
        return profileCalculator.calculate(records);
    }
}
