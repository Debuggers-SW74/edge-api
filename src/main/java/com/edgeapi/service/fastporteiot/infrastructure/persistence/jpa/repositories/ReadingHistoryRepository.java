package com.edgeapi.service.fastporteiot.infrastructure.persistence.jpa.repositories;

import com.edgeapi.service.fastporteiot.domain.model.entities.ReadingHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface ReadingHistoryRepository extends JpaRepository<ReadingHistory, Long> {
    List<ReadingHistory> findByMacAddressAndTimestampBetween(
            String macAddress,
            Instant startTime,
            Instant endTime
    );
}
