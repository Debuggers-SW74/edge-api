package com.edgeapi.service.fastporteiot.interfaces.rest.resources;

import java.time.Instant;

public record DeviceHealthResource(
        String macAddress,
        String status,  // HEALTHY, WARNING or INACTIVE
        Instant timestamp
) { }
