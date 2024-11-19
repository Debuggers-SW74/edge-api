package com.edgeapi.service.fastporteiot.application.external.rest;

import com.edgeapi.service.fastporteiot.domain.model.entities.SensorReadingWithTripId;
import com.edgeapi.service.fastporteiot.domain.model.valueobjects.SensorReading;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.springframework.http.HttpStatus;

import java.util.List;

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

    public void sendReadings(List<SensorReadingWithTripId> readings) {
        if (readings == null || readings.isEmpty()) {
            log.warn("No readings to send to Cloud API.");
            return;
        }

        try {
            String url = cloudApiUrl + "/api/v1/sensor-data";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-Internal-API-Key", internalApiKey);

            HttpEntity<List<SensorReadingWithTripId>> requestEntity = new HttpEntity<>(readings, headers);
            log.info("Sending readings to {}", url);

            ResponseEntity<Void> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    Void.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                log.info("Readings sent successfully to Cloud API.");
            } else {
                log.warn("Unexpected response status: {}", response.getStatusCode());
                throw new RuntimeException("Failed to send readings: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            log.error("Client error: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Error sending readings: " + e.getMessage());
        } catch (HttpServerErrorException e) {
            log.error("Server error: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Server error while sending readings");
        } catch (Exception e) {
            log.error("Unexpected error sending readings", e);
            throw new RuntimeException("Failed to send readings: " + e.getMessage());
        }
    }
}
