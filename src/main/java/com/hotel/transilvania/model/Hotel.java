package com.hotel.transilvania.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "hoteis")
public class Hotel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Size(max = 200)
    @Column(nullable = false)
    private String nome;
    
    @NotBlank
    @Size(max = 9)
    @Column(nullable = false, length = 9)
    private String cep;
    
    @NotBlank
    @Column(nullable = false)
    private String endereco;
    
    @NotBlank
    @Size(max = 100)
    @Column(nullable = false)
    private String cidade;
    
    @NotBlank
    @Size(max = 2)
    @Column(nullable = false, length = 2)
    private String uf;
    
    @NotNull
    @Column(nullable = false)
    private Double latitude;
    
    @NotNull
    @Column(nullable = false)
    private Double longitude;
    
    @Column(columnDefinition = "TEXT")
    private String descricao;
    
    private Integer estrelas;
}
