package com.edgeapi.service.fastporteiot.interfaces.rest.transform;

import com.edgeapi.service.fastporteiot.domain.model.aggregates.DeviceDetails;
import com.edgeapi.service.fastporteiot.interfaces.rest.resources.DeviceThresholdsResource;

public class DeviceThresholdsResourceAssembler {
    public static DeviceThresholdsResource toResource(DeviceDetails device) {
        return new DeviceThresholdsResource(
                device.getMacAddress(),
                device.getThresholdSettings().maxTemperature(),
                device.getThresholdSettings().maxHumidity(),
                device.getThresholdSettings().maxPressure()
        );
    }
}
