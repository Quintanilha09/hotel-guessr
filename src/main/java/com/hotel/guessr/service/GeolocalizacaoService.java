package com.hotel.guessr.service;

import com.hotel.guessr.dto.CoordenadasResponse;
import com.hotel.guessr.dto.GeocodingResponse;
import com.hotel.guessr.exception.ErroConsultaExternaException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Locale;

/**
 * Serviço que obtém coordenadas geográficas usando Google Geocoding API
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GeolocalizacaoService implements GeolocalizacaoServiceInterface {
    
    @Value("${google.places.api.key}")
    private String apiKey;
    
    @Value("${google.geocoding.api.url}")
    private String geocodingApiUrl;
    
    private final RestTemplate restTemplate;
    
    @Override
    public CoordenadasResponse obterCoordenadasPorCep(String cep) {
        log.info("Obtendo coordenadas reais para CEP: {}", cep);
        
        try {
            String url = String.format(Locale.US, "%s?address=%s,Brazil&key=%s",
                    geocodingApiUrl,
                    cep.replace("-", ""),
                    apiKey);
            
            log.debug("Consultando Geocoding API para CEP: {}", cep);
            
            GeocodingResponse response = restTemplate.getForObject(url, GeocodingResponse.class);
            
            if (response == null || response.getResults() == null || response.getResults().isEmpty()) {
                log.warn("Nenhuma coordenada encontrada para CEP: {}", cep);
                throw new ErroConsultaExternaException("Não foi possível obter coordenadas para o CEP informado");
            }
            
            GeocodingResponse.Location location = response.getResults().get(0)
                    .getGeometry().getLocation();
            
            CoordenadasResponse coordenadas = CoordenadasResponse.builder()
                    .latitude(location.getLat())
                    .longitude(location.getLng())
                    .build();
            
            log.info("Coordenadas obtidas: lat={}, lng={}", 
                    coordenadas.getLatitude(), coordenadas.getLongitude());
            
            return coordenadas;
            
        } catch (Exception e) {
            log.error("Erro ao obter coordenadas para CEP {}: {}", cep, e.getMessage());
            throw new ErroConsultaExternaException("Erro ao consultar serviço de geolocalização", e);
        }
    }
}
