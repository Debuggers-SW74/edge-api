package com.edgeapi.service.fastporteiot.domain.model.commands;

import com.edgeapi.service.fastporteiot.domain.model.valueobjects.DeviceStatus;

public record UpdateDeviceDetailsHealthCommand(
        String macAddress,
        DeviceStatus status
)  { }
