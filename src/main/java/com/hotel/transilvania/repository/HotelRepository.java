package com.hotel.transilvania.repository;

import com.hotel.transilvania.model.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {
    
    List<Hotel> findByUf(String uf);
    
    List<Hotel> findByCidade(String cidade);
    
    @Query(value = """
        SELECT * FROM hoteis h
        WHERE h.uf = :uf
        ORDER BY (
            6371 * acos(
                cos(radians(:latitude)) * 
                cos(radians(h.latitude)) * 
                cos(radians(h.longitude) - radians(:longitude)) + 
                sin(radians(:latitude)) * 
                sin(radians(h.latitude))
            )
        ) ASC
        LIMIT :limite
        """, nativeQuery = true)
    List<Hotel> findHoteisProximosPorCoordenadas(
        @Param("latitude") Double latitude,
        @Param("longitude") Double longitude,
        @Param("uf") String uf,
        @Param("limite") Integer limite
    );
}
