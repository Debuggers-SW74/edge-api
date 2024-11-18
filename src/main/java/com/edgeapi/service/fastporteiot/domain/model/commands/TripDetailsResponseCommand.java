package com.edgeapi.service.fastporteiot.domain.model.commands;

public record TripDetailsResponseCommand(
        Integer tripId,
        Integer driverId,
        String driverName,
        Integer supervisorId,
        String supervisorName
) { }
