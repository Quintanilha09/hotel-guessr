package com.hotel.transilvania.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class ApiErroResponse {
    
    @JsonProperty("apierro")
    private ApiErro apierro;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApiErro {
        private LocalDateTime timestamp;
        private String status;
        private Integer codigoErro;
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
