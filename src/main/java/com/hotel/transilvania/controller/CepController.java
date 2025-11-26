package com.hotel.transilvania.controller;

import com.hotel.transilvania.dto.ConsultaCepResponse;
import com.hotel.transilvania.service.CepServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/cep")
@RequiredArgsConstructor
public class CepController {
    
    private final CepServiceInterface cepService;
    
    @GetMapping("/{cep}")
    public ResponseEntity<ConsultaCepResponse> consultarCep(@PathVariable String cep) {
        log.info("Consultando CEP: {}", cep);
        ConsultaCepResponse response = cepService.consultarCep(cep);
        return ResponseEntity.ok(response);
    }
}
