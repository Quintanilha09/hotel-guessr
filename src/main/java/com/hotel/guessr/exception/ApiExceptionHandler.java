package com.hotel.guessr.exception;

import com.hotel.guessr.dto.ApiErroResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

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
    
    @ExceptionHandler(HotelNaoEncontradoException.class)
    public ResponseEntity<ApiErroResponse> handleHotelNaoEncontrado(HotelNaoEncontradoException ex) {
        log.warn("Hotel não encontrado: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiErroResponse.of(HttpStatus.NOT_FOUND, ex.getMessage()));
    }
    
    @ExceptionHandler(ErroConsultaExternaException.class)
    public ResponseEntity<ApiErroResponse> handleErroConsultaExterna(ErroConsultaExternaException ex) {
        log.error("Erro na consulta externa: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ApiErroResponse.of(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage()));
    }
    
    @ExceptionHandler(ApiKeyInvalidaException.class)
    public ResponseEntity<ApiErroResponse> handleApiKeyInvalida(ApiKeyInvalidaException ex) {
        log.error("API Key inválida: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiErroResponse.of(HttpStatus.UNAUTHORIZED, ex.getMessage()));
    }
    
    @ExceptionHandler(LimiteRequisicaoExcedidoException.class)
    public ResponseEntity<ApiErroResponse> handleLimiteRequisicaoExcedido(LimiteRequisicaoExcedidoException ex) {
        log.error("Limite de requisições excedido: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.TOO_MANY_REQUESTS)
                .body(ApiErroResponse.of(HttpStatus.TOO_MANY_REQUESTS, ex.getMessage()));
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
    
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErroResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        log.warn("Erro de tipo de argumento: {}", ex.getMessage());
        String message = String.format("Parâmetro '%s' inválido. Valor fornecido: '%s'", 
                ex.getName(), ex.getValue());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiErroResponse.of(HttpStatus.BAD_REQUEST, message));
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErroResponse> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Argumento ilegal: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiErroResponse.of(HttpStatus.BAD_REQUEST, ex.getMessage()));
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErroResponse> handleGenericException(Exception ex) {
        log.error("Erro inesperado: {}", ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiErroResponse.of(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno no servidor"));
    }
}
