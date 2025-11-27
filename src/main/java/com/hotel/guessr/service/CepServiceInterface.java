package com.hotel.guessr.service;

import com.hotel.guessr.dto.ConsultaCepResponse;

public interface CepServiceInterface {
    
    ConsultaCepResponse consultarCep(String cep);
}
