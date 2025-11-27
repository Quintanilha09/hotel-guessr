package com.hotel.guessr.exception;

/**
 * Exception lançada quando um CEP não é encontrado
 */
public class CepNaoEncontradoException extends RuntimeException {
    
    public CepNaoEncontradoException(String cep) {
        super(String.format("CEP %s não encontrado", cep));
    }
    
    public CepNaoEncontradoException(String cep, Throwable cause) {
        super(String.format("CEP %s não encontrado", cep), cause);
    }
}
