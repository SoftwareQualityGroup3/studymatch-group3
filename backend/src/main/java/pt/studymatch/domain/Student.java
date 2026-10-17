package pt.studymatch.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "student")
public class Student {

    public static final int MIN_CURRICULAR_YEAR = 1;
    public static final int MAX_CURRICULAR_YEAR = 5;

    static final int NUMBER_MAX_LENGTH = 20;
    static final int NAME_MAX_LENGTH = 150;
    static final int EMAIL_MAX_LENGTH = 255;
    static final int COURSE_MAX_LENGTH = 100;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "number", nullable = false, unique = true, length = NUMBER_MAX_LENGTH)
    private String number;

    @Column(name = "name", nullable = false, length = NAME_MAX_LENGTH)
    private String name;

    @Column(name = "email", length = EMAIL_MAX_LENGTH)
    private String email;

    @Column(name = "course", length = COURSE_MAX_LENGTH)
    private String course;

    @Column(name = "curricular_year", nullable = false)
    private int curricularYear;

    protected Student() {
        // exigido pelo JPA
    }

    public Student(String number, String name, String email, String course, int curricularYear) {
        apply(number, name, email, course, curricularYear);
    }

    public void update(String number, String name, String email, String course, int curricularYear) {
        apply(number, name, email, course, curricularYear);
    }

    private void apply(String number, String name, String email, String course, int curricularYear) {
        String validNumber = requiredText("number", number, NUMBER_MAX_LENGTH);
        String validName = requiredText("name", name, NAME_MAX_LENGTH);
        String validEmail = optionalText("email", email, EMAIL_MAX_LENGTH);
        String validCourse = optionalText("course", course, COURSE_MAX_LENGTH);

        if (curricularYear < MIN_CURRICULAR_YEAR || curricularYear > MAX_CURRICULAR_YEAR) {
            throw new IllegalArgumentException("curricularYear must be between "
                    + MIN_CURRICULAR_YEAR + " and " + MAX_CURRICULAR_YEAR);
        }

        this.number = validNumber;
        this.name = validName;
        this.email = validEmail;
        this.course = validCourse;
        this.curricularYear = curricularYear;
    }

    private static String requiredText(String field, String value, int maxLength) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required");
        }
        return checkLength(field, value.trim(), maxLength);
    }

    private static String optionalText(String field, String value, int maxLength) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return checkLength(field, value.trim(), maxLength);
    }

    private static String checkLength(String field, String value, int maxLength) {
        if (value.length() > maxLength) {
            throw new IllegalArgumentException(field + " must have at most " + maxLength + " characters");
        }
        return value;
    }

    public Long getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getCourse() {
        return course;
    }

    public int getCurricularYear() {
        return curricularYear;
    }
}
