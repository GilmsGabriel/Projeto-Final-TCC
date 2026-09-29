package br.edu.escola.agendamento.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<ApiErrorResponse> handleRegraNegocioException(
            RegraNegocioException ex
    ) {
        ApiErrorResponse response = new ApiErrorResponse(
                false,
                ex.getMessage(),
                ex.getCodigoErro(),
                List.of(ex.getDetalhe()),
                Instant.now()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ApiErrorResponse> handleRecursoNaoEncontrado(
            RecursoNaoEncontradoException ex
    ) {
        ApiErrorResponse response = new ApiErrorResponse(
                false,
                ex.getMessage(),
                ex.getCodigoErro(),
                List.of(),
                Instant.now()
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    @ExceptionHandler(AcessoNegadoException.class)
    public ResponseEntity<ApiErrorResponse> handleAcessoNegado(
            AcessoNegadoException ex
    ) {
        ApiErrorResponse response = new ApiErrorResponse(
                false,
                ex.getMessage(),
                ex.getCodigoErro(),
                List.of(),
                Instant.now()
        );

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(response);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleSpringAccessDenied(
            AccessDeniedException ex
    ) {
        ApiErrorResponse response = new ApiErrorResponse(
                false,
                "Acesso negado.",
                "ACESSO_NEGADO",
                List.of(ex.getMessage()),
                Instant.now()
        );

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex
    ) {
        List<String> detalhes = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        ApiErrorResponse response = new ApiErrorResponse(
                false,
                "Erro de validação.",
                "VALIDACAO_ERRO",
                detalhes,
                Instant.now()
        );

        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(
            Exception ex
    ) {
        ApiErrorResponse response = new ApiErrorResponse(
                false,
                "Ocorreu um erro interno no servidor.",
                "ERRO_INTERNO",
                List.of(),
                Instant.now()
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }
}
