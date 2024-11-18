package com.edgeapi.service.fastporteiot.interfaces.rest.resources;

public record TripDetailsResource(
        Integer tripId,
        Integer driverId,
        String driverName,
        Integer supervisorId,
        String supervisorName
) { }
