package com.hotel.guessr.service;

import com.hotel.guessr.dto.CepApiResponse;
import com.hotel.guessr.dto.ConsultaCepResponse;
import com.hotel.guessr.exception.CepInvalidoException;
import com.hotel.guessr.exception.CepNaoEncontradoException;
import com.hotel.guessr.exception.ErroConsultaExternaException;
import com.hotel.guessr.model.ConsultaCep;
import com.hotel.guessr.repository.ConsultaCepRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CepServiceTest {

    @Mock
    private ConsultaCepRepository repository;
    
    @Mock
    private RestTemplate restTemplate;
    
    @InjectMocks
    private CepService cepService;
    
    private static final String CEP_VALIDO = "01310100";
    private static final String CEP_VALIDO_FORMATADO = "01310-100";
    private static final String URL_API_CEP = "https://viacep.com.br/ws";
    
    // ==================== MÉTODOS AUXILIARES ====================
    
    private CepApiResponse criarCepApiResponseValido() {
        CepApiResponse response = new CepApiResponse();
        response.setCep(CEP_VALIDO_FORMATADO);
        response.setLogradouro("Avenida Paulista");
        response.setComplemento("de 612 a 1510 - lado par");
        response.setBairro("Bela Vista");
        response.setLocalidade("São Paulo");
        response.setUf("SP");
        response.setDdd("11");
        response.setIbge("3550308");
        response.setGia("1004");
        response.setSiafi("7107");
        return response;
    }
    
    private ConsultaCep criarConsultaCepEntity() {
        return ConsultaCep.builder()
                .id(UUID.randomUUID())
                .cep(CEP_VALIDO_FORMATADO)
                .logradouro("Avenida Paulista")
                .complemento("de 612 a 1510 - lado par")
                .bairro("Bela Vista")
                .localidade("São Paulo")
                .uf("SP")
                .ddd("11")
                .ibge("3550308")
                .gia("1004")
                .siafi("7107")
                .dataConsulta(LocalDateTime.now())
                .build();
    }
    
    private static Stream<Arguments> getCepsInvalidos() {
        return Stream.of(
                Arguments.of(null, "CEP não pode ser vazio"),
                Arguments.of("", "CEP não pode ser vazio"),
                Arguments.of("   ", "CEP não pode ser vazio"),
                Arguments.of("123", "CEP inválido"),
                Arguments.of("123456789", "CEP inválido")
        );
    }
    
    // ==================== TESTES ====================
    
    @Test
    @DisplayName("Deve consultar CEP com sucesso quando CEP é válido")
    void deveConsultarCepComSucessoQuandoCepValido() {
        // Given
        ReflectionTestUtils.setField(cepService, "cepApiUrl", URL_API_CEP);
        var apiResponse = criarCepApiResponseValido();
        var entitySalva = criarConsultaCepEntity();
        
        when(restTemplate.getForObject(anyString(), eq(CepApiResponse.class))).thenReturn(apiResponse);
        when(repository.save(any(ConsultaCep.class))).thenReturn(entitySalva);
        
        // When
        var resultado = cepService.consultarCep(CEP_VALIDO);
        
        // Then
        assertNotNull(resultado);
        assertEquals(CEP_VALIDO_FORMATADO, resultado.getCep());
        assertEquals("Avenida Paulista", resultado.getLogradouro());
        assertEquals("São Paulo", resultado.getLocalidade());
        assertEquals("SP", resultado.getUf());
        
        verify(restTemplate, times(1)).getForObject(anyString(), eq(CepApiResponse.class));
        verify(repository, times(1)).save(any(ConsultaCep.class));
    }
    
    @Test
    @DisplayName("Deve consultar CEP com sucesso quando CEP tem formatação")
    void deveConsultarCepComSucessoQuandoCepTemFormatacao() {
        // Given
        ReflectionTestUtils.setField(cepService, "cepApiUrl", URL_API_CEP);
        var apiResponse = criarCepApiResponseValido();
        var entitySalva = criarConsultaCepEntity();
        
        when(restTemplate.getForObject(anyString(), eq(CepApiResponse.class))).thenReturn(apiResponse);
        when(repository.save(any(ConsultaCep.class))).thenReturn(entitySalva);
        
        // When
        var resultado = cepService.consultarCep(CEP_VALIDO_FORMATADO);
        
        // Then
        assertNotNull(resultado);
        assertEquals(CEP_VALIDO_FORMATADO, resultado.getCep());
        verify(restTemplate, times(1)).getForObject(anyString(), eq(CepApiResponse.class));
    }
    
    @ParameterizedTest
    @MethodSource("getCepsInvalidos")
    @DisplayName("Deve lançar CepInvalidoException quando CEP é inválido")
    void deveLancarCepInvalidoExceptionQuandoCepInvalido(String cepInvalido, String mensagemEsperada) {
        // Given & When & Then
        var excecao = assertThrows(CepInvalidoException.class, 
            () -> cepService.consultarCep(cepInvalido)
        );
        
        assertNotNull(excecao.getMessage());
        
        verify(restTemplate, never()).getForObject(anyString(), eq(CepApiResponse.class));
        verify(repository, never()).save(any(ConsultaCep.class));
    }
    
    @Test
    @DisplayName("Deve lançar ErroConsultaExternaException quando API retorna 404")
    void deveLancarErroConsultaExternaExceptionQuandoApiRetorna404() {
        // Given
        ReflectionTestUtils.setField(cepService, "cepApiUrl", URL_API_CEP);
        
        when(restTemplate.getForObject(anyString(), eq(CepApiResponse.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));
        
        // When & Then
        var excecao = assertThrows(ErroConsultaExternaException.class, 
            () -> cepService.consultarCep(CEP_VALIDO)
        );
        
        // A exceção é envolvida por ErroConsultaExternaException no catch genérico
        assertNotNull(excecao.getMessage());
        assertTrue(excecao.getMessage().contains("API externa"));
        
        verify(restTemplate, times(1)).getForObject(anyString(), eq(CepApiResponse.class));
        verify(repository, never()).save(any(ConsultaCep.class));
    }
    
    @Test
    @DisplayName("Deve lançar ErroConsultaExternaException quando API retorna erro 500")
    void deveLancarErroConsultaExternaExceptionQuandoApiRetornaErro500() {
        // Given
        ReflectionTestUtils.setField(cepService, "cepApiUrl", URL_API_CEP);
        
        when(restTemplate.getForObject(anyString(), eq(CepApiResponse.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR));
        
        // When & Then
        assertThrows(ErroConsultaExternaException.class, 
            () -> cepService.consultarCep(CEP_VALIDO)
        );
        
        verify(restTemplate, times(1)).getForObject(anyString(), eq(CepApiResponse.class));
        verify(repository, never()).save(any(ConsultaCep.class));
    }
    
    @Test
    @DisplayName("Deve lançar ErroConsultaExternaException quando API retorna CEP com erro")
    void deveLancarErroConsultaExternaExceptionQuandoApiRetornaCepComErro() {
        // Given
        ReflectionTestUtils.setField(cepService, "cepApiUrl", URL_API_CEP);
        var apiResponse = new CepApiResponse();
        apiResponse.setErro(true);
        
        when(restTemplate.getForObject(anyString(), eq(CepApiResponse.class))).thenReturn(apiResponse);
        
        // When & Then
        var excecao = assertThrows(ErroConsultaExternaException.class, 
            () -> cepService.consultarCep(CEP_VALIDO)
        );
        
        assertNotNull(excecao.getCause());
        assertTrue(excecao.getCause() instanceof CepNaoEncontradoException);
        
        verify(restTemplate, times(1)).getForObject(anyString(), eq(CepApiResponse.class));
        verify(repository, never()).save(any(ConsultaCep.class));
    }
    
    @Test
    @DisplayName("Deve salvar consulta no repositório após busca bem-sucedida")
    void deveSalvarConsultaNoRepositorioAposBuscaBemSucedida() {
        // Given
        ReflectionTestUtils.setField(cepService, "cepApiUrl", URL_API_CEP);
        var apiResponse = criarCepApiResponseValido();
        var entitySalva = criarConsultaCepEntity();
        
        when(restTemplate.getForObject(anyString(), eq(CepApiResponse.class))).thenReturn(apiResponse);
        when(repository.save(any(ConsultaCep.class))).thenReturn(entitySalva);
        
        // When
        cepService.consultarCep(CEP_VALIDO);
        
        // Then
        verify(repository, times(1)).save(any(ConsultaCep.class));
    }
}
