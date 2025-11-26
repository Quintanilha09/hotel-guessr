package com.hotel.transilvania.service;

import com.hotel.transilvania.dto.HotelResponse;
import com.hotel.transilvania.dto.HoteisProximosResponse;

public interface HotelServiceInterface {
    
    HoteisProximosResponse buscarHoteisProximosPorCep(String cep, Integer limite);
}
