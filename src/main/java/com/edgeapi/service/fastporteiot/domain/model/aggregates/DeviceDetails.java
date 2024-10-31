package com.edgeapi.service.fastporteiot.domain.model.aggregates;

import com.edgeapi.service.fastporteiot.domain.model.events.ThresholdExceededEvent;
import com.edgeapi.service.fastporteiot.domain.model.valueobjects.DeviceStatus;
import com.edgeapi.service.fastporteiot.domain.model.valueobjects.SensorReading;
import com.edgeapi.service.fastporteiot.domain.model.valueobjects.ThresholdSettings;
import com.edgeapi.service.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "device_details")
public class DeviceDetails extends AuditableAbstractAggregateRoot<DeviceDetails> {
    @Column(name = "devide_mac_address")
    private String macAddress;

    @Embedded
    private SensorReading currentReading;

    @Embedded
    private ThresholdSettings thresholdSettings;

    @Enumerated(EnumType.STRING)
    private DeviceStatus status;

    private Instant lastHealthUpdate;

    public DeviceDetails(String macAddress, ThresholdSettings thresholdSettings) {
        this.macAddress = macAddress;
        this.thresholdSettings = thresholdSettings;
        this.status = DeviceStatus.HEALTHY;
        this.lastHealthUpdate = Instant.now();
    }

    public void updateHealth(DeviceStatus status, Instant timestamp) {
        this.status = status;
        this.lastHealthUpdate = timestamp;
    }

    public void updateReading(SensorReading reading) {
        this.currentReading = reading;
        checkThresholds();
    }

    public void updateThresholds(ThresholdSettings newThresholds) {
        this.thresholdSettings = newThresholds;
        checkThresholds();
    }

    private void checkThresholds() {
        if (currentReading == null || thresholdSettings == null) return;

        if (currentReading.temperature() > thresholdSettings.maxTemperature()) {
            registerEvent(new ThresholdExceededEvent(
                    this,
                    macAddress,
                    currentReading,
                    "TEMPERATURE",
                    thresholdSettings.maxTemperature()
            ));
        }
        if (currentReading.humidity() > thresholdSettings.maxHumidity()) {
            registerEvent(new ThresholdExceededEvent(
                    this,
                    macAddress,
                    currentReading,
                    "HUMIDITY",
                    thresholdSettings.maxHumidity()
            ));
        }
        if (currentReading.pressure() > thresholdSettings.maxPressure()) {
            registerEvent(new ThresholdExceededEvent(
                    this,
                    macAddress,
                    currentReading,
                    "PRESSURE",
                    thresholdSettings.maxPressure()
            ));
        }
    }
}
