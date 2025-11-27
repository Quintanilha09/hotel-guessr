package com.hotel.guessr.controller;

import com.hotel.guessr.dto.ApiErroResponse;
import com.hotel.guessr.dto.ConsultaCepResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "CEP", description = "Operações para consulta de CEP")
public interface SwaggerCepController {

    @Operation(
        summary = "Consulta informações de um CEP",
        description = "Endpoint que consulta informações completas de endereço a partir de um CEP válido"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Ok",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ConsultaCepResponse.class))
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
    ResponseEntity<ConsultaCepResponse> consultarCep(
        @Parameter(description = "CEP a ser consultado", required = true, example = "01310100")
        @PathVariable String cep
    );
}
