package com.meu.porquinho.exceptions;

import io.jsonwebtoken.JwtException;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import static java.util.stream.Collectors.joining;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErroDTO> handleResponseStatus(ResponseStatusException ex) {
        return ResponseEntity
                .status(ex.getStatusCode())
                .body(new ErroDTO(ex.getReason()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroDTO> handleValidation(MethodArgumentNotValidException ex) {
        String mensagem = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .collect(joining(", "));
        return ResponseEntity.badRequest().body(new ErroDTO(mensagem));
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ErroDTO> handleJwt(JwtException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErroDTO("Token inválido ou expirado"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroDTO> handleGeneric(Exception ex) {
        return ResponseEntity.internalServerError()
                .body(new ErroDTO("Erro interno do servidor"));
    }

    @Data
    @AllArgsConstructor
    public static class ErroDTO {
        private String mensagem;
    }
}