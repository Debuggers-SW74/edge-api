package com.edgeapi.service.fastporteiot.domain.model.valueobjects;

import jakarta.persistence.Embeddable;

@Embeddable
public record ThresholdSettings(
        float maxTemperature,
        float maxHumidity,
        float maxPressure,
        float maxGas
) {
    public ThresholdSettings {
        if (maxTemperature < 0 || maxHumidity < 0 || maxPressure < 0 || maxGas < 0) {
            throw new IllegalArgumentException("Threshold values cannot be negative");
        }
    }

    public static ThresholdSettings defaultSettings() {
        return new ThresholdSettings(0.0f, 0.0f, 0.0f, 0.0f);
    }
}
