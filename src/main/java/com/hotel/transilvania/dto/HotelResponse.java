package com.hotel.transilvania.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelResponse {
    
    private String nome;
    private String endereco;
    private Integer estrelas;
    private String descricao;
    private Double distanciaKm;
}
