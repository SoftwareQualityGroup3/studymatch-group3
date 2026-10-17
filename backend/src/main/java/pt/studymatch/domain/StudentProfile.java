package pt.studymatch.domain;

public class StudentProfile {
    private final Double score; // "Double" e não "double" porque no caso sem evidência devolve-se null
    private final int evidenceCount;
    private final ProfileSource source;

    public StudentProfile(Double score, int evidenceCount, ProfileSource source){
        this.score = score;
        this.evidenceCount = evidenceCount;
        this.source = source;
    }

    public Double getScore() {
        return score;
    }

    public int getEvidenceCount() {
        return evidenceCount;
    }

    public ProfileSource getSource() {
        return source;
    }
}
