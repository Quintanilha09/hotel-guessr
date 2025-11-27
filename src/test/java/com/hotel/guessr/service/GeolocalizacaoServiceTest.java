package com.hotel.guessr.service;

import com.hotel.guessr.dto.CoordenadasResponse;
import com.hotel.guessr.dto.GeocodingResponse;
import com.hotel.guessr.exception.ErroConsultaExternaException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GeolocalizacaoServiceTest {

    @Mock
    private RestTemplate restTemplate;
    
    @InjectMocks
    private GeolocalizacaoService geolocalizacaoService;
    
    private static final String API_KEY = "AIzaSyABC123";
    private static final String GEOCODING_URL = "https://maps.googleapis.com/maps/api/geocode/json";
    private static final String CEP_VALIDO = "01310-100";
    private static final String CEP_SEM_FORMATACAO = "01310100";
    
    // ==================== MÉTODOS AUXILIARES ====================
    
    private GeocodingResponse criarGeocodingResponseValido() {
        GeocodingResponse response = new GeocodingResponse();
        response.setStatus("OK");
        
        List<GeocodingResponse.Result> results = new ArrayList<>();
        GeocodingResponse.Result result = new GeocodingResponse.Result();
        
        GeocodingResponse.Geometry geometry = new GeocodingResponse.Geometry();
        GeocodingResponse.Location location = new GeocodingResponse.Location();
        location.setLat(-23.561684);
        location.setLng(-46.656139);
        geometry.setLocation(location);
        result.setGeometry(geometry);
        
        results.add(result);
        response.setResults(results);
        
        return response;
    }
    
    private void configurarApiKey() {
        ReflectionTestUtils.setField(geolocalizacaoService, "apiKey", API_KEY);
        ReflectionTestUtils.setField(geolocalizacaoService, "geocodingApiUrl", GEOCODING_URL);
    }
    
    private static Stream<Arguments> getCepsComDiferentesFormatos() {
        return Stream.of(
                Arguments.of("01310-100"),
                Arguments.of("01310100")
        );
    }
    
    // ==================== TESTES ====================
    
    @Test
    @DisplayName("Deve obter coordenadas com sucesso quando CEP é válido")
    void deveObterCoordenadasComSucessoQuandoCepValido() {
        // Given
        configurarApiKey();
        var response = criarGeocodingResponseValido();
        
        when(restTemplate.getForObject(anyString(), eq(GeocodingResponse.class))).thenReturn(response);
        
        // When
        var resultado = geolocalizacaoService.obterCoordenadasPorCep(CEP_VALIDO);
        
        // Then
        assertNotNull(resultado);
        assertEquals(-23.561684, resultado.getLatitude());
        assertEquals(-46.656139, resultado.getLongitude());
        
        verify(restTemplate, times(1)).getForObject(anyString(), eq(GeocodingResponse.class));
    }
    
    @ParameterizedTest
    @MethodSource("getCepsComDiferentesFormatos")
    @DisplayName("Deve processar CEP corretamente independente da formatação")
    void deveProcessarCepCorretamenteIndependenteFormatacao(String cep) {
        // Given
        configurarApiKey();
        var response = criarGeocodingResponseValido();
        
        when(restTemplate.getForObject(anyString(), eq(GeocodingResponse.class))).thenReturn(response);
        
        // When
        var resultado = geolocalizacaoService.obterCoordenadasPorCep(cep);
        
        // Then
        assertNotNull(resultado);
        assertNotNull(resultado.getLatitude());
        assertNotNull(resultado.getLongitude());
    }
    
    @Test
    @DisplayName("Deve lançar ErroConsultaExternaException quando resposta é nula")
    void deveLancarErroConsultaExternaExceptionQuandoRespostaNula() {
        // Given
        configurarApiKey();
        
        when(restTemplate.getForObject(anyString(), eq(GeocodingResponse.class))).thenReturn(null);
        
        // When & Then
        assertThrows(ErroConsultaExternaException.class,
                () -> geolocalizacaoService.obterCoordenadasPorCep(CEP_VALIDO)
        );
    }
    
    @Test
    @DisplayName("Deve lançar ErroConsultaExternaException quando lista de resultados é vazia")
    void deveLancarErroConsultaExternaExceptionQuandoListaResultadosVazia() {
        // Given
        configurarApiKey();
        var response = new GeocodingResponse();
        response.setStatus("ZERO_RESULTS");
        response.setResults(new ArrayList<>());
        
        when(restTemplate.getForObject(anyString(), eq(GeocodingResponse.class))).thenReturn(response);
        
        // When & Then
        assertThrows(ErroConsultaExternaException.class,
                () -> geolocalizacaoService.obterCoordenadasPorCep(CEP_VALIDO)
        );
    }
    
    @Test
    @DisplayName("Deve lançar ErroConsultaExternaException quando lista de resultados é nula")
    void deveLancarErroConsultaExternaExceptionQuandoListaResultadosNula() {
        // Given
        configurarApiKey();
        var response = new GeocodingResponse();
        response.setStatus("ZERO_RESULTS");
        response.setResults(null);
        
        when(restTemplate.getForObject(anyString(), eq(GeocodingResponse.class))).thenReturn(response);
        
        // When & Then
        assertThrows(ErroConsultaExternaException.class,
                () -> geolocalizacaoService.obterCoordenadasPorCep(CEP_VALIDO)
        );
    }
    
    @Test
    @DisplayName("Deve lançar ErroConsultaExternaException quando RestTemplate lança exceção")
    void deveLancarErroConsultaExternaExceptionQuandoRestTemplateLancaExcecao() {
        // Given
        configurarApiKey();
        
        when(restTemplate.getForObject(anyString(), eq(GeocodingResponse.class)))
                .thenThrow(new RuntimeException("Erro de conexão"));
        
        // When & Then
        assertThrows(ErroConsultaExternaException.class,
                () -> geolocalizacaoService.obterCoordenadasPorCep(CEP_VALIDO)
        );
    }
    
    @Test
    @DisplayName("Deve remover hífen do CEP na construção da URL")
    void deveRemoverHifenDoCepNaConstrucaoUrl() {
        // Given
        configurarApiKey();
        var response = criarGeocodingResponseValido();
        
        when(restTemplate.getForObject(anyString(), eq(GeocodingResponse.class))).thenReturn(response);
        
        // When
        geolocalizacaoService.obterCoordenadasPorCep(CEP_VALIDO);
        
        // Then
        verify(restTemplate, times(1)).getForObject(
                argThat((String url) -> url.contains(CEP_SEM_FORMATACAO) && !url.contains(CEP_VALIDO)),
                eq(GeocodingResponse.class)
        );
    }
    
    @Test
    @DisplayName("Deve incluir Brazil na consulta de geolocalização")
    void deveIncluirBrazilNaConsultaGeolocalizacao() {
        // Given
        configurarApiKey();
        var response = criarGeocodingResponseValido();
        
        when(restTemplate.getForObject(anyString(), eq(GeocodingResponse.class))).thenReturn(response);
        
        // When
        geolocalizacaoService.obterCoordenadasPorCep(CEP_VALIDO);
        
        // Then
        verify(restTemplate, times(1)).getForObject(
                argThat((String url) -> url.contains("Brazil")),
                eq(GeocodingResponse.class)
        );
    }
    
    @Test
    @DisplayName("Deve retornar coordenadas do primeiro resultado da API")
    void deveRetornarCoordenadasDoPrimeiroResultadoDaApi() {
        // Given
        configurarApiKey();
        var response = criarGeocodingResponseValido();
        
        GeocodingResponse.Result segundoResultado = new GeocodingResponse.Result();
        GeocodingResponse.Geometry geometry2 = new GeocodingResponse.Geometry();
        GeocodingResponse.Location location2 = new GeocodingResponse.Location();
        location2.setLat(-23.999999);
        location2.setLng(-46.999999);
        geometry2.setLocation(location2);
        segundoResultado.setGeometry(geometry2);
        response.getResults().add(segundoResultado);
        
        when(restTemplate.getForObject(anyString(), eq(GeocodingResponse.class))).thenReturn(response);
        
        // When
        var resultado = geolocalizacaoService.obterCoordenadasPorCep(CEP_VALIDO);
        
        // Then
        assertEquals(-23.561684, resultado.getLatitude());
        assertEquals(-46.656139, resultado.getLongitude());
    }
}
