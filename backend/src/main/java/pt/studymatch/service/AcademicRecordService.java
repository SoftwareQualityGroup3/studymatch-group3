package pt.studymatch.service;

import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.studymatch.domain.AcademicRecord;
import pt.studymatch.domain.AcademicYear;
import pt.studymatch.domain.CourseUnit;
import pt.studymatch.domain.Student;
import pt.studymatch.dto.AcademicRecordRequest;
import pt.studymatch.dto.AcademicRecordResponse;
import pt.studymatch.exception.CourseUnitNotFoundException;
import pt.studymatch.exception.DuplicateAcademicRecordException;
import pt.studymatch.exception.StudentNotFoundException;
import pt.studymatch.repository.AcademicRecordRepository;
import pt.studymatch.repository.CourseUnitRepository;
import pt.studymatch.repository.StudentRepository;

// service dos registos académicos com as regras de negócio principais
// precisa de três repositories porque um registo liga um aluno a uma uc
@Service
public class AcademicRecordService {
    private final AcademicRecordRepository academicRecordRepository;
    private final StudentRepository studentRepository;
    private final CourseUnitRepository courseUnitRepository;

    public AcademicRecordService(
        AcademicRecordRepository academicRecordRepository,
        StudentRepository studentRepository,
        CourseUnitRepository courseUnitRepository) {
        this.academicRecordRepository = academicRecordRepository;
        this.studentRepository = studentRepository;
        this.courseUnitRepository = courseUnitRepository;
    }

    // transacional porque faz leituras e uma escrita
    // se algo falhar a meio não fica nada guardado
    @Transactional
    public AcademicRecordResponse create(Long studentId, AcademicRecordRequest request) {
        // 1 verifica se o aluno existe senão lança 404
        Student student = studentRepository.findById(studentId)
            .orElseThrow(() ->
                new StudentNotFoundException(studentId));
        // 2 tira os espaços do código para não falhar a procura por causa de um espaço a mais
        String courseUnitCode = request.courseUnitCode().trim();
        CourseUnit courseUnit = courseUnitRepository.findById(courseUnitCode)
            .orElseThrow(() ->
                new CourseUnitNotFoundException(courseUnitCode));
        // 3 não deixa criar dois registos da mesma uc para o mesmo aluno
        // a base de dados também tem uma unique constraint mas assim damos um erro mais claro
        if (academicRecordRepository.existsByStudent_IdAndCourseUnit_Code(studentId, courseUnitCode)) {
            throw new DuplicateAcademicRecordException(studentId, courseUnitCode);
        }

        // 4 cria a entidade e é no construtor que se valida a nota e o semestre
        // o academicyear também valida o formato e se o segundo ano é o seguinte
        AcademicRecord record = new AcademicRecord(
            student,
            courseUnit,
            new AcademicYear(request.academicYear()),
            request.semester(),
            request.grade()
        );
        // guarda e devolve já com o id gerado pela base de dados
        AcademicRecord savedRecord = academicRecordRepository.save(record);
        return AcademicRecordResponse.from(savedRecord);
    }

    // lista os registos de um aluno ordenados por ano letivo e depois semestre e depois nome da uc
    // o existsbyid é mais leve do que carregar o aluno todo só para ver se existe
    @Transactional(readOnly = true)
    public List<AcademicRecordResponse> findByStudentId(Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new StudentNotFoundException(studentId);
        }

        return academicRecordRepository.findByStudent_Id(studentId)
            .stream()
            // ordenação em cadeia com comparing e thencomparing
            .sorted(Comparator
                .comparing((AcademicRecord record) -> record.getAcademicYear().value())
                .thenComparingInt(AcademicRecord::getSemester)
                .thenComparing(record -> record.getCourseUnit().getName()))
            .map(AcademicRecordResponse::from)
            .toList();
    }
}
