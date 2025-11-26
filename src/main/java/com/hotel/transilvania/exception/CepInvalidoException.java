package com.hotel.transilvania.exception;

/**
 * Exception lançada quando um CEP está em formato inválido
 */
public class CepInvalidoException extends RuntimeException {
    
    public CepInvalidoException(String cep) {
        super(String.format("CEP %s está em formato inválido. Use formato: 00000000 ou 00000-000", cep));
    }
    
    public CepInvalidoException(String message, Throwable cause) {
        super(message, cause);
    }
}
