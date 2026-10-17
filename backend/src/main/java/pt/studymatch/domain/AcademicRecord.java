package pt.studymatch.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Table(name = "academic_record", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"student_id", "course_unit_code"})
})
public class AcademicRecord {

    public static final BigDecimal MIN_GRADE = BigDecimal.valueOf(0);
    public static final BigDecimal MAX_GRADE = BigDecimal.valueOf(20);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(optional = false)
    @JoinColumn(name = "course_unit_code", referencedColumnName = "code", nullable = false)
    private CourseUnit courseUnit;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "academic_year", nullable = false, length = 10))
    private AcademicYear academicYear;

    @Column(name = "semester", nullable = false)
    private int semester;

    @Column(name = "grade", nullable = false, precision = 4, scale = 2)
    private BigDecimal grade;

    protected AcademicRecord() {
        // Exigido pelo JPA
    }

    public AcademicRecord(Student student, CourseUnit courseUnit, AcademicYear academicYear, int semester, BigDecimal grade) {
        apply(student, courseUnit, academicYear, semester, grade);
    }

    public void update(AcademicYear academicYear, int semester, BigDecimal grade) {
        apply(this.student, this.courseUnit, academicYear, semester, grade);
    }

    private void apply(Student student, CourseUnit courseUnit, AcademicYear academicYear, int semester, BigDecimal grade) {
        if (student == null) {
            throw new IllegalArgumentException("Student is required");
        }
        if (courseUnit == null) {
            throw new IllegalArgumentException("Course unit is required");
        }
        if (academicYear == null) {
            throw new IllegalArgumentException("Academic year is required");
        }
        if (semester != 1 && semester != 2) {
            throw new IllegalArgumentException("Semester must be 1 or 2");
        }
        if (grade == null) {
            throw new IllegalArgumentException("Grade is required");
        }

        BigDecimal normalizedGrade = grade.setScale(2, RoundingMode.HALF_UP);
        if (normalizedGrade.compareTo(MIN_GRADE) < 0 || normalizedGrade.compareTo(MAX_GRADE) > 0) {
            throw new IllegalArgumentException("Grade must be between 0 and 20");
        }

        this.student = student;
        this.courseUnit = courseUnit;
        this.academicYear = academicYear;
        this.semester = semester;
        this.grade = normalizedGrade;
    }

    public Long getId() {
        return id;
    }

    public Student getStudent() {
        return student;
    }

    public CourseUnit getCourseUnit() {
        return courseUnit;
    }

    public AcademicYear getAcademicYear() {
        return academicYear;
    }

    public int getSemester() {
        return semester;
    }

    public BigDecimal getGrade() {
        return grade;
    }
}
