package com.hotel.transilvania.service;

import com.hotel.transilvania.dto.CoordenadasResponse;
import com.hotel.transilvania.dto.HotelResponse;
import com.hotel.transilvania.dto.HoteisProximosResponse;
import com.hotel.transilvania.exception.HotelNaoEncontradoException;
import com.hotel.transilvania.model.Hotel;
import com.hotel.transilvania.repository.HotelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class HotelService implements HotelServiceInterface {
    
    private final HotelRepository hotelRepository;
    private final CepServiceInterface cepService;
    private final GeolocalizacaoServiceInterface geolocalizacaoService;
    
    @Override
    @Transactional(readOnly = true)
    public HoteisProximosResponse buscarHoteisProximosPorCep(String cep, Integer limite) {
        log.info("Buscando hotéis próximos ao CEP: {} (limite: {})", cep, limite);
        
        try {
            // Consulta o CEP para obter informações do endereço
            var endereco = cepService.consultarCep(cep);
            
            // Obtém coordenadas aproximadas
            CoordenadasResponse coordenadas = geolocalizacaoService.obterCoordenadasPorCep(cep);
            
            // Busca hotéis próximos usando a query de distância
            List<Hotel> hoteis = hotelRepository.findHoteisProximosPorCoordenadas(
                    coordenadas.getLatitude(),
                    coordenadas.getLongitude(),
                    endereco.getUf(),
                    limite != null ? limite : 5
            );
            
            if (hoteis.isEmpty()) {
                log.warn("Nenhum hotel encontrado próximo ao CEP: {} no estado: {}", cep, endereco.getUf());
                throw new HotelNaoEncontradoException(
                    String.format("Nenhum hotel encontrado para o estado: %s", endereco.getUf())
                );
            }
            
            log.info("Encontrados {} hotéis próximos", hoteis.size());
            
            // Converte para DTO
            List<HotelResponse> hoteisResponse = hoteis.stream()
                    .map(hotel -> converterParaResponse(hotel, coordenadas))
                    .collect(Collectors.toList());
            
            return HoteisProximosResponse.builder()
                    .cepConsultado(endereco.getCep())
                    .enderecoConsultado(String.format("%s, %s", endereco.getLogradouro(), endereco.getBairro()))
                    .cidade(endereco.getLocalidade())
                    .uf(endereco.getUf())
                    .hoteis(hoteisResponse)
                    .totalEncontrado(hoteisResponse.size())
                    .build();
                    
        } catch (HotelNaoEncontradoException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erro ao buscar hotéis próximos ao CEP: {}", cep, e);
            throw e;
        }
    }
    
    private HotelResponse converterParaResponse(Hotel hotel, CoordenadasResponse coordenadas) {
        Double distancia = calcularDistancia(
                coordenadas.getLatitude(),
                coordenadas.getLongitude(),
                hotel.getLatitude(),
                hotel.getLongitude()
        );
        
        return HotelResponse.builder()
                .id(hotel.getId())
                .nome(hotel.getNome())
                .endereco(hotel.getEndereco())
                .cidade(hotel.getCidade())
                .uf(hotel.getUf())
                .cep(hotel.getCep())
                .estrelas(hotel.getEstrelas())
                .descricao(hotel.getDescricao())
                .distanciaKm(Math.round(distancia * 100.0) / 100.0)
                .build();
    }
    
    /**
     * Calcula distância entre dois pontos usando fórmula de Haversine
     */
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
