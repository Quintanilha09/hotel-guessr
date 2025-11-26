package com.hotel.transilvania.exception;

import com.hotel.transilvania.dto.ApiErroResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ApiExceptionHandler {
    
    @ExceptionHandler(CepNaoEncontradoException.class)
    public ResponseEntity<ApiErroResponse> handleCepNaoEncontrado(CepNaoEncontradoException ex) {
        log.warn("CEP não encontrado: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiErroResponse.of(HttpStatus.NOT_FOUND, ex.getMessage()));
    }
    
    @ExceptionHandler(CepInvalidoException.class)
    public ResponseEntity<ApiErroResponse> handleCepInvalido(CepInvalidoException ex) {
        log.warn("CEP inválido: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiErroResponse.of(HttpStatus.BAD_REQUEST, ex.getMessage()));
    }
    
    @ExceptionHandler(ErroConsultaExternaException.class)
    public ResponseEntity<ApiErroResponse> handleErroConsultaExterna(ErroConsultaExternaException ex) {
        log.error("Erro na consulta externa: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ApiErroResponse.of(HttpStatus.SERVICE_UNAVAILABLE, "Serviço de consulta de CEP temporariamente indisponível"));
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErroResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        log.warn("Erro de validação: {}", ex.getMessage());
        
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiErroResponse.of(HttpStatus.BAD_REQUEST, message));
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErroResponse> handleGenericException(Exception ex) {
        log.error("Erro inesperado: {}", ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiErroResponse.of(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno no servidor"));
    }
}
