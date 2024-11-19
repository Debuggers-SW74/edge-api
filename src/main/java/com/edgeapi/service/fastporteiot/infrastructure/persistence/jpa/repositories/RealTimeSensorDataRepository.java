package com.edgeapi.service.fastporteiot.infrastructure.persistence.jpa.repositories;

import com.edgeapi.service.fastporteiot.domain.model.entities.RealTimeSensorData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RealTimeSensorDataRepository extends JpaRepository<RealTimeSensorData, Long> {
    List<RealTimeSensorData> findByTripId(int tripId);
}
