package com.edgeapi.service.fastporteiot.interfaces.rest.transform;

import com.edgeapi.service.fastporteiot.domain.model.commands.UpdateDeviceDetailsThresholdsCommand;
import com.edgeapi.service.fastporteiot.interfaces.rest.resources.UpdateDeviceDetailsThresholdsResource;

public class UpdateDeviceThresholdsCommandFromResourceAssembler {
    public static UpdateDeviceDetailsThresholdsCommand toCommandFromResource(String macAddress, UpdateDeviceDetailsThresholdsResource resource) {
        return new UpdateDeviceDetailsThresholdsCommand(
                macAddress,
                resource.thresholds()
        );
    }
}
