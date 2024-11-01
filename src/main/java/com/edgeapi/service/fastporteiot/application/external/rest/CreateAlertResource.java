package com.edgeapi.service.fastporteiot.application.external.rest;

public record CreateAlertResource(
        String sensorType,
        Double value,
        String timestamp,
        Long tripId
) {}
