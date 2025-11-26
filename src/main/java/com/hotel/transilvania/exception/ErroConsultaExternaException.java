package com.hotel.transilvania.exception;

/**
 * Exception lançada quando há erro na consulta à API externa
 */
public class ErroConsultaExternaException extends RuntimeException {
    
    public ErroConsultaExternaException(String message) {
        super(message);
    }
    
    public ErroConsultaExternaException(String message, Throwable cause) {
        super(message, cause);
    }
}
