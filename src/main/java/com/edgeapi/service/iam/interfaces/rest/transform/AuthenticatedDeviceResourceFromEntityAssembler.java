package com.edgeapi.service.iam.interfaces.rest.transform;

import com.edgeapi.service.iam.domain.model.aggregates.Device;
import com.edgeapi.service.iam.interfaces.rest.resources.AuthenticatedDeviceResource;

public class AuthenticatedDeviceResourceFromEntityAssembler {
    public static AuthenticatedDeviceResource toResourceFromEntity(Device device, String token) {
        return new AuthenticatedDeviceResource(device.getId(), device.getMacAddress(), token);
    }
}
