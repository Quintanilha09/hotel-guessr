package com.hotel.transilvania.repository;

import com.hotel.transilvania.model.ConsultaCep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository para persistência de consultas de CEP
 */
@Repository
public interface ConsultaCepRepository extends JpaRepository<ConsultaCep, Long> {
    
    /**
     * Busca consultas por CEP
     */
    List<ConsultaCep> findByCep(String cep);
    
    /**
     * Busca consultas por período
     */
    List<ConsultaCep> findByDataConsultaBetween(LocalDateTime inicio, LocalDateTime fim);
    
    /**
     * Busca consultas por UF
     */
    List<ConsultaCep> findByUf(String uf);
}
