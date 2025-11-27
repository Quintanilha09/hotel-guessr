package com.hotel.guessr.exception;

/**
 * Exception lançada quando não há hotéis disponíveis para consulta
 */
public class HotelNaoEncontradoException extends RuntimeException {
    
    public HotelNaoEncontradoException(String message) {
        super(message);
    }
}
