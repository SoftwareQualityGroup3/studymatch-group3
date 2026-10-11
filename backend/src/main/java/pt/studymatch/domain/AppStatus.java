package pt.studymatch.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entidade técnica que representa a linha de estado da aplicação.
 * Serve apenas para validar a ligação à base de dados (health check) e
 * não faz parte do modelo de domínio do StudyMatch.
 */
@Entity
@Table(name = "app_status")
public class AppStatus {

    @Id
    private Integer id;

    @Column(nullable = false, length = 20)
    private String status;

    protected AppStatus() {
        // exigido pelo JPA
    }

    public Integer getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }
}
