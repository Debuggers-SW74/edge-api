package com.edgeapi.service.fastporteiot.domain.model.commands;

import com.edgeapi.service.fastporteiot.domain.model.valueobjects.ThresholdSettings;

public record RegisterDeviceDetailsCommand(String macAddress, ThresholdSettings initialThresholds) {
}
