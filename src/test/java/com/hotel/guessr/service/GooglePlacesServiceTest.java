package com.hotel.guessr.service;

import com.hotel.guessr.dto.CoordenadasResponse;
import com.hotel.guessr.dto.GooglePlacesResponse;
import com.hotel.guessr.dto.HotelResponse;
import com.hotel.guessr.exception.ApiKeyInvalidaException;
import com.hotel.guessr.exception.ErroConsultaExternaException;
import com.hotel.guessr.exception.LimiteRequisicaoExcedidoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GooglePlacesServiceTest {

    @Mock
    private RestTemplate restTemplate;
    
    @InjectMocks
    private GooglePlacesService googlePlacesService;
    
    private static final String API_KEY_VALIDA = "AIzaSyABC123";
    private static final String API_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json";
    private static final Integer LIMITE = 5;
    private static final Integer RAIO = 5000;
    
    // ==================== MÉTODOS AUXILIARES ====================
    
    private CoordenadasResponse criarCoordenadasValidas() {
        return CoordenadasResponse.builder()
                .latitude(-23.561684)
                .longitude(-46.656139)
                .build();
    }
    
    private GooglePlacesResponse criarGooglePlacesResponseValido() {
        GooglePlacesResponse response = new GooglePlacesResponse();
        response.setStatus("OK");
        
        List<GooglePlacesResponse.PlaceResult> results = new ArrayList<>();
        
        GooglePlacesResponse.PlaceResult place1 = new GooglePlacesResponse.PlaceResult();
        place1.setName("Hotel Paulista Plaza");
        place1.setVicinity("Av. Paulista, 2000");
        place1.setRating(4.5);
        place1.setUserRatingsTotal(250);
        
        GooglePlacesResponse.Geometry geometry1 = new GooglePlacesResponse.Geometry();
        GooglePlacesResponse.Location location1 = new GooglePlacesResponse.Location();
        location1.setLat(-23.561684);
        location1.setLng(-46.656139);
        geometry1.setLocation(location1);
        place1.setGeometry(geometry1);
        
        results.add(place1);
        response.setResults(results);
        
        return response;
    }
    
    private void configurarApiKey() {
        ReflectionTestUtils.setField(googlePlacesService, "apiKey", API_KEY_VALIDA);
        ReflectionTestUtils.setField(googlePlacesService, "apiUrl", API_URL);
    }
    
    // ==================== TESTES ====================
    
    @Test
    @DisplayName("Deve buscar hotéis próximos com sucesso quando dados são válidos")
    void deveBuscarHoteisProximosComSucessoQuandoDadosValidos() {
        // Given
        configurarApiKey();
        var coordenadas = criarCoordenadasValidas();
        var response = criarGooglePlacesResponseValido();
        
        when(restTemplate.getForObject(anyString(), eq(GooglePlacesResponse.class))).thenReturn(response);
        
        // When
        var resultado = googlePlacesService.buscarHoteisProximos(coordenadas, LIMITE, RAIO);
        
        // Then
        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        assertEquals("Hotel Paulista Plaza", resultado.get(0).getNome());
        
        verify(restTemplate, times(1)).getForObject(anyString(), eq(GooglePlacesResponse.class));
    }
    
    @Test
    @DisplayName("Deve lançar ApiKeyInvalidaException quando API key está vazia")
    void deveLancarApiKeyInvalidaExceptionQuandoApiKeyVazia() {
        // Given
        ReflectionTestUtils.setField(googlePlacesService, "apiKey", "");
        ReflectionTestUtils.setField(googlePlacesService, "apiUrl", API_URL);
        var coordenadas = criarCoordenadasValidas();
        
        // When & Then
        assertThrows(ApiKeyInvalidaException.class,
                () -> googlePlacesService.buscarHoteisProximos(coordenadas, LIMITE, RAIO)
        );
        
        verify(restTemplate, never()).getForObject(anyString(), eq(GooglePlacesResponse.class));
    }
    
    @Test
    @DisplayName("Deve lançar ApiKeyInvalidaException quando API key é YOUR_API_KEY_HERE")
    void deveLancarApiKeyInvalidaExceptionQuandoApiKeyPlaceholder() {
        // Given
        ReflectionTestUtils.setField(googlePlacesService, "apiKey", "YOUR_API_KEY_HERE");
        ReflectionTestUtils.setField(googlePlacesService, "apiUrl", API_URL);
        var coordenadas = criarCoordenadasValidas();
        
        // When & Then
        assertThrows(ApiKeyInvalidaException.class,
                () -> googlePlacesService.buscarHoteisProximos(coordenadas, LIMITE, RAIO)
        );
        
        verify(restTemplate, never()).getForObject(anyString(), eq(GooglePlacesResponse.class));
    }
    
    @Test
    @DisplayName("Deve retornar lista vazia quando nenhum resultado é encontrado")
    void deveRetornarListaVaziaQuandoNenhumResultadoEncontrado() {
        // Given
        configurarApiKey();
        var coordenadas = criarCoordenadasValidas();
        var response = new GooglePlacesResponse();
        response.setStatus("ZERO_RESULTS");
        response.setResults(null);
        
        when(restTemplate.getForObject(anyString(), eq(GooglePlacesResponse.class))).thenReturn(response);
        
        // When
        var resultado = googlePlacesService.buscarHoteisProximos(coordenadas, LIMITE, RAIO);
        
        // Then
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }
    
    @Test
    @DisplayName("Deve lançar LimiteRequisicaoExcedidoException quando recebe status 429")
    void deveLancarLimiteRequisicaoExcedidoExceptionQuandoRecebe429() {
        // Given
        configurarApiKey();
        var coordenadas = criarCoordenadasValidas();
        
        when(restTemplate.getForObject(anyString(), eq(GooglePlacesResponse.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.TOO_MANY_REQUESTS));
        
        // When & Then
        assertThrows(LimiteRequisicaoExcedidoException.class,
                () -> googlePlacesService.buscarHoteisProximos(coordenadas, LIMITE, RAIO)
        );
    }
    
    @Test
    @DisplayName("Deve lançar ErroConsultaExternaException quando ocorre erro 500")
    void deveLancarErroConsultaExternaExceptionQuandoOcorreErro500() {
        // Given
        configurarApiKey();
        var coordenadas = criarCoordenadasValidas();
        
        when(restTemplate.getForObject(anyString(), eq(GooglePlacesResponse.class)))
                .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));
        
        // When & Then
        assertThrows(ErroConsultaExternaException.class,
                () -> googlePlacesService.buscarHoteisProximos(coordenadas, LIMITE, RAIO)
        );
    }
    
    @Test
    @DisplayName("Deve lançar ErroConsultaExternaException quando resposta é nula")
    void deveLancarErroConsultaExternaExceptionQuandoRespostaNula() {
        // Given
        configurarApiKey();
        var coordenadas = criarCoordenadasValidas();
        
        when(restTemplate.getForObject(anyString(), eq(GooglePlacesResponse.class))).thenReturn(null);
        
        // When & Then
        assertThrows(ErroConsultaExternaException.class,
                () -> googlePlacesService.buscarHoteisProximos(coordenadas, LIMITE, RAIO)
        );
    }
    
    @Test
    @DisplayName("Deve limitar resultados quando limite é especificado")
    void deveLimitarResultadosQuandoLimiteEspecificado() {
        // Given
        configurarApiKey();
        var coordenadas = criarCoordenadasValidas();
        var response = criarGooglePlacesResponseValido();
        
        GooglePlacesResponse.PlaceResult place2 = new GooglePlacesResponse.PlaceResult();
        place2.setName("Hotel 2");
        place2.setVicinity("Rua Test");
        place2.setRating(4.0);
        place2.setUserRatingsTotal(100);
        
        GooglePlacesResponse.Geometry geometry = new GooglePlacesResponse.Geometry();
        GooglePlacesResponse.Location location = new GooglePlacesResponse.Location();
        location.setLat(-23.561684);
        location.setLng(-46.656139);
        geometry.setLocation(location);
        place2.setGeometry(geometry);
        
        response.getResults().add(place2);
        
        when(restTemplate.getForObject(anyString(), eq(GooglePlacesResponse.class))).thenReturn(response);
        
        // When
        var resultado = googlePlacesService.buscarHoteisProximos(coordenadas, 1, RAIO);
        
        // Then
        assertEquals(1, resultado.size());
    }
    
    @Test
    @DisplayName("Deve usar limite padrão de 5 quando limite não é especificado")
    void deveUsarLimitePadrao5QuandoLimiteNaoEspecificado() {
        // Given
        configurarApiKey();
        var coordenadas = criarCoordenadasValidas();
        var response = criarGooglePlacesResponseValido();
        
        for (int i = 0; i < 10; i++) {
            GooglePlacesResponse.PlaceResult place = new GooglePlacesResponse.PlaceResult();
            place.setName("Hotel " + i);
            place.setVicinity("Rua Test " + i);
            place.setRating(4.0);
            place.setUserRatingsTotal(100);
            
            GooglePlacesResponse.Geometry geometry = new GooglePlacesResponse.Geometry();
            GooglePlacesResponse.Location location = new GooglePlacesResponse.Location();
            location.setLat(-23.561684);
            location.setLng(-46.656139);
            geometry.setLocation(location);
            place.setGeometry(geometry);
            
            response.getResults().add(place);
        }
        
        when(restTemplate.getForObject(anyString(), eq(GooglePlacesResponse.class))).thenReturn(response);
        
        // When
        var resultado = googlePlacesService.buscarHoteisProximos(coordenadas, null, RAIO);
        
        // Then
        assertEquals(5, resultado.size());
    }
    
    @Test
    @DisplayName("Deve construir URL corretamente com coordenadas e raio")
    void deveConstruirUrlCorretamenteComCoordenadasERaio() {
        // Given
        configurarApiKey();
        var coordenadas = criarCoordenadasValidas();
        var response = criarGooglePlacesResponseValido();
        
        when(restTemplate.getForObject(anyString(), eq(GooglePlacesResponse.class))).thenReturn(response);
        
        // When
        googlePlacesService.buscarHoteisProximos(coordenadas, LIMITE, RAIO);
        
        // Then
        verify(restTemplate, times(1)).getForObject(
                argThat((String url) -> url.contains("location=-23.561684,-46.656139") && url.contains("radius=5000")),
                eq(GooglePlacesResponse.class)
        );
    }
}
