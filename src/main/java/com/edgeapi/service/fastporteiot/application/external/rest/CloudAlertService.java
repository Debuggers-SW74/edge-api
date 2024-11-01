package com.edgeapi.service.fastporteiot.application.external.rest;

import com.edgeapi.service.fastporteiot.domain.model.commands.CreateAlertCommand;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CloudAlertService {
    private final RestTemplate restTemplate;

    @Value("${iot.cloud.api-url}")
    private String cloudApiUrl;

    public CloudAlertService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void sendAlert(CreateAlertCommand command) {
        ResponseEntity<Void> response = restTemplate.postForEntity(
                cloudApiUrl + "/alerts",
                command,
                Void.class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            System.out.println("Alert sent successfully to Cloud API");
        } else {
            System.err.println("Failed to send alert to Cloud API: " + response.getStatusCode());
        }
    }
}
