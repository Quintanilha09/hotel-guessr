package com.hotel.transilvania.service;

import com.hotel.transilvania.dto.CoordenadasResponse;
import com.hotel.transilvania.dto.GooglePlacesResponse;
import com.hotel.transilvania.dto.HotelResponse;
import com.hotel.transilvania.exception.ApiKeyInvalidaException;
import com.hotel.transilvania.exception.ErroConsultaExternaException;
import com.hotel.transilvania.exception.LimiteRequisicaoExcedidoException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GooglePlacesService {
    
    @Value("${google.places.api.key}")
    private String apiKey;
    
    @Value("${google.places.api.url}")
    private String apiUrl;
    
    private final RestTemplate restTemplate;
    
    /**
     * Busca hotéis próximos usando Google Places API
     */
    public List<HotelResponse> buscarHoteisProximos(CoordenadasResponse coordenadas, Integer limite, Integer raio) {
        validarApiKey();
        
        try {
            String url = construirUrl(coordenadas, raio);
            log.info("Consultando Google Places API: {}", url);
            
            GooglePlacesResponse response = restTemplate.getForObject(url, GooglePlacesResponse.class);
            
            if (response == null) {
                log.error("Resposta nula da Google Places API");
                throw new ErroConsultaExternaException("Resposta vazia do Google Places");
            }
            
            log.info("Status da resposta: {}", response.getStatus());
            log.info("Mensagem de erro: {}", response.getErrorMessage());
            
            validarStatusResposta(response.getStatus());
            
            if (response.getResults() == null) {
                log.warn("Nenhum resultado encontrado");
                return Collections.emptyList();
            }
            
            log.info("Google Places retornou {} resultados", response.getResults().size());
            
            return response.getResults().stream()
                    .limit(limite != null ? limite : 5)
                    .map(place -> converterParaHotelResponse(place, coordenadas))
                    .collect(Collectors.toList());
                    
        } catch (HttpClientErrorException e) {
            tratarErroCliente(e);
            throw new ErroConsultaExternaException("Erro ao buscar hotéis próximos", e);
            
        } catch (HttpServerErrorException e) {
            log.error("Erro no servidor do Google Places ({}): {}", e.getStatusCode(), e.getMessage());
            throw new ErroConsultaExternaException("Serviço do Google Places temporariamente indisponível", e);
            
        } catch (ResourceAccessException e) {
            log.error("Timeout ou erro de conexão com Google Places: {}", e.getMessage());
            throw new ErroConsultaExternaException("Não foi possível conectar ao serviço de busca de hotéis", e);
            
        } catch (ApiKeyInvalidaException | LimiteRequisicaoExcedidoException e) {
            throw e;
            
        } catch (Exception e) {
            log.error("Erro inesperado ao consultar Google Places API", e);
            throw new ErroConsultaExternaException("Erro ao buscar hotéis próximos", e);
        }
    }
    
    private void validarApiKey() {
        if (apiKey == null || apiKey.isBlank() || apiKey.equals("YOUR_API_KEY_HERE")) {
            log.error("API Key do Google Places não configurada");
            throw new ApiKeyInvalidaException(
                "Google Places API Key não configurada. Configure a variável GOOGLE_PLACES_API_KEY"
            );
        }
    }
    
    private void validarStatusResposta(String status) {
        switch (status) {
            case "OK", "ZERO_RESULTS" -> log.debug("Status da API: {}", status);
            
            case "REQUEST_DENIED" -> {
                log.error("Requisição negada pelo Google Places: API Key inválida");
                throw new ApiKeyInvalidaException("API Key do Google Places inválida ou sem permissões");
            }
            
            case "OVER_QUERY_LIMIT" -> {
                log.error("Limite de requisições do Google Places excedido");
                throw new LimiteRequisicaoExcedidoException(
                    "Limite de requisições da API do Google Places excedido. Tente novamente mais tarde"
                );
            }
            
            case "INVALID_REQUEST" -> {
                log.error("Requisição inválida para Google Places");
                throw new IllegalArgumentException("Parâmetros inválidos para busca de hotéis");
            }
            
            default -> {
                log.error("Status desconhecido do Google Places: {}", status);
                throw new ErroConsultaExternaException("Erro ao consultar hotéis: " + status);
            }
        }
    }
    
    private void tratarErroCliente(HttpClientErrorException e) {
        HttpStatus status = (HttpStatus) e.getStatusCode();
        
        if (status == HttpStatus.FORBIDDEN || status == HttpStatus.UNAUTHORIZED) {
            log.error("Acesso negado ao Google Places: {}", e.getMessage());
            throw new ApiKeyInvalidaException("API Key do Google Places inválida ou sem permissões", e);
        }
        
        if (status == HttpStatus.TOO_MANY_REQUESTS) {
            log.error("Rate limit excedido no Google Places");
            throw new LimiteRequisicaoExcedidoException("Limite de requisições excedido", e);
        }
        
        log.error("Erro do cliente ao consultar Google Places ({}): {}", status, e.getMessage());
    }
    
    private String construirUrl(CoordenadasResponse coordenadas, Integer raio) {
        int raioMetros = raio != null ? raio : 5000; // Padrão 5km
        
        return String.format("%s?location=%f,%f&radius=%d&type=lodging&key=%s",
                apiUrl,
                coordenadas.getLatitude(),
                coordenadas.getLongitude(),
                raioMetros,
                apiKey);
    }
    
    private HotelResponse converterParaHotelResponse(GooglePlacesResponse.PlaceResult place, 
                                                     CoordenadasResponse coordenadasOrigem) {
        Double distancia = calcularDistancia(
                coordenadasOrigem.getLatitude(),
                coordenadasOrigem.getLongitude(),
                place.getGeometry().getLocation().getLat(),
                place.getGeometry().getLocation().getLng()
        );
        
        return HotelResponse.builder()
                .nome(place.getName())
                .endereco(place.getVicinity())
                .distanciaKm(Math.round(distancia * 100.0) / 100.0)
                .estrelas(converterRatingParaEstrelas(place.getRating()))
                .descricao(String.format("Avaliação: %.1f (%d avaliações)", 
                        place.getRating() != null ? place.getRating() : 0.0,
                        place.getUserRatingsTotal() != null ? place.getUserRatingsTotal() : 0))
                .build();
    }
    
    private Integer converterRatingParaEstrelas(Double rating) {
        if (rating == null) return null;
        return (int) Math.round(rating);
    }
    
    private Double calcularDistancia(Double lat1, Double lon1, Double lat2, Double lon2) {
        final int RAIO_TERRA_KM = 6371;
        
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return RAIO_TERRA_KM * c;
    }
}
