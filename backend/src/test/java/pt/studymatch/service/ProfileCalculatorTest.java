package pt.studymatch.service;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import pt.studymatch.domain.AcademicRecord;
import pt.studymatch.domain.AcademicYear;
import pt.studymatch.domain.Competency;
import pt.studymatch.domain.CourseUnit;
import pt.studymatch.domain.Student;
import pt.studymatch.domain.StudentProfile;

class ProfileCalculatorTest {

    private final ProfileCalculator calculator = new ProfileCalculator();

    @Test
    void calculatesWeightedScoreForTwoCourseUnits() {
        Student student = createStudent();

        // Cria duas unidades curriculares com nota e peso
        AcademicRecord firstRecord = createRecord(student, "UC1", 10, Map.of(Competency.PROGRAMMING, 0.25));

        AcademicRecord secondRecord = createRecord(student, "UC2", 18, Map.of(Competency.PROGRAMMING, 0.75));

        // Calcula o perfil com base nos dois resultados academicos
        StudentProfile profile = calculator.calculate(List.of(firstRecord, secondRecord));

        // Obtém o resultado da competência de programação
        StudentProfile.CompetencyResult result = profile.getProfile(Competency.PROGRAMMING);

        //verifica se a nota calculada (result.score()) é igual a 16.0, aceitando uma pequena margem de erro de 0.001
        assertEquals(16.0, result.score(), 0.001);

        //verifica se o número de resultados académicos utilizados no cálculo do perfil é igual a 2
        assertEquals(2, result.evidenceCount());
    }

    @Test
    void ignoresZeroWeightAndUsesHigherWeightsMoreStrongly() {
        Student student = createStudent();

        AcademicRecord zeroWeightRecord = createRecord(student, "UC_ZERO", 20, Map.of(Competency.PROGRAMMING, 0.0));

        AcademicRecord relevantRecord = createRecord(student, "UC_RELEVANT", 10, Map.of(Competency.PROGRAMMING, 1.0));

        StudentProfile profile = calculator.calculate(List.of(zeroWeightRecord, relevantRecord));

        StudentProfile.CompetencyResult result = profile.getProfile(Competency.PROGRAMMING);

        // Confirma que a nota com peso zero não influencia a média
        assertEquals(10.0, result.score(), 0.001);

        // Confirma que apenas um resultado foi considerado como evidência
        assertEquals(1, result.evidenceCount());
    }

    @Test
    void returnsNullForAllCompetenciesWhenThereAreNoResults() {
        StudentProfile profile =
            calculator.calculate(List.of());

        // Confirma que todas as competências ficam sem nota
        assertAll(
            // A programação não tem nota atribuída
            () -> assertNull(
                profile.getProfile(Competency.PROGRAMMING).score()),

            // A matemática não tem nota atribuída
            () -> assertNull(
                profile.getProfile(Competency.MATHEMATICS).score()),

            // As bases de dados não têm nota atribuída
            () -> assertNull(
                profile.getProfile(Competency.DATABASES).score()),

            // A engenharia de software não tem nota atribuída
            () -> assertNull(
                profile.getProfile(Competency.SOFTWARE_ENGINEERING).score()));
    }

    @Test
    void keepsUnrelatedCompetenciesNull() {
        Student student = createStudent();

        AcademicRecord programmingRecord = createRecord(student, "UC_PROGRAMMING", 15, Map.of(Competency.PROGRAMMING, 1.0));

        StudentProfile profile = calculator.calculate(List.of(programmingRecord));

        // Confirma que a nota de programação é 15
        assertEquals(15.0, profile.getProfile(Competency.PROGRAMMING).score(), 0.001);

        // Confirma que a matemática continua sem nota
        assertNull(profile.getProfile(Competency.MATHEMATICS).score());

        // Confirma que as bases de dados continuam sem nota
        assertNull(profile.getProfile(Competency.DATABASES).score());

        // Confirma que a engenharia de software continua sem nota
        assertNull(profile.getProfile(Competency.SOFTWARE_ENGINEERING).score());
    }

    @Test
    void returnsTwentyWhenAllGradesAreTwenty() {
        Student student = createStudent();

        AcademicRecord firstRecord = createRecord(student, "UC1", 20, Map.of(Competency.PROGRAMMING, 0.25));

        AcademicRecord secondRecord = createRecord(student, "UC2", 20, Map.of(Competency.PROGRAMMING, 0.75));

        StudentProfile profile = calculator.calculate(List.of(firstRecord, secondRecord));

        StudentProfile.CompetencyResult result = profile.getProfile(Competency.PROGRAMMING);

        // Confirma que a média ponderada continua a ser 20
        assertEquals(20.0, result.score(), 0.001);

        // Confirma que os dois resultados contam como evidência
        assertEquals(2, result.evidenceCount());
    }

    @Test
    void returnsZeroWhenAllGradesAreZero() {
        Student student = createStudent();

        AcademicRecord firstRecord = createRecord(student, "UC1", 0, Map.of(Competency.PROGRAMMING, 0.25));

        AcademicRecord secondRecord = createRecord(student, "UC2", 0, Map.of(Competency.PROGRAMMING, 0.75));

        StudentProfile profile = calculator.calculate(List.of(firstRecord, secondRecord));

        StudentProfile.CompetencyResult result = profile.getProfile(Competency.PROGRAMMING);

        // Confirma que a média ponderada é zero
        assertEquals(0.0, result.score(), 0.001);

        // Confirma que os dois resultados contam como evidência
        assertEquals(2, result.evidenceCount());
    }

    @Test
    void recalculatesProfileWhenANewResultIsAdded() {
        Student student = createStudent();

        AcademicRecord firstRecord =
            createRecord(
                student,
                "UC1",
                10,
                Map.of(Competency.PROGRAMMING, 1.0));

        StudentProfile initialProfile = calculator.calculate(List.of(firstRecord));

        AcademicRecord secondRecord = createRecord(student, "UC2", 20, Map.of(Competency.PROGRAMMING, 1.0));

        StudentProfile updatedProfile = calculator.calculate(List.of(firstRecord, secondRecord));

        // Confirma que o perfil inicial mantém a nota 10
        assertEquals(10.0, initialProfile.getProfile(Competency.PROGRAMMING).score(), 0.001);

        // Faz o mesmo que a linha anterior mas para nota 15 e para o outro perfil
        assertEquals(15.0, updatedProfile.getProfile(Competency.PROGRAMMING).score(), 0.001);

        // Confirma que o perfil atualizado utiliza dois resultados.
        assertEquals(2, updatedProfile.getProfile(Competency.PROGRAMMING).evidenceCount());
    }

    @Test
    void rejectsGradeBelowZero() {
        Student student = createStudent();

        // Confirma que uma nota negativa provoca uma exceção
        assertThrows(
            IllegalArgumentException.class,
            () -> createRecord(
                student,
                "UC_INVALID",
                -1,
                Map.of(Competency.PROGRAMMING, 1.0)));
    }

    @Test
    void rejectsGradeAboveTwenty() {
        Student student = createStudent();

        // Confirma que uma nota superior a 20 provoca uma exceção
        assertThrows(
            IllegalArgumentException.class,
            () -> createRecord(
                student,
                "UC_INVALID",
                21,
                Map.of(Competency.PROGRAMMING, 1.0)));
    }

    @Test
    void rejectsCourseUnitWithoutCompetencyWeights() {
        Student student = createStudent();

        AcademicRecord record = createRecord(student, "UC_WITHOUT_WEIGHTS", 12, Map.of());

        // Confirma que calcular o perfil provoca uma exceção
        assertThrows(
            IllegalArgumentException.class,
            () -> calculator.calculate(List.of(record)));
    }

    private Student createStudent() {
        return new Student("51732", "Joao Xavier", "joao@example.com", "Engenharia Informatica", 3);
    }

    // Cria um resultado académico com a unidade, nota e pesos indicados
    private AcademicRecord createRecord(Student student, String courseUnitCode, int grade, Map<Competency, Double> weights) {

        CourseUnit courseUnit = new CourseUnit(courseUnitCode, "Course Unit " + courseUnitCode, 6, weights);

        return new AcademicRecord(student, courseUnit, new AcademicYear("2026/27"), 1, BigDecimal.valueOf(grade));
    }
}
