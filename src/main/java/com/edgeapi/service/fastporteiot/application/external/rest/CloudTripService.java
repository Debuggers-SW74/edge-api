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

@Slf4j
@Component
public class CloudTripService {
    private final RestTemplate restTemplate;
    private final String cloudApiUrl;
    private final String internalApiKey;

    public CloudTripService(
            RestTemplate restTemplate,
            @Value("${iot.cloud.api-url}") String cloudApiUrl,
            @Value("${internal.api-key}") String internalApiKey
    ) {
        this.restTemplate = restTemplate;
        this.cloudApiUrl = cloudApiUrl;
        this.internalApiKey = internalApiKey;
    }


    public TripDetailsResponseCommand getTripDetails(GetTripDetailsCommand command) {
        String url = cloudApiUrl + "/api/v1/trips/driver/" + command.driverId();
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Internal-API-Key", internalApiKey);

        ResponseEntity<List<TripData>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {}
        );

        if (response.getBody() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No trips found for driver ID: " + command.driverId());
        }

        List<TripData> tripDataList = response.getBody();
        if (tripDataList == null || tripDataList.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No trips found for driver ID: " + command.driverId());
        }

        TripData lastTrip = response.getBody().getLast();
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
}
