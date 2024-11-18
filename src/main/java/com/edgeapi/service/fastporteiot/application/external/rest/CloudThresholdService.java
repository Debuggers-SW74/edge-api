package com.edgeapi.service.fastporteiot.application.external.rest;

import com.edgeapi.service.fastporteiot.domain.model.entities.ThresholdManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.util.List;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

@Slf4j
@Component
public class CloudThresholdService {
    private final RestTemplate restTemplate;
    private final String cloudApiUrl;
    private final String internalApiKey;

    public CloudThresholdService(
            RestTemplate restTemplate,
            @Value("${iot.cloud.api-url}") String cloudApiUrl,
            @Value("${internal.api-key}") String internalApiKey
    ) {
        this.restTemplate = restTemplate;
        this.cloudApiUrl = cloudApiUrl;
        this.internalApiKey = internalApiKey;
    }


    public List<ThresholdManager> getThresholdsByTripId(Integer tripId) {
        String url = cloudApiUrl + "/api/v1/thresholds/trip/" + tripId;

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Internal-API-Key", internalApiKey);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<List<ThresholdManager>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    requestEntity,
                    new ParameterizedTypeReference<List<ThresholdManager>>() {}
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            } else {
                log.warn("Unexpected response status: {}", response.getStatusCode());
                throw new RuntimeException("Failed to fetch thresholds");
            }
        } catch (HttpClientErrorException e) {
            log.error("Client error: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Error fetching thresholds: " + e.getMessage());
        } catch (HttpServerErrorException e) {
            log.error("Server error: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Server error while fetching thresholds");
        } catch (Exception e) {
            log.error("Unexpected error fetching thresholds", e);
            throw new RuntimeException("Failed to fetch thresholds: " + e.getMessage());
        }
    }
}
