package pt.studymatch.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Pergunta do banco. Nos problemas de Programação as opções ficam a null. */
@Entity
@Table(name = "question")
public class Question {

    @Id
    @Column(name = "id", length = 60)
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(name = "competency", nullable = false, length = 50)
    private Competency competency;

    @Column(name = "level", nullable = false)
    private int level;

    @Column(name = "variant", nullable = false)
    private int variant;

    @Column(name = "statement", nullable = false, length = 4000)
    private String statement;

    @Column(name = "option_a", length = 1000)
    private String optionA;

    @Column(name = "option_b", length = 1000)
    private String optionB;

    @Column(name = "option_c", length = 1000)
    private String optionC;

    @Column(name = "option_d", length = 1000)
    private String optionD;

    @Column(name = "correct_option", length = 1)
    private String correctOption;

    protected Question() {
    }

    public Question(String id, Competency competency, int level, int variant, String statement,
                    String optionA, String optionB, String optionC, String optionD,
                    String correctOption) {
        this.id = id;
        this.competency = competency;
        this.level = level;
        this.variant = variant;
        this.statement = statement;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctOption = correctOption;
    }

    public String getId() {
        return id;
    }

    public Competency getCompetency() {
        return competency;
    }

    public int getLevel() {
        return level;
    }

    public int getVariant() {
        return variant;
    }

    public String getStatement() {
        return statement;
    }

    public String getOptionA() {
        return optionA;
    }

    public String getOptionB() {
        return optionB;
    }

    public String getOptionC() {
        return optionC;
    }

    public String getOptionD() {
        return optionD;
    }

    public String getCorrectOption() {
        return correctOption;
    }
}
