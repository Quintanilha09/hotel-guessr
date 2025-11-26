package com.hotel.transilvania.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para resposta da API externa de CEP (ViaCEP)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CepApiResponse {
    
    private String cep;
    private String logradouro;
    private String complemento;
    private String bairro;
    private String localidade;
    private String uf;
    private String ibge;
    private String gia;
    private String ddd;
    private String siafi;
    
    @JsonProperty("erro")
    private Boolean erro;
}
