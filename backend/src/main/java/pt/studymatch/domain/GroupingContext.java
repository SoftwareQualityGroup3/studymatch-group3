package pt.studymatch.domain;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "grouping_context")
public class GroupingContext {

    static final int NAME_MAX_LENGTH = 150;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = NAME_MAX_LENGTH)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_unit_code")
    private CourseUnit courseUnit;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @ManyToMany
    @JoinTable(
        name = "grouping_context_student",
        joinColumns = @JoinColumn(name = "grouping_context_id"),
        inverseJoinColumns = @JoinColumn(name = "student_id")
    )
    private Set<Student> eligibleStudents = new LinkedHashSet<>();

    protected GroupingContext() {
        // Exigido pelo JPA
    }

    public GroupingContext(String name, CourseUnit courseUnit) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name is required");
        }

        String trimmedName = name.trim();

        if (trimmedName.length() > NAME_MAX_LENGTH) {
            throw new IllegalArgumentException(
                "Name must have at most " + NAME_MAX_LENGTH + " characters"
            );
        }

        this.name = trimmedName;
        this.courseUnit = courseUnit;
    }

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    public void replaceEligibleStudents(Set<Student> students) {
        eligibleStudents.clear();
        eligibleStudents.addAll(students);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public CourseUnit getCourseUnit() {
        return courseUnit;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Set<Student> getEligibleStudents() {
        return eligibleStudents;
    }
}
