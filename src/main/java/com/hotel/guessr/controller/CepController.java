package com.hotel.guessr.controller;

import com.hotel.guessr.dto.ConsultaCepResponse;
import com.hotel.guessr.service.CepServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/cep")
@RequiredArgsConstructor
public class CepController implements SwaggerCepController {
    
    private final CepServiceInterface cepService;
    
    @Override
    @GetMapping("/{cep}")
    public ResponseEntity<ConsultaCepResponse> consultarCep(@PathVariable String cep) {
        log.info("Consultando CEP: {}", cep);
        ConsultaCepResponse response = cepService.consultarCep(cep);
        return ResponseEntity.ok(response);
    }
}
