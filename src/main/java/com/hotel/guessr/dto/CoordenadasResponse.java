package com.hotel.guessr.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoordenadasResponse {
    
    @JsonProperty("lat")
    private Double latitude;
    
    @JsonProperty("lon")
    private Double longitude;
}
