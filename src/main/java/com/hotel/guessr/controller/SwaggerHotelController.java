package com.hotel.guessr.controller;

import com.hotel.guessr.dto.ApiErroResponse;
import com.hotel.guessr.dto.HoteisProximosResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Hotéis", description = "Operações para busca de hotéis próximos")
public interface SwaggerHotelController {

    @Operation(
        summary = "Busca hotéis próximos a um CEP",
        description = "Endpoint que busca hotéis em um raio próximo ao CEP informado utilizando Google Places API"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Ok",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = HoteisProximosResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Bad Request",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErroResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Not Found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErroResponse.class))
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal Server Error",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErroResponse.class))
        )
    })
    ResponseEntity<HoteisProximosResponse> buscarHoteisProximos(
        @Parameter(description = "CEP de referência para busca", required = true, example = "01310100")
        String cep,
        @Parameter(description = "Limite de hotéis retornados", required = false, example = "5")
        Integer limite
    );
}
