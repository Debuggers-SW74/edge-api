package com.edgeapi.service.fastporteiot.interfaces.rest.resources;

public record DeviceThresholdsResource(
        String macAddress,
        float temperatureMax,
        float humidityMax,
        float pressureMax,
        float gasMax
) { }
