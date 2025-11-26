package com.hotel.transilvania.service;

import com.hotel.transilvania.dto.CoordenadasResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Serviço que simula obtenção de coordenadas geográficas por CEP
 * Em produção, seria substituído por uma API real de geolocalização
 */
@Slf4j
@Service
public class GeolocalizacaoService implements GeolocalizacaoServiceInterface {
    
    // Mock de coordenadas para algumas cidades principais
    private static final Map<String, CoordenadasResponse> COORDENADAS_POR_ESTADO = new HashMap<>();
    
    static {
        // São Paulo
        COORDENADAS_POR_ESTADO.put("SP", CoordenadasResponse.builder()
                .latitude(-23.5505)
                .longitude(-46.6333)
                .build());
        
        // Rio de Janeiro
        COORDENADAS_POR_ESTADO.put("RJ", CoordenadasResponse.builder()
                .latitude(-22.9068)
                .longitude(-43.1729)
                .build());
        
        // Minas Gerais
        COORDENADAS_POR_ESTADO.put("MG", CoordenadasResponse.builder()
                .latitude(-19.9167)
                .longitude(-43.9345)
                .build());
        
        // Bahia
        COORDENADAS_POR_ESTADO.put("BA", CoordenadasResponse.builder()
                .latitude(-12.9714)
                .longitude(-38.5014)
                .build());
        
        // Paraná
        COORDENADAS_POR_ESTADO.put("PR", CoordenadasResponse.builder()
                .latitude(-25.4284)
                .longitude(-49.2733)
                .build());
    }
    
    @Override
    public CoordenadasResponse obterCoordenadasPorCep(String cep) {
        log.info("Obtendo coordenadas aproximadas para CEP: {}", cep);
        
        // Simulação: retorna coordenadas aproximadas baseadas no CEP
        // Em produção, usaria API de geocodificação
        String uf = extrairUfDoCep(cep);
        
        CoordenadasResponse coordenadas = COORDENADAS_POR_ESTADO.getOrDefault(uf,
                CoordenadasResponse.builder()
                        .latitude(-15.7801)
                        .longitude(-47.9292)
                        .build()
        );
        
        log.debug("Coordenadas encontradas: lat={}, lon={}", 
                coordenadas.getLatitude(), coordenadas.getLongitude());
        
        return coordenadas;
    }
    
    private String extrairUfDoCep(String cep) {
        // Lógica simplificada baseada nos primeiros dígitos do CEP
        if (cep.startsWith("01") || cep.startsWith("02") || cep.startsWith("03")) {
            return "SP";
        } else if (cep.startsWith("20") || cep.startsWith("21")) {
            return "RJ";
        } else if (cep.startsWith("30") || cep.startsWith("31")) {
            return "MG";
        } else if (cep.startsWith("40") || cep.startsWith("41")) {
            return "BA";
        } else if (cep.startsWith("80") || cep.startsWith("81")) {
            return "PR";
        }
        return "DF"; // Default
    }
}
