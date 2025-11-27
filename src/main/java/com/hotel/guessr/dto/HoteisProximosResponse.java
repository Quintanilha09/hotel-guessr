package com.hotel.guessr.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HoteisProximosResponse {
    
    private String cepConsultado;
    private String enderecoConsultado;
    private String cidade;
    private String uf;
    private List<HotelResponse> hoteis;
    private Integer totalEncontrado;
}
