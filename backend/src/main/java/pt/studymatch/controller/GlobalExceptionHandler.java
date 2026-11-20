package pt.studymatch.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import pt.studymatch.exception.DuplicateUsernameException;
import pt.studymatch.dto.ApiErrorResponse;
import pt.studymatch.exception.CourseUnitNotFoundException;
import pt.studymatch.exception.DuplicateAcademicRecordException;
import pt.studymatch.exception.DuplicateStudentNumberException;
import pt.studymatch.exception.StudentNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;

/**
 * Trata as exceções da aplicação e transforma-as
 * em respostas HTTP consistentes.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Trata erros das validações do StudentRequest.
     *
     * Exemplo:
     * name vazio -> 400 Bad Request
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(
        MethodArgumentNotValidException exception) {

        Map<String, String> fieldErrors = new LinkedHashMap<>();

        exception.getBindingResult()
            .getFieldErrors()
            .forEach(error ->
                fieldErrors.put(
                    error.getField(),
                    error.getDefaultMessage()
                )
            );

        return ResponseEntity
            .badRequest()
            .body(new ApiErrorResponse(fieldErrors));
    }

    /**
     * Trata pedidos para estudantes que não existem.
     */
    @ExceptionHandler(StudentNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleStudentNotFound(
        StudentNotFoundException exception) {

        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(new ApiErrorResponse(
                Map.of("id", exception.getMessage())
            ));
    }

    /**
     * Trata pedidos para unidades curriculares que não existem.
     */
    @ExceptionHandler(CourseUnitNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleCourseUnitNotFound(
        CourseUnitNotFoundException exception) {

        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(new ApiErrorResponse(
                Map.of("courseUnitCode", exception.getMessage())
            ));
    }

    /**
     * Trata números de estudante repetidos.
     */
    @ExceptionHandler(DuplicateStudentNumberException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicateNumber(
        DuplicateStudentNumberException exception) {

        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(new ApiErrorResponse(
                Map.of("number", exception.getMessage())
            ));
    }

    /**
     * Trata registos académicos duplicados.
     */
    @ExceptionHandler(DuplicateAcademicRecordException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicateAcademicRecord(
        DuplicateAcademicRecordException exception) {

        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(new ApiErrorResponse(
                Map.of("courseUnitCode", exception.getMessage())
            ));
    }
    /**
     * Trata pedidos sem credenciais válidas (401).
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiErrorResponse> handleAuthentication(
        AuthenticationException exception) {

        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(new ApiErrorResponse(
                Map.of("general", "Authentication is required")
            ));
    }

    /**
     * Trata pedidos de utilizadores sem o papel necessário (403).
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDenied(
        AccessDeniedException exception) {

        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(new ApiErrorResponse(
                Map.of("general", "Access denied")
            ));
    }

    /**
     * Trata outras violações das regras do domínio.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgument(
        IllegalArgumentException exception) {

        return ResponseEntity
            .badRequest()
            .body(new ApiErrorResponse(
                Map.of("general", exception.getMessage())
            ));
    }
    /**
     * Trata usernames repetidos.
     */
    @ExceptionHandler(DuplicateUsernameException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicateUsername(
        DuplicateUsernameException exception) {

        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(new ApiErrorResponse(
                Map.of("username", exception.getMessage())
            ));
    }
}
