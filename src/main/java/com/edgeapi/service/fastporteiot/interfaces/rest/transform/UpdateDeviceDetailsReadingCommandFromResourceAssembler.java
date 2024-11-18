package com.edgeapi.service.fastporteiot.interfaces.rest.transform;

import com.edgeapi.service.fastporteiot.domain.model.commands.UpdateDeviceDetailsReadingCommand;
import com.edgeapi.service.fastporteiot.interfaces.rest.resources.UpdateDeviceDetailsReadingResource;

public class UpdateDeviceDetailsReadingCommandFromResourceAssembler {
    public static UpdateDeviceDetailsReadingCommand toCommandFromResource(String macAddress, UpdateDeviceDetailsReadingResource resource, Integer tripId) {
        return new UpdateDeviceDetailsReadingCommand(
                macAddress,
                resource.toSensorReading(),
                tripId
        );
    }
}
