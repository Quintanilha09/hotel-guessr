package com.hotel.guessr.service;

import com.hotel.guessr.dto.CoordenadasResponse;
import com.hotel.guessr.dto.HotelResponse;
import com.hotel.guessr.dto.HoteisProximosResponse;
import com.hotel.guessr.exception.HotelNaoEncontradoException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HotelService implements HotelServiceInterface {
    
    private final CepServiceInterface cepService;
    private final GeolocalizacaoServiceInterface geolocalizacaoService;
    private final GooglePlacesService googlePlacesService;
    
    @Override
    public HoteisProximosResponse buscarHoteisProximosPorCep(String cep, Integer limite) {
        log.info("Buscando hotéis próximos ao CEP: {} (limite: {})", cep, limite);
        
        try {
            var endereco = cepService.consultarCep(cep);
            CoordenadasResponse coordenadas = geolocalizacaoService.obterCoordenadasPorCep(cep);
            
            List<HotelResponse> hoteis = googlePlacesService.buscarHoteisProximos(
                    coordenadas,
                    limite,
                    5000
            );
            
            if (hoteis.isEmpty()) {
                log.warn("Nenhum hotel encontrado próximo ao CEP: {}", cep);
                throw new HotelNaoEncontradoException(
                    String.format("Nenhum hotel encontrado próximo ao CEP: %s", cep)
                );
            }
            
            log.info("Encontrados {} hotéis próximos via Google Places", hoteis.size());
            
            return HoteisProximosResponse.builder()
                    .cepConsultado(endereco.getCep())
                    .enderecoConsultado(String.format("%s, %s", endereco.getLogradouro(), endereco.getBairro()))
                    .cidade(endereco.getLocalidade())
                    .uf(endereco.getUf())
                    .hoteis(hoteis)
                    .totalEncontrado(hoteis.size())
                    .build();
                    
        } catch (HotelNaoEncontradoException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erro ao buscar hotéis próximos ao CEP: {}", cep, e);
            throw e;
        }
    }
}
