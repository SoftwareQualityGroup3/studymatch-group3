package pt.studymatch.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import pt.studymatch.domain.AcademicRecord;
import pt.studymatch.domain.AcademicYear;
import pt.studymatch.domain.Competency;
import pt.studymatch.domain.CourseUnit;
import pt.studymatch.domain.ProfileSource;
import pt.studymatch.domain.SelfAssessment;
import pt.studymatch.domain.Student;
import pt.studymatch.domain.StudentProfile;

class ProfileCalculatorColdStartTest {

    private final ProfileCalculator calculator =
        new ProfileCalculator();

    /*
     * Sem resultados académicos:
     * a pontuação vem apenas da autoavaliação.
     *
     * Autoavaliação 4 -> 16 pontos.
     */
    @Test
    void usesSelfAssessmentWhenThereIsNoAcademicEvidence() {

        Student student = createStudent();

        SelfAssessment selfAssessment =
            new SelfAssessment(
                student,
                Competency.PROGRAMMING,
                4);

        StudentProfile profile =
            calculator.calculate(
                List.of(),
                List.of(selfAssessment));

        StudentProfile.CompetencyResult result =
            profile.getProfile(Competency.PROGRAMMING);

        assertEquals(16.0, result.score(), 0.001);
        assertEquals(0, result.evidenceCount());
        assertEquals(
            ProfileSource.SELF_ASSESSMENT,
            result.source());
    }

    /*
     * Uma evidência académica com nota 12
     * e autoavaliação 4 -> 16.
     *
     * academicWeight = 1 / 1.5
     *
     * resultado ~= 13.33
     */
    @Test
    void mixesAcademicScoreAndSelfAssessmentWithOneEvidence() {

        Student student = createStudent();

        AcademicRecord record =
            createProgrammingRecord(
                student,
                "UC1",
                12);

        SelfAssessment selfAssessment =
            new SelfAssessment(
                student,
                Competency.PROGRAMMING,
                4);

        StudentProfile profile =
            calculator.calculate(
                List.of(record),
                List.of(selfAssessment));

        StudentProfile.CompetencyResult result =
            profile.getProfile(Competency.PROGRAMMING);

        assertEquals(13.3333, result.score(), 0.001);
        assertEquals(1, result.evidenceCount());
        assertEquals(
            ProfileSource.MIXED,
            result.source());
    }

    /*
     * Três evidências académicas, todas com nota 12,
     * e a mesma autoavaliação 4 -> 16.
     *
     * Com mais evidências, a influência da
     * autoavaliação diminui.
     *
     * resultado ~= 12.57
     */
    @Test
    void selfAssessmentHasLessInfluenceWithThreeEvidences() {

        Student student = createStudent();

        AcademicRecord record1 =
            createProgrammingRecord(
                student,
                "UC1",
                12);

        AcademicRecord record2 =
            createProgrammingRecord(
                student,
                "UC2",
                12);

        AcademicRecord record3 =
            createProgrammingRecord(
                student,
                "UC3",
                12);

        SelfAssessment selfAssessment =
            new SelfAssessment(
                student,
                Competency.PROGRAMMING,
                4);

        StudentProfile profile =
            calculator.calculate(
                List.of(
                    record1,
                    record2,
                    record3),
                List.of(selfAssessment));

        StudentProfile.CompetencyResult result =
            profile.getProfile(Competency.PROGRAMMING);

        assertEquals(12.5714, result.score(), 0.001);
        assertEquals(3, result.evidenceCount());
        assertEquals(
            ProfileSource.MIXED,
            result.source());
    }

    /*
     * Sem autoavaliação deve continuar
     * o comportamento original do S2-07.
     */
    @Test
    void keepsAcademicBehaviourWithoutSelfAssessment() {

        Student student = createStudent();

        AcademicRecord record =
            createProgrammingRecord(
                student,
                "UC1",
                12);

        StudentProfile profile =
            calculator.calculate(
                List.of(record));

        StudentProfile.CompetencyResult result =
            profile.getProfile(Competency.PROGRAMMING);

        assertEquals(12.0, result.score(), 0.001);
        assertEquals(1, result.evidenceCount());
        assertEquals(
            ProfileSource.ACADEMIC,
            result.source());
    }

    private Student createStudent() {
        return new Student(
            "51732",
            "Joao Xavier",
            "joao@example.com",
            "Engenharia Informatica",
            3);
    }

    private AcademicRecord createProgrammingRecord(
        Student student,
        String courseUnitCode,
        int grade) {

        CourseUnit courseUnit =
            new CourseUnit(
                courseUnitCode,
                "Course Unit " + courseUnitCode,
                6,
                Map.of(
                    Competency.PROGRAMMING,
                    1.0));

        return new AcademicRecord(
            student,
            courseUnit,
            new AcademicYear("2026/27"),
            1,
            BigDecimal.valueOf(grade));
    }
}
