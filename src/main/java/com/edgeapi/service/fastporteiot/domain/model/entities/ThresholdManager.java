package com.edgeapi.service.fastporteiot.domain.model.entities;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ThresholdManager {
    private Long id;
    private String sensorType;
    private float maxThreshold;
    private float minThreshold;
    private Long tripId;
}
