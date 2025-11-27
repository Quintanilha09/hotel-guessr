package com.hotel.guessr.service;

import com.hotel.guessr.dto.CoordenadasResponse;
import com.hotel.guessr.dto.ConsultaCepResponse;
import com.hotel.guessr.dto.HotelResponse;
import com.hotel.guessr.exception.CepNaoEncontradoException;
import com.hotel.guessr.exception.HotelNaoEncontradoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HotelServiceTest {

    @Mock
    private CepServiceInterface cepService;
    
    @Mock
    private GeolocalizacaoServiceInterface geolocalizacaoService;
    
    @Mock
    private GooglePlacesService googlePlacesService;
    
    @InjectMocks
    private HotelService hotelService;
    
    private static final String CEP_VALIDO = "01310-100";
    private static final Integer LIMITE_HOTEIS = 10;
    private static final Integer RAIO_PADRAO = 5000;
    
    // ==================== MÉTODOS AUXILIARES ====================
    
    private ConsultaCepResponse criarConsultaCepResponse() {
        return ConsultaCepResponse.builder()
                .id(UUID.randomUUID())
                .cep(CEP_VALIDO)
                .logradouro("Avenida Paulista")
                .bairro("Bela Vista")
                .localidade("São Paulo")
                .uf("SP")
                .ddd("11")
                .dataConsulta(LocalDateTime.now())
                .build();
    }
    
    private CoordenadasResponse criarCoordenadasResponse() {
        return CoordenadasResponse.builder()
                .latitude(-23.561684)
                .longitude(-46.656139)
                .build();
    }
    
    private List<HotelResponse> criarListaHoteisValida() {
        List<HotelResponse> hoteis = new ArrayList<>();
        
        hoteis.add(HotelResponse.builder()
                .nome("Hotel Paulista Plaza")
                .endereco("Av. Paulista, 2000")
                .distanciaKm(0.5)
                .estrelas(4)
                .build());
                
        hoteis.add(HotelResponse.builder()
                .nome("Hotel Centro SP")
                .endereco("Rua da Consolação, 1500")
                .distanciaKm(1.2)
                .estrelas(4)
                .build());
                
        return hoteis;
    }
    
    // ==================== TESTES ====================
    
    @Test
    @DisplayName("Deve buscar hotéis próximos com sucesso quando CEP é válido")
    void deveBuscarHoteisProximosComSucessoQuandoCepValido() {
        // Given
        var enderecoResponse = criarConsultaCepResponse();
        var coordenadas = criarCoordenadasResponse();
        var hoteisEncontrados = criarListaHoteisValida();
        
        when(cepService.consultarCep(CEP_VALIDO)).thenReturn(enderecoResponse);
        when(geolocalizacaoService.obterCoordenadasPorCep(CEP_VALIDO)).thenReturn(coordenadas);
        when(googlePlacesService.buscarHoteisProximos(coordenadas, LIMITE_HOTEIS, RAIO_PADRAO))
                .thenReturn(hoteisEncontrados);
        
        // When
        var resultado = hotelService.buscarHoteisProximosPorCep(CEP_VALIDO, LIMITE_HOTEIS);
        
        // Then
        assertNotNull(resultado);
        assertEquals(CEP_VALIDO, resultado.getCepConsultado());
        assertEquals("São Paulo", resultado.getCidade());
        assertEquals("SP", resultado.getUf());
        assertEquals(2, resultado.getTotalEncontrado());
        assertEquals(2, resultado.getHoteis().size());
        
        verify(cepService, times(1)).consultarCep(CEP_VALIDO);
        verify(geolocalizacaoService, times(1)).obterCoordenadasPorCep(CEP_VALIDO);
        verify(googlePlacesService, times(1)).buscarHoteisProximos(coordenadas, LIMITE_HOTEIS, RAIO_PADRAO);
    }
    
    @Test
    @DisplayName("Deve lançar HotelNaoEncontradoException quando nenhum hotel é encontrado")
    void deveLancarHotelNaoEncontradoExceptionQuandoNenhumHotelEncontrado() {
        // Given
        var enderecoResponse = criarConsultaCepResponse();
        var coordenadas = criarCoordenadasResponse();
        var listaVazia = new ArrayList<HotelResponse>();
        
        when(cepService.consultarCep(CEP_VALIDO)).thenReturn(enderecoResponse);
        when(geolocalizacaoService.obterCoordenadasPorCep(CEP_VALIDO)).thenReturn(coordenadas);
        when(googlePlacesService.buscarHoteisProximos(coordenadas, LIMITE_HOTEIS, RAIO_PADRAO))
                .thenReturn(listaVazia);
        
        // When & Then
        var excecao = assertThrows(HotelNaoEncontradoException.class,
                () -> hotelService.buscarHoteisProximosPorCep(CEP_VALIDO, LIMITE_HOTEIS)
        );
        
        assertTrue(excecao.getMessage().contains(CEP_VALIDO));
        
        verify(cepService, times(1)).consultarCep(CEP_VALIDO);
        verify(geolocalizacaoService, times(1)).obterCoordenadasPorCep(CEP_VALIDO);
        verify(googlePlacesService, times(1)).buscarHoteisProximos(coordenadas, LIMITE_HOTEIS, RAIO_PADRAO);
    }
    
    @Test
    @DisplayName("Deve propagar exceção quando CEP não é encontrado")
    void devePropagarExcecaoQuandoCepNaoEncontrado() {
        // Given
        when(cepService.consultarCep(CEP_VALIDO))
                .thenThrow(new CepNaoEncontradoException(CEP_VALIDO));
        
        // When & Then
        assertThrows(CepNaoEncontradoException.class,
                () -> hotelService.buscarHoteisProximosPorCep(CEP_VALIDO, LIMITE_HOTEIS)
        );
        
        verify(cepService, times(1)).consultarCep(CEP_VALIDO);
        verify(geolocalizacaoService, never()).obterCoordenadasPorCep(anyString());
        verify(googlePlacesService, never()).buscarHoteisProximos(any(), any(), any());
    }
    
    @Test
    @DisplayName("Deve montar resposta corretamente com dados do endereço")
    void deveMontarRespostaCorretamenteComDadosEndereco() {
        // Given
        var enderecoResponse = criarConsultaCepResponse();
        var coordenadas = criarCoordenadasResponse();
        var hoteisEncontrados = criarListaHoteisValida();
        
        when(cepService.consultarCep(CEP_VALIDO)).thenReturn(enderecoResponse);
        when(geolocalizacaoService.obterCoordenadasPorCep(CEP_VALIDO)).thenReturn(coordenadas);
        when(googlePlacesService.buscarHoteisProximos(any(), any(), any())).thenReturn(hoteisEncontrados);
        
        // When
        var resultado = hotelService.buscarHoteisProximosPorCep(CEP_VALIDO, LIMITE_HOTEIS);
        
        // Then
        assertNotNull(resultado.getEnderecoConsultado());
        assertTrue(resultado.getEnderecoConsultado().contains("Avenida Paulista"));
        assertTrue(resultado.getEnderecoConsultado().contains("Bela Vista"));
    }
    
    @Test
    @DisplayName("Deve usar raio padrão de 5000 metros na busca")
    void deveUsarRaioPadrao5000MetrosNaBusca() {
        // Given
        var enderecoResponse = criarConsultaCepResponse();
        var coordenadas = criarCoordenadasResponse();
        var hoteisEncontrados = criarListaHoteisValida();
        
        when(cepService.consultarCep(CEP_VALIDO)).thenReturn(enderecoResponse);
        when(geolocalizacaoService.obterCoordenadasPorCep(CEP_VALIDO)).thenReturn(coordenadas);
        when(googlePlacesService.buscarHoteisProximos(any(), any(), eq(5000))).thenReturn(hoteisEncontrados);
        
        // When
        hotelService.buscarHoteisProximosPorCep(CEP_VALIDO, LIMITE_HOTEIS);
        
        // Then
        verify(googlePlacesService, times(1)).buscarHoteisProximos(any(), any(), eq(5000));
    }
    
    @Test
    @DisplayName("Deve retornar total encontrado igual ao tamanho da lista de hotéis")
    void deveRetornarTotalEncontradoIgualTamanhoListaHoteis() {
        // Given
        var enderecoResponse = criarConsultaCepResponse();
        var coordenadas = criarCoordenadasResponse();
        var hoteisEncontrados = criarListaHoteisValida();
        
        when(cepService.consultarCep(CEP_VALIDO)).thenReturn(enderecoResponse);
        when(geolocalizacaoService.obterCoordenadasPorCep(CEP_VALIDO)).thenReturn(coordenadas);
        when(googlePlacesService.buscarHoteisProximos(any(), any(), any())).thenReturn(hoteisEncontrados);
        
        // When
        var resultado = hotelService.buscarHoteisProximosPorCep(CEP_VALIDO, LIMITE_HOTEIS);
        
        // Then
        assertEquals(hoteisEncontrados.size(), resultado.getTotalEncontrado());
        assertEquals(hoteisEncontrados.size(), resultado.getHoteis().size());
    }
}
