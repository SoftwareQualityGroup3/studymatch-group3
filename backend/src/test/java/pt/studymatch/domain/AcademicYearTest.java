package pt.studymatch.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class AcademicYearTest {

    @Test
    void acceptsConsecutiveYears() {
        assertEquals("2024/25", new AcademicYear("2024/25").value());
    }

    @Test
    void acceptsTheChangeOfCentury() {
        assertEquals("2099/00", new AcademicYear("2099/00").value());
    }

    @Test
    void ignoresSurroundingWhitespace() {
        assertEquals("2024/25", new AcademicYear(" 2024/25 ").value());
    }

    @Test
    void rejectsEndYearThatIsNotTheNextYear() {
        assertThrows(IllegalArgumentException.class, () -> new AcademicYear("2024/30"));
    }

    @Test
    void rejectsSameStartAndEndYear() {
        assertThrows(IllegalArgumentException.class, () -> new AcademicYear("2024/24"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"2024-25", "24/25", "2024/2025", "abcd/ef", "", "   "})
    void rejectsWrongFormat(String invalid) {
        assertThrows(IllegalArgumentException.class, () -> new AcademicYear(invalid));
    }

    @Test
    void rejectsNull() {
        assertThrows(IllegalArgumentException.class, () -> new AcademicYear(null));
    }
}
