package com.edgeapi.service.fastporteiot.domain.model.commands;

import com.edgeapi.service.fastporteiot.domain.model.valueobjects.SensorReading;

public record UpdateDeviceDetailsReadingCommand(String macAddress, SensorReading reading, Integer tripId) {
}
