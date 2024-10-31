package com.edgeapi.service.fastporteiot.interfaces.rest.transform;

import com.edgeapi.service.fastporteiot.domain.model.events.ThresholdExceededEvent;
import com.edgeapi.service.fastporteiot.interfaces.rest.resources.DeviceEventResource;

import java.time.Instant;

public class DeviceEventResourceAssembler {
    public static DeviceEventResource toResource(ThresholdExceededEvent event) {
        return new DeviceEventResource(
                event.getMacAddress(),
                "THRESHOLD_EXCEEDED",
                event.getThresholdType(),
                event.getReading().getValueForType(event.getThresholdType()),
                event.getThresholdValue(),
                Instant.now()
        );
    }
}
