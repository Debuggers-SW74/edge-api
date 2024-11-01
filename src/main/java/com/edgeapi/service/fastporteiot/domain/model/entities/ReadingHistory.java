package com.edgeapi.service.fastporteiot.domain.model.entities;

import com.edgeapi.service.fastporteiot.domain.model.valueobjects.DeviceStatus;
import com.edgeapi.service.fastporteiot.domain.model.valueobjects.SensorReading;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@Entity(name = "reading_history")
@AllArgsConstructor
public class ReadingHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String macAddress;

    @Embedded
    private SensorReading reading;

    private Instant timestamp;

    @Enumerated(EnumType.STRING)
    private DeviceStatus status;

    protected ReadingHistory() {}

    public ReadingHistory(String macAddress, SensorReading reading, DeviceStatus status) {
        this.macAddress = macAddress;
        this.reading = reading;
        this.status = status;
        this.timestamp = Instant.now();
    }

    public ReadingHistory(String macAddress, SensorReading reading) {
        this(macAddress, reading, DeviceStatus.HEALTHY);
    }
}
