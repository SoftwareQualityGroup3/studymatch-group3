package pt.studymatch.exception;

// exceção lançada quando o aluno já tem nota registada naquela uc
// cada aluno só pode ter um registo por uc
// o handler global de erros transforma isto num 409 conflito
public class DuplicateAcademicRecordException extends RuntimeException {
    public DuplicateAcademicRecordException(Long studentId, String courseUnitCode) {
        super("Student " + studentId + " already has a record for course unit " + courseUnitCode);
    }
}
