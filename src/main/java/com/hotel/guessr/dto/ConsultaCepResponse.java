package com.hotel.guessr.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO para resposta de consulta de CEP
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Resposta da consulta de CEP contendo endereço completo")
public class ConsultaCepResponse {
    
    @Schema(description = "Identificador único da consulta", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;
    
    @Schema(description = "Código de Endereçamento Postal", example = "01310100")
    private String cep;
    
    @Schema(description = "Logradouro (rua, avenida, etc.)", example = "Avenida Paulista")
    private String logradouro;
    
    @Schema(description = "Complemento do endereço", example = "lado ímpar")
    private String complemento;
    
    @Schema(description = "Bairro", example = "Bela Vista")
    private String bairro;
    
    @Schema(description = "Cidade/Localidade", example = "São Paulo")
    private String localidade;
    
    @Schema(description = "Unidade Federativa (estado)", example = "SP")
    private String uf;
    
    @Schema(description = "Código DDD", example = "11")
    private String ddd;
    
    @Schema(description = "Data e hora da consulta", example = "2024-01-15T10:30:45")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dataConsulta;
}
