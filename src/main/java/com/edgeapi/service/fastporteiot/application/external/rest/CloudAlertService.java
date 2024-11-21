package com.edgeapi.service.fastporteiot.application.external.rest;

import com.edgeapi.service.fastporteiot.domain.exceptions.AlertServiceException;
import com.edgeapi.service.fastporteiot.domain.model.commands.CreateAlertCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.*;
import org.springframework.http.HttpStatus;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
public class CloudAlertService {
    private final RestTemplate restTemplate;
    private final String cloudApiUrl;
    private final String internalApiKey;

    public CloudAlertService(
            RestTemplate restTemplate,
            @Value("${iot.cloud.api-url}") String cloudApiUrl,
            @Value("${internal.api-key}") String internalApiKey
    ) {
        this.restTemplate = restTemplate;
        this.cloudApiUrl = cloudApiUrl;
        this.internalApiKey = internalApiKey;
    }

    /**
     * Envía una alerta al servicio en la nube.
     *
     * @param command el comando que contiene los datos de la alerta a enviar.
     */
    public void sendAlert(CreateAlertCommand command) {
        try {
            // Convertir el comando a un CreateAlertResource
            CreateAlertResource alertResource = new CreateAlertResource(
                    command.sensorType(),
                    command.value(),
                    command.timestamp().format(DateTimeFormatter.ISO_DATE_TIME),
                    command.tripId()
            );

            // Configurar los encabezados de la solicitud HTTP
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-Internal-API-Key", internalApiKey);

            // Construir la URL del endpoint de alertas
            String url = cloudApiUrl + "/api/v1/alerts";

            log.info("Sending alert to {}", url);
            log.info("Alert data: {}", alertResource);

            HttpEntity<CreateAlertResource> requestEntity =
                    new HttpEntity<>(alertResource, headers);

            // Enviar la solicitud HTTP POST al endpoint de alertas
            ResponseEntity<AlertInformationResource> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    AlertInformationResource.class
            );

            // Verificar si la alerta fue creada exitosamente
            if (response.getStatusCode() == HttpStatus.CREATED) {
                log.info("Alert created successfully");
            } else {
                log.warn("Unexpected response status: {}", response.getStatusCode());
            }

        } catch (HttpClientErrorException e) {
            log.error("Client error: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new AlertServiceException("Error creating alert: " + e.getMessage());
        } catch (HttpServerErrorException e) {
            log.error("Server error: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new AlertServiceException("Server error while creating alert");
        } catch (Exception e) {
            log.error("Unexpected error sending alert", e);
            throw new AlertServiceException("Failed to send alert: " + e.getMessage());
        }
    }
}

