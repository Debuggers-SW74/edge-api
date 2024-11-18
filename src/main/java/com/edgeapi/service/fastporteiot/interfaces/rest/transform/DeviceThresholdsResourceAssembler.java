package com.edgeapi.service.fastporteiot.interfaces.rest.transform;

import com.edgeapi.service.fastporteiot.domain.model.aggregates.DeviceDetails;
import com.edgeapi.service.fastporteiot.domain.model.entities.ThresholdManager;
import com.edgeapi.service.fastporteiot.interfaces.rest.resources.DeviceThresholdsResource;

import java.util.List;

public class DeviceThresholdsResourceAssembler {
    public static DeviceThresholdsResource toResource(DeviceDetails device, List<ThresholdManager> thresholds) {
        float temperatureMax = thresholds.stream()
                .filter(threshold -> "SENSOR_TEMPERATURE".equals(threshold.getSensorType()))
                .map(ThresholdManager::getMaxThreshold)
                .findFirst()
                .orElse(0f);

        float humidityMax = thresholds.stream()
                .filter(threshold -> "SENSOR_HUMIDITY".equals(threshold.getSensorType()))
                .map(ThresholdManager::getMaxThreshold)
                .findFirst()
                .orElse(0f);

        float pressureMax = thresholds.stream()
                .filter(threshold -> "SENSOR_PRESSURE".equals(threshold.getSensorType()))
                .map(ThresholdManager::getMaxThreshold)
                .findFirst()
                .orElse(0f);

        float gasMax = thresholds.stream()
                .filter(threshold -> "SENSOR_GAS".equals(threshold.getSensorType()))
                .map(ThresholdManager::getMaxThreshold)
                .findFirst()
                .orElse(0f);

        return new DeviceThresholdsResource(
                device.getMacAddress(),
                temperatureMax,
                humidityMax,
                pressureMax,
                gasMax
        );
    }
}
