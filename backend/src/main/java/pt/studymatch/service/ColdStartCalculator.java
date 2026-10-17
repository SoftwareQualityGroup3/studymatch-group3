package pt.studymatch.service;

/**
 * Classe responsável pela lógica de cold start.
 *
 * O objetivo é conseguir calcular uma pontuação para uma competência
 * mesmo quando o estudante ainda tem pouco ou nenhum histórico académico.
 *
 * A lógica combina:
 * - pontuação académica, quando existem resultados;
 * - autoavaliação do estudante, numa escala de 1 a 5.
 *
 * Esta classe não acede à base de dados.
 */
public final class ColdStartCalculator {

    /**
     * converte a autoavalição de 1-5 para 0-20
     */
    public double convertSelfAssessment(int value) {

        // A autoavaliação só pode ter valores entre 1 e 5.
        // Se estiver fora desse intervalo, os dados são inválidos.
        if (value < 1 || value > 5) {
            throw new IllegalArgumentException(
                "Self-assessment must be between 1 and 5");
        }

        // Converte a escala 1-5 para 4-20.
        return value * 4.0;
    }


    public Double calculateScore(
        Double academicScore,
        int evidenceCount,
        Integer selfAssessment) {

        // O número de evidências nunca pode ser negativo.
        if (evidenceCount < 0) {
            throw new IllegalArgumentException(
                "Evidence count cannot be negative");
        }

        // Se existir uma pontuação académica,
        // ela tem de ser um número válido entre 0 e 20.
        if (academicScore != null
            && (!Double.isFinite(academicScore)
            || academicScore < 0
            || academicScore > 20)) {

            throw new IllegalArgumentException(
                "Academic score must be finite and between 0 and 20");
        }

        // Não faz sentido existir uma pontuação académica
        // quando não existe nenhuma evidência académica.
        if (evidenceCount == 0 && academicScore != null) {
            throw new IllegalArgumentException(
                "Academic score must be null when there is no evidence");
        }

        // Também não faz sentido dizer que existem evidências
        // mas não existir uma pontuação académica calculada.
        if (evidenceCount > 0 && academicScore == null) {
            throw new IllegalArgumentException(
                "Academic score is required when evidence exists");
        }

        // Por defeito assumimos que não existe autoavaliação.
        Double selfAssessmentScore = null;

        // Se existir autoavaliação, convertemos da escala 1-5
        // para a escala 0-20 usada no perfil.
        if (selfAssessment != null) {
            selfAssessmentScore = convertSelfAssessment(selfAssessment);
        }

        // Caso 1 e caso 4:
        // se não existem evidências académicas,
        // devolvemos a autoavaliação convertida.
        //
        // Se também não existir autoavaliação,
        // selfAssessmentScore será null.
        if (evidenceCount == 0) {
            return selfAssessmentScore;
        }

        // Caso 3:
        // existem resultados académicos,
        // mas não existe autoavaliação.
        // Nesse caso usamos apenas a pontuação académica.
        if (selfAssessmentScore == null) {
            return academicScore;
        }

        // Caso 2:
        // existem resultados académicos e autoavaliação.
        //
        // Quanto maior for o número de evidências,
        // maior é o peso da componente académica.
        //
        // Exemplo:
        // n = 1 -> 1 / 1.5 = 0.667
        // n = 3 -> 3 / 3.5 = 0.857
        double academicWeight =
            evidenceCount / (evidenceCount + 0.5);

        // A pontuação final combina:
        // - componente académica;
        // - componente da autoavaliação.
        //
        // À medida que evidenceCount aumenta,
        // a influência da autoavaliação diminui.
        return academicWeight * academicScore
            + (1.0 - academicWeight) * selfAssessmentScore;
    }
}
