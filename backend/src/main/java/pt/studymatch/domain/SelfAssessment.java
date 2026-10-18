package pt.studymatch.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "self_assessment",

    // Garante que cada estudante só tem uma autoavaliação
    // por competência.
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"student_id", "competency"})
    }
)
public class SelfAssessment {

    // Identificador único da autoavaliação.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Estudante a quem pertence esta autoavaliação.
    @ManyToOne(optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    // Competência que está a ser autoavaliada.
    // Exemplo: PROGRAMMING ou MATHEMATICS.
    @Enumerated(EnumType.STRING)
    @Column(name = "competency", nullable = false)
    private Competency competency;

    // Valor atribuído pelo estudante.
    // Só pode estar entre 1 e 5.
    @Column(name = "value", nullable = false)
    private int value;

    // Construtor vazio obrigatório para o JPA.
    protected SelfAssessment() {
    }

    // Cria uma nova autoavaliação.
    public SelfAssessment(
        Student student,
        Competency competency,
        int value) {

        // A autoavaliação tem obrigatoriamente
        // de pertencer a um estudante.
        if (student == null) {
            throw new IllegalArgumentException(
                "Student is required");
        }

        // É obrigatório indicar a competência
        // que está a ser avaliada.
        if (competency == null) {
            throw new IllegalArgumentException(
                "Competency is required");
        }

        // Confirma se o valor está entre 1 e 5.
        validateValue(value);

        this.student = student;
        this.competency = competency;
        this.value = value;
    }

    // Permite alterar uma autoavaliação existente.
    // Por exemplo, o estudante tinha 3 em PROGRAMMING
    // e depois atualiza para 4.
    public void updateValue(int value) {
        validateValue(value);
        this.value = value;
    }

    // Método usado para garantir que a autoavaliação
    // está sempre dentro do intervalo permitido.
    private void validateValue(int value) {
        if (value < 1 || value > 5) {
            throw new IllegalArgumentException(
                "Self-assessment value must be between 1 and 5");
        }
    }

    public Long getId() {
        return id;
    }

    public Student getStudent() {
        return student;
    }

    public Competency getCompetency() {
        return competency;
    }

    public int getValue() {
        return value;
    }
}
