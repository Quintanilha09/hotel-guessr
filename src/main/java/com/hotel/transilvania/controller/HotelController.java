package com.hotel.transilvania.controller;

import com.hotel.transilvania.dto.HoteisProximosResponse;
import com.hotel.transilvania.service.HotelServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/hoteis")
@RequiredArgsConstructor
public class HotelController {
    
    private final HotelServiceInterface hotelService;
    
    @GetMapping("/proximos/{cep}")
    public ResponseEntity<HoteisProximosResponse> buscarHoteisProximos(
            @PathVariable String cep,
            @RequestParam(required = false, defaultValue = "5") Integer limite) {
        log.info("Buscando hotéis próximos ao CEP: {}", cep);
        HoteisProximosResponse response = hotelService.buscarHoteisProximosPorCep(cep, limite);
        return ResponseEntity.ok(response);
    }
}
