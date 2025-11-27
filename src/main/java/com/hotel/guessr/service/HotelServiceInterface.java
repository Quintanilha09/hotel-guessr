package com.hotel.guessr.service;

import com.hotel.guessr.dto.HotelResponse;
import com.hotel.guessr.dto.HoteisProximosResponse;

public interface HotelServiceInterface {
    
    HoteisProximosResponse buscarHoteisProximosPorCep(String cep, Integer limite);
}
