package com.edgeapi.service.fastporteiot.domain.model.commands;

import java.time.LocalDateTime;

public record CreateAlertCommand(
        String sensorType,
        Double value,
        LocalDateTime timestamp,
        Long tripId
) {  }
