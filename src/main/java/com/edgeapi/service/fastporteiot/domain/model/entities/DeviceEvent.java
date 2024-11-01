package com.edgeapi.service.fastporteiot.domain.model.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "device_events")
public class DeviceEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String macAddress;
    private String eventType;
    private String sensorType;
    private float currentValue;
    private float thresholdValue;
    private Instant timestamp;

    public DeviceEvent(String macAddress, String eventType, String sensorType, float currentValue, float thresholdValue, Instant timestamp) {
        this.macAddress = macAddress;
        this.eventType = eventType;
        this.sensorType = sensorType;
        this.currentValue = currentValue;
        this.thresholdValue = thresholdValue;
        this.timestamp = timestamp;
    }

}
