package com.hotel.guessr.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Resposta de erro da API")
public class ApiErroResponse {
    
    @Schema(description = "Detalhes do erro")
    @JsonProperty("apierro")
    private ApiErro apierro;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Objeto contendo informações detalhadas do erro")
    public static class ApiErro {
        
        @Schema(description = "Data e hora do erro", example = "2024-01-15T10:30:45")
        private LocalDateTime timestamp;
        
        @Schema(description = "Status HTTP do erro", example = "NOT_FOUND")
        private String status;
        
        @Schema(description = "Código numérico do erro HTTP", example = "404")
        private Integer codigoErro;
        
        @Schema(description = "Mensagem detalhada explicando o erro", example = "CEP não encontrado na base de dados")
        private String mensagemDetalhada;
    }
    
    public static ApiErroResponse of(HttpStatus httpStatus, String mensagemDetalhada) {
        return ApiErroResponse.builder()
                .apierro(ApiErro.builder()
                        .timestamp(LocalDateTime.now())
                        .status(httpStatus.name())
                        .codigoErro(httpStatus.value())
                        .mensagemDetalhada(mensagemDetalhada)
                        .build())
                .build();
    }
}
