package com.edgeapi.service.fastporteiot.interfaces.rest.resources;

import com.edgeapi.service.fastporteiot.domain.model.aggregates.DeviceDetails;

public record DeviceStateResource(
        float temperature,
        float humidity,
        float pressure,
        float gas
) {
    public static DeviceStateResource fromDeviceDetails(DeviceDetails device) {
        return new DeviceStateResource(
                device.getCurrentReading().temperature(),
                device.getCurrentReading().humidity(),
                device.getCurrentReading().pressure(),
                device.getCurrentReading().gas()
        );
    }
}
