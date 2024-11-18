package com.edgeapi.service.fastporteiot.domain.model.entities;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ThresholdManager {
    private Integer id;
    private String sensorType;
    private Double maxThreshold;
    private Double minThreshold;
    private Integer tripId;
}
