package com.hotel.transilvania.exception;

/**
 * Exception lançada quando a API Key do Google Places é inválida ou não configurada
 */
public class ApiKeyInvalidaException extends RuntimeException {
    
    public ApiKeyInvalidaException(String message) {
        super(message);
    }
    
    public ApiKeyInvalidaException(String message, Throwable cause) {
        super(message, cause);
    }
}
