package com.edgeapi.service.fastporteiot.interfaces.rest.transform;

import com.edgeapi.service.fastporteiot.domain.model.commands.TripDetailsResponseCommand;
import com.edgeapi.service.fastporteiot.interfaces.rest.resources.TripDetailsResource;

public class TripDetailsResourceAssembler {
    public static TripDetailsResource toResource(TripDetailsResponseCommand tripData) {
        return new TripDetailsResource(
                tripData.tripId(),
                tripData.driverId(),
                tripData.driverName(),
                tripData.supervisorId(),
                tripData.supervisorName()
        );
    }
}
