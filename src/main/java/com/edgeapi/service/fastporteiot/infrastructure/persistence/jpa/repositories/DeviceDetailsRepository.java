package com.edgeapi.service.fastporteiot.infrastructure.persistence.jpa.repositories;

import com.edgeapi.service.fastporteiot.domain.model.aggregates.DeviceDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeviceDetailsRepository extends JpaRepository<DeviceDetails, Long> {
    Optional<DeviceDetails> findByMacAddress(String macAddress);
}
