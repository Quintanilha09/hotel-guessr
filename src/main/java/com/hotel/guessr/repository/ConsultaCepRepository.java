package com.hotel.guessr.repository;

import com.hotel.guessr.model.ConsultaCep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ConsultaCepRepository extends JpaRepository<ConsultaCep, UUID> {
}
