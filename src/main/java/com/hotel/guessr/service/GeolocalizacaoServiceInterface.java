package com.hotel.guessr.service;

import com.hotel.guessr.dto.CoordenadasResponse;

public interface GeolocalizacaoServiceInterface {
    
    CoordenadasResponse obterCoordenadasPorCep(String cep);
}
