package com.edgeapi.service.fastporteiot.interfaces.rest.resources;

import java.time.Instant;

public record DeviceEventResource(
        String macAddress,
        String eventType,    // THRESHOLD_EXCEEDED
        String sensorType,   // TEMPERATURE, HUMIDITY, PRESSURE and GAS
        float currentValue,
        float thresholdValue,
        Instant timestamp
) { }
