package com.hotel.guessr.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GooglePlacesResponse {
    
    @JsonProperty("results")
    private List<PlaceResult> results;
    
    @JsonProperty("status")
    private String status;
    
    @JsonProperty("error_message")
    private String errorMessage;
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PlaceResult {
        
        @JsonProperty("place_id")
        private String placeId;
        
        @JsonProperty("name")
        private String name;
        
        @JsonProperty("vicinity")
        private String vicinity;
        
        @JsonProperty("geometry")
        private Geometry geometry;
        
        @JsonProperty("rating")
        private Double rating;
        
        @JsonProperty("user_ratings_total")
        private Integer userRatingsTotal;
        
        @JsonProperty("types")
        private List<String> types;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Geometry {
        
        @JsonProperty("location")
        private Location location;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Location {
        
        @JsonProperty("lat")
        private Double lat;
        
        @JsonProperty("lng")
        private Double lng;
    }
}
