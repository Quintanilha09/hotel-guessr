package com.hotel.guessr.repository;

import com.hotel.guessr.model.ConsultaCep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConsultaCepRepository extends JpaRepository<ConsultaCep, Long> {
}
