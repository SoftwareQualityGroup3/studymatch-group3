package pt.studymatch.exception;

// exceção lançada quando o código da uc não existe na base de dados
// é runtime para não obrigar a declarar throws em todo o lado
// o handler global de erros transforma isto num 404
public class CourseUnitNotFoundException extends RuntimeException {
    public CourseUnitNotFoundException(String code) {
        super("Course unit with code " + code + " was not found");
    }
}
