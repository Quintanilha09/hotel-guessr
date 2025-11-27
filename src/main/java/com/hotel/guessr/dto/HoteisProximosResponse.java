package com.hotel.guessr.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Resposta contendo lista de hotéis próximos ao CEP consultado")
public class HoteisProximosResponse {
    
    @Schema(description = "CEP que foi consultado", example = "01310100")
    private String cepConsultado;
    
    @Schema(description = "Endereço completo do CEP consultado", example = "Avenida Paulista, 1578 - Bela Vista")
    private String enderecoConsultado;
    
    @Schema(description = "Cidade do CEP consultado", example = "São Paulo")
    private String cidade;
    
    @Schema(description = "Estado do CEP consultado", example = "SP")
    private String uf;
    
    @Schema(description = "Lista de hotéis encontrados ordenados por distância")
    private List<HotelResponse> hoteis;
    
    @Schema(description = "Total de hotéis encontrados", example = "5")
    private Integer totalEncontrado;
}
