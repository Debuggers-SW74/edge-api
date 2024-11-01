package com.edgeapi.service.fastporteiot.interfaces.rest.transform;

import com.edgeapi.service.fastporteiot.domain.model.aggregates.DeviceDetails;
import com.edgeapi.service.fastporteiot.interfaces.rest.resources.DeviceStateResource;

public class DeviceStateResourceAssembler {
    public static DeviceStateResource toResource(DeviceDetails device) {
        return new DeviceStateResource(
                device.getCurrentReading().temperature(),
                device.getCurrentReading().humidity(),
                device.getCurrentReading().pressure()
        );
    }
}
