package com.hotel.guessr.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidade JPA representando uma consulta de CEP
 */
@Entity
@Table(name = "consultas_cep")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsultaCep {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "CEP não pode ser vazio")
    @Size(min = 8, max = 9, message = "CEP deve ter 8 ou 9 caracteres")
    @Column(nullable = false, length = 9)
    private String cep;
    
    @NotBlank(message = "Logradouro não pode ser vazio")
    @Column(nullable = false)
    private String logradouro;
    
    private String complemento;
    
    private String bairro;
    
    @NotBlank(message = "Localidade não pode ser vazia")
    @Column(nullable = false)
    private String localidade;
    
    @NotBlank(message = "UF não pode ser vazio")
    @Size(min = 2, max = 2, message = "UF deve ter 2 caracteres")
    @Column(nullable = false, length = 2)
    private String uf;
    
    private String ibge;
    
    private String gia;
    
    private String ddd;
    
    private String siafi;
    
    @NotNull(message = "Data da consulta não pode ser nula")
    @Column(nullable = false)
    private LocalDateTime dataConsulta;
    
    @PrePersist
    protected void onCreate() {
        if (dataConsulta == null) {
            dataConsulta = LocalDateTime.now();
        }
    }
}
