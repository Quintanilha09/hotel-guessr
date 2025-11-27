package com.hotel.guessr.service;

import com.hotel.guessr.dto.CepApiResponse;
import com.hotel.guessr.dto.ConsultaCepResponse;
import com.hotel.guessr.exception.CepInvalidoException;
import com.hotel.guessr.exception.CepNaoEncontradoException;
import com.hotel.guessr.exception.ErroConsultaExternaException;
import com.hotel.guessr.model.ConsultaCep;
import com.hotel.guessr.repository.ConsultaCepRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CepService implements CepServiceInterface {
    
    private final ConsultaCepRepository repository;
    private final RestTemplate restTemplate;
    
    @Value("${cep.api.url}")
    private String cepApiUrl;
    
    @Transactional
    public ConsultaCepResponse consultarCep(String cep) {
        log.info("Iniciando consulta de CEP: {}", cep);
        
        String cepNormalizado = validarENormalizarCep(cep);
        CepApiResponse apiResponse = buscarCepNaApiExterna(cepNormalizado);
        
        ConsultaCep consultaCep = converterParaEntity(apiResponse);
        ConsultaCep consultaSalva = repository.save(consultaCep);
        
        log.info("Consulta de CEP concluída e registrada: {}", cep);
        
        return converterParaResponse(consultaSalva);
    }
    
    private String validarENormalizarCep(String cep) {
        if (cep == null || cep.isBlank()) {
            throw new CepInvalidoException("CEP não pode ser vazio");
        }
        
        String cepLimpo = cep.replaceAll("[^0-9]", "");
        
        if (cepLimpo.length() != 8) {
            throw new CepInvalidoException(cep);
        }
        
        return cepLimpo;
    }
    
    /**
     * Busca o CEP na API externa (ViaCEP)
     */
    private CepApiResponse buscarCepNaApiExterna(String cep) {
        try {
            String url = String.format("%s/%s/json/", cepApiUrl, cep);
            log.debug("Consultando API externa: {}", url);
            
            CepApiResponse response = restTemplate.getForObject(url, CepApiResponse.class);
            
            if (response == null || Boolean.TRUE.equals(response.getErro())) {
                throw new CepNaoEncontradoException(cep);
            }
            
            return response;
            
        } catch (HttpClientErrorException.NotFound e) {
            log.warn("CEP não encontrado na API externa: {}", cep);
            throw new CepNaoEncontradoException(cep, e);
        } catch (Exception e) {
            log.error("Erro ao consultar API externa para CEP: {}", cep, e);
            throw new ErroConsultaExternaException("Erro ao consultar API externa de CEP", e);
        }
    }
    
    /**
     * Converte DTO da API para Entity
     */
    private ConsultaCep converterParaEntity(CepApiResponse apiResponse) {
        return ConsultaCep.builder()
                .cep(apiResponse.getCep())
                .logradouro(apiResponse.getLogradouro())
                .complemento(apiResponse.getComplemento())
                .bairro(apiResponse.getBairro())
                .localidade(apiResponse.getLocalidade())
                .uf(apiResponse.getUf())
                .ibge(apiResponse.getIbge())
                .gia(apiResponse.getGia())
                .ddd(apiResponse.getDdd())
                .siafi(apiResponse.getSiafi())
                .build();
    }
    
    /**
     * Converte Entity para DTO de resposta
     */
    private ConsultaCepResponse converterParaResponse(ConsultaCep entity) {
        return ConsultaCepResponse.builder()
                .id(entity.getId())
                .cep(entity.getCep())
                .logradouro(entity.getLogradouro())
                .complemento(entity.getComplemento())
                .bairro(entity.getBairro())
                .localidade(entity.getLocalidade())
                .uf(entity.getUf())
                .ddd(entity.getDdd())
                .dataConsulta(entity.getDataConsulta())
                .build();
    }
}
