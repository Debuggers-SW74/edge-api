package com.edgeapi.service.fastporteiot.application.external.rest;

import com.edgeapi.service.fastporteiot.domain.model.commands.GetTripDetailsCommand;
import com.edgeapi.service.fastporteiot.domain.model.commands.TripDetailsResponseCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class CloudTripService {
    private final RestTemplate restTemplate;
    private final String cloudApiUrl;
    private final String internalApiKey;

    /**
     * Constructor para inyectar dependencias y configurar el servicio.
     *
     * @param restTemplate instancia de RestTemplate para realizar solicitudes HTTP.
     * @param cloudApiUrl URL base del servicio en la nube.
     * @param internalApiKey clave API interna para autenticación.
     */
    public CloudTripService(
            RestTemplate restTemplate,
            @Value("${iot.cloud.api-url}") String cloudApiUrl,
            @Value("${internal.api-key}") String internalApiKey
    ) {
        this.restTemplate = restTemplate;
        this.cloudApiUrl = cloudApiUrl;
        this.internalApiKey = internalApiKey;
    }


    /**
     * Obtiene los detalles de un viaje desde el servicio en la nube.
     *
     * @param command el comando que contiene los datos necesarios para obtener los detalles del viaje.
     * @return una instancia de TripDetailsResponseCommand con los detalles del viaje.
     */
    public TripDetailsResponseCommand getTripDetails(GetTripDetailsCommand command) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Internal-API-Key", internalApiKey);

        String url = cloudApiUrl + "/api/v1/trips/driver/" + command.driverId();

        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {}
        );

        if (response.getBody() == null || response.getBody().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No trips found for driver ID: " + command.driverId());
        }

        List<Map<String, Object>> tripDataList = response.getBody();
        TripData lastTrip = mapToTripData(tripDataList.getLast());
        return new TripDetailsResponseCommand(
                lastTrip.tripId,
                lastTrip.driverId,
                lastTrip.driverName,
                lastTrip.supervisorId,
                lastTrip.supervisorName
        );
    }

    private static class TripData {
        public Integer tripId;
        public Integer driverId;
        public String driverName;
        public Integer supervisorId;
        public String supervisorName;
    }

    private TripData mapToTripData(Map<String, Object> tripMap) {
        TripData tripData = new TripData();
        tripData.tripId = (Integer) tripMap.get("tripId");
        tripData.driverId = (Integer) tripMap.get("driverId");
        tripData.driverName = (String) tripMap.get("driverName");
        tripData.supervisorId = (Integer) tripMap.get("supervisorId");
        tripData.supervisorName = (String) tripMap.get("supervisorName");
        return tripData;
    }
}
