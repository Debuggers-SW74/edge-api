package com.edgeapi.service.fastporteiot.interfaces.rest.resources;

public record DeviceEventPublishResource(
        String eventType,
        String sensorType,
        float currentValue,
        float thresholdValue
) {
}
