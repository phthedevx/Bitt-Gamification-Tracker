package com.bitt.tracker.api;
import com.bitt.tracker.services.RecursoNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ProblemDetail handleNotFound(RecursoNaoEncontradoException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }
    @ExceptionHandler(RegraDeNegocioException.class)
    public ProblemDetail handleBusinessRule(RegraDeNegocioException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    }
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public org.springframework.http.ProblemDetail handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        org.springframework.http.ProblemDetail pd = org.springframework.http.ProblemDetail.forStatusAndDetail(
                org.springframework.http.HttpStatus.BAD_REQUEST, "Parâmetro inválido: " + ex.getName());
        pd.setTitle("Erro de tipagem no parâmetro");
        pd.setType(java.net.URI.create("urn:bitt:erro:parametro-invalido"));
        pd.setProperty("timestamp", java.time.Instant.now());
        return pd;
    }
}