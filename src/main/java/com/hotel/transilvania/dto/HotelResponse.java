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
    
    private Long id;
    private String nome;
    private String endereco;
    private String cidade;
    private String uf;
    private String cep;
    private Integer estrelas;
    private String descricao;
    private Double distanciaKm;
}
