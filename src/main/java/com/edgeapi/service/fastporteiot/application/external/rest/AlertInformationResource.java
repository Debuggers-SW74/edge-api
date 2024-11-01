package com.edgeapi.service.fastporteiot.application.external.rest;

public record AlertInformationResource(
        Long id,
        String sensorType,
        Double value,
        String timestamp,
        Long tripId
) {}