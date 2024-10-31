package com.edgeapi.service.fastporteiot.interfaces.rest.transform;

import com.edgeapi.service.fastporteiot.domain.model.commands.UpdateDeviceDetailsReadingCommand;
import com.edgeapi.service.fastporteiot.interfaces.rest.resources.UpdateDeviceDetailsReadingResource;

public class UpdateDeviceDetailsReadingCommandFromResourceAssembler {
    public static UpdateDeviceDetailsReadingCommand toCommandFromResource(String macAddress, UpdateDeviceDetailsReadingResource resource) {
        return new UpdateDeviceDetailsReadingCommand(
                macAddress,
                resource.toSensorReading()
        );
    }
}
