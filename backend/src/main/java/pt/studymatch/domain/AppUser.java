package pt.studymatch.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "app_user")
public class AppUser {

    static final int USERNAME_MAX_LENGTH = 100;
    static final int PASSWORD_HASH_MAX_LENGTH = 100;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, unique = true, length = USERNAME_MAX_LENGTH)
    private String username;

    @Column(name = "password_hash", nullable = false, length = PASSWORD_HASH_MAX_LENGTH)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 30)
    private Role role;

    @OneToOne
    @JoinColumn(name = "student_id")
    private Student student;

    protected AppUser() {
        // exigido pelo JPA
    }

    /**
     * @param passwordHash hash BCrypt da password (nunca a password em texto)
     * @param student      estudante associado; opcional
     */
    public AppUser(String username, String passwordHash, Role role, Student student) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("username is required");
        }
        if (username.trim().length() > USERNAME_MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "username must have at most " + USERNAME_MAX_LENGTH + " characters");
        }
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new IllegalArgumentException("passwordHash is required");
        }
        if (passwordHash.length() > PASSWORD_HASH_MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "passwordHash must have at most " + PASSWORD_HASH_MAX_LENGTH + " characters");
        }
        if (role == null) {
            throw new IllegalArgumentException("role is required");
        }

        this.username = username.trim();
        this.passwordHash = passwordHash;
        this.role = role;
        this.student = student;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public Role getRole() {
        return role;
    }

    public Student getStudent() {
        return student;
    }
}
