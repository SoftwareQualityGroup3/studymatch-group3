package pt.studymatch.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pt.studymatch.domain.CourseUnit;
import pt.studymatch.domain.GroupingContext;
import pt.studymatch.domain.Student;
import pt.studymatch.dto.GroupingContextCreateRequest;
import pt.studymatch.dto.GroupingContextResponse;
import pt.studymatch.dto.GroupingContextStudentsRequest;
import pt.studymatch.exception.CourseUnitNotFoundException;
import pt.studymatch.exception.DuplicateGroupingContextNameException;
import pt.studymatch.exception.GroupingContextNotFoundException;
import pt.studymatch.exception.StudentNotFoundException;
import pt.studymatch.repository.CourseUnitRepository;
import pt.studymatch.repository.GroupingContextRepository;
import pt.studymatch.repository.StudentRepository;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class GroupingContextService {

    private final GroupingContextRepository groupingContextRepository;
    private final StudentRepository studentRepository;
    private final CourseUnitRepository courseUnitRepository;

    public GroupingContextService(
        GroupingContextRepository groupingContextRepository,
        StudentRepository studentRepository,
        CourseUnitRepository courseUnitRepository
    ) {
        this.groupingContextRepository = groupingContextRepository;
        this.studentRepository = studentRepository;
        this.courseUnitRepository = courseUnitRepository;
    }

    @Transactional
    public GroupingContextResponse create(GroupingContextCreateRequest request) {
        String name = request.name().trim();

        if (groupingContextRepository.existsByName(name)) {
            throw new DuplicateGroupingContextNameException(name);
        }

        CourseUnit courseUnit = findCourseUnit(request.courseUnitCode());

        Set<Student> students = findEligibleStudents(request.studentIds());

        GroupingContext context = new GroupingContext(name, courseUnit);
        context.replaceEligibleStudents(students);

        return GroupingContextResponse.from(
            groupingContextRepository.save(context)
        );
    }

    @Transactional(readOnly = true)
    public List<GroupingContextResponse> findAll() {
        return groupingContextRepository.findAll().stream()
            .map(GroupingContextResponse::from)
            .toList();
    }

    @Transactional(readOnly = true)
    public GroupingContextResponse findById(Long id) {
        GroupingContext context = groupingContextRepository.findById(id)
            .orElseThrow(() -> new GroupingContextNotFoundException(id));

        return GroupingContextResponse.from(context);
    }

    @Transactional
    public GroupingContextResponse replaceStudents(
        Long id,
        GroupingContextStudentsRequest request
    ) {
        GroupingContext context = groupingContextRepository.findById(id)
            .orElseThrow(() -> new GroupingContextNotFoundException(id));

        Set<Student> students = findEligibleStudents(request.studentIds());
        context.replaceEligibleStudents(students);

        return GroupingContextResponse.from(context);
    }

    private CourseUnit findCourseUnit(String courseUnitCode) {
        if (courseUnitCode == null || courseUnitCode.isBlank()) {
            return null;
        }

        return courseUnitRepository.findById(courseUnitCode.trim())
            .orElseThrow(() ->
                new CourseUnitNotFoundException(courseUnitCode.trim())
            );
    }

    private Set<Student> findEligibleStudents(List<Long> studentIds) {
        if (studentIds == null || studentIds.isEmpty()) {
            throw new IllegalArgumentException("Student list must not be empty");
        }

        Set<Long> uniqueIds = new LinkedHashSet<>(studentIds);

        if (uniqueIds.size() != studentIds.size()) {
            throw new IllegalArgumentException(
                "Student ids must not contain repeated ids"
            );
        }

        Set<Student> students = new LinkedHashSet<>();

        for (Long studentId : studentIds) {
            Student student = studentRepository.findById(studentId)
                .orElseThrow(() ->
                    new StudentNotFoundException(studentId)
                );

            students.add(student);
        }

        return students;
    }
}
