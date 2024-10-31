package com.edgeapi.service.fastporteiot.domain.model.commands;

import com.edgeapi.service.fastporteiot.domain.model.valueobjects.ThresholdSettings;

public record UpdateDeviceDetailsThresholdsCommand(String macAddress, ThresholdSettings thresholds) {
}
