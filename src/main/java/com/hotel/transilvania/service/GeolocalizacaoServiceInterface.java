package com.hotel.transilvania.service;

import com.hotel.transilvania.dto.CoordenadasResponse;

public interface GeolocalizacaoServiceInterface {
    
    CoordenadasResponse obterCoordenadasPorCep(String cep);
}
