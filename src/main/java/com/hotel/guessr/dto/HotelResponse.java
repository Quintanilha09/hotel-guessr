package com.hotel.guessr.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Informações de um hotel encontrado")
public class HotelResponse {
    
    @Schema(description = "Nome do hotel", example = "Hotel Renaissance São Paulo")
    private String nome;
    
    @Schema(description = "Endereço completo do hotel", example = "Alameda Santos, 2233 - Jardim Paulista")
    private String endereco;
    
    @Schema(description = "Classificação do hotel em estrelas", example = "4")
    private Integer estrelas;
    
    @Schema(description = "Descrição/avaliação do hotel", example = "Hotel de luxo com vista panorâmica da cidade")
    private String descricao;
    
    @Schema(description = "Distância do hotel até o CEP consultado em quilômetros", example = "1.2")
    private Double distanciaKm;
}
