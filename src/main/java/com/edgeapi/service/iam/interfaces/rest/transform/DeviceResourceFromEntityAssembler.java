package com.edgeapi.service.iam.interfaces.rest.transform;

import com.edgeapi.service.iam.domain.model.aggregates.Device;
import com.edgeapi.service.iam.interfaces.rest.resources.DeviceResource;

public class DeviceResourceFromEntityAssembler {
    public static DeviceResource toResourceFromEntity(Device device) {
        return new DeviceResource(device.getId(), device.getMacAddress());
    }
}
