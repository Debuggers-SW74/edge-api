package com.edgeapi.service.fastporteiot.application.external.rest;

import com.edgeapi.service.fastporteiot.domain.model.entities.RealTimeSensorData;
import com.edgeapi.service.fastporteiot.domain.model.entities.SensorReadingWithTripId;
import com.edgeapi.service.fastporteiot.domain.model.valueobjects.SensorReading;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class CloudRealTimeSensorDataService {
    private final RestTemplate restTemplate;
    private final String cloudApiUrl;
    private final String internalApiKey;

    public CloudRealTimeSensorDataService(
            RestTemplate restTemplate,
            @Value("${iot.cloud.api-url}") String cloudApiUrl,
            @Value("${internal.api-key}") String internalApiKey
    ) {
        this.restTemplate = restTemplate;
        this.cloudApiUrl = cloudApiUrl;
        this.internalApiKey = internalApiKey;
    }

    public void sendReadings(List<RealTimeSensorData> sensorReadingEntities) {
        if (sensorReadingEntities.isEmpty()) {
            log.warn("No readings to send.");
            return;
        }

        try {
            // Convert SensorReadingEntity to DTOs or the expected Cloud API model
            List<Map<String, Object>> readingsToSend = sensorReadingEntities.stream().map(entity -> {
                Map<String, Object> reading = new HashMap<>();
                reading.put("tripId", entity.getTripId());
                reading.put("temperatureValue", entity.getTemperatureValue());
                reading.put("humidityValue", entity.getHumidityValue());
                reading.put("pressureValue", entity.getPressureValue());
                reading.put("gasValue", entity.getGasValue());
                reading.put("timestamp", entity.getTimestamp());
                return reading;
            }).toList();

            String url = cloudApiUrl + "/api/v1/sensor-data";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-Internal-API-Key", internalApiKey);

            HttpEntity<List<Map<String, Object>>> requestEntity = new HttpEntity<>(readingsToSend, headers);
            log.info("Sending {} readings to {}", sensorReadingEntities.size(), url);

            ResponseEntity<Void> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    Void.class
            );

            if (response.getStatusCode() == HttpStatus.OK || response.getStatusCode() == HttpStatus.CREATED) {
                log.info("Readings sent successfully.");
            } else {
                throw new RuntimeException("Unexpected response status: " + response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("Failed to send readings", e);
            throw new RuntimeException("Error sending readings: " + e.getMessage());
        }
    }
}
