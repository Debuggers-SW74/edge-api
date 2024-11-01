package com.edgeapi.service.fastporteiot.domain.model.queries;

import java.time.Instant;

public record GetDeviceDetailsReadingHistoryQuery(
        String macAddress,
        Instant startTime,
        Instant endTime
) {
}
