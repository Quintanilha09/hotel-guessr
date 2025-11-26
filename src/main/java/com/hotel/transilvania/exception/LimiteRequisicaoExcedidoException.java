package com.hotel.transilvania.exception;

/**
 * Exception lançada quando há excesso de requisições à API externa
 */
public class LimiteRequisicaoExcedidoException extends RuntimeException {
    
    public LimiteRequisicaoExcedidoException(String message) {
        super(message);
    }
    
    public LimiteRequisicaoExcedidoException(String message, Throwable cause) {
        super(message, cause);
    }
}
