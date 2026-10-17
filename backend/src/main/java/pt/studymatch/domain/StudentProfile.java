package pt.studymatch.domain;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public class StudentProfile {

    // Guarda o resultado calculado de uma competência.
    public record CompetencyResult(
        Double score,
        int evidenceCount,
        ProfileSource source) {
    }

    private final Map<Competency, CompetencyResult> profiles;

    public StudentProfile(
        Map<Competency, CompetencyResult> profiles) {

        // Guarda os perfis de cada competência num mapa.
        // Depois de guardado, o mapa fica protegido contra alterações diretas.
        this.profiles = Collections.unmodifiableMap(
            new EnumMap<>(profiles));
    }

    public Map<Competency, CompetencyResult> getProfiles() {
        return profiles;
    }

    public CompetencyResult getProfile(Competency competency) {
        return profiles.get(competency);
    }
}
