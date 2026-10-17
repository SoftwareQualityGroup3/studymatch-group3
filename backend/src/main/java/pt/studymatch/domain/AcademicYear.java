package pt.studymatch.domain;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Ano letivo no formato YYYY/YY, em que o segundo número é o ano seguinte
 * (por exemplo, 2024/25). Exemplos inválidos: 2024/30, 2024-25, 24/25.
 */
public record AcademicYear(String value) {

    private static final Pattern FORMAT = Pattern.compile("(\\d{4})/(\\d{2})");

    public AcademicYear {
        if (value == null) {
            throw new IllegalArgumentException("Academic year is required");
        }

        String trimmed = value.trim();
        Matcher matcher = FORMAT.matcher(trimmed);
        if (!matcher.matches()) {
            throw new IllegalArgumentException(
                    "Academic year must have the format YYYY/YY, but was '" + value + "'");
        }

        int startYear = Integer.parseInt(matcher.group(1));
        int endYear = Integer.parseInt(matcher.group(2));
        if (endYear != (startYear + 1) % 100) {
            throw new IllegalArgumentException(
                    "Academic year must end in the year after it starts, but was '" + value + "'");
        }

        value = trimmed;
    }

    @Override
    public String toString() {
        return value;
    }
}
