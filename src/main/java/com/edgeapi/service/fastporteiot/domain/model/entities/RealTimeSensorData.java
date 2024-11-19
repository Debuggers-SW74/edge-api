package com.edgeapi.service.fastporteiot.domain.model.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;


@Entity(name = "real_time_sensor_data")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RealTimeSensorData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int tripId;
    private float temperatureValue;
    private float humidityValue;
    private float pressureValue;
    private float gasValue;
    private String timestamp;
}
