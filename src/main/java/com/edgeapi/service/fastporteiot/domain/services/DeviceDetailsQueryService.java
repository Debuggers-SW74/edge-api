package com.edgeapi.service.fastporteiot.domain.services;

import com.edgeapi.service.fastporteiot.domain.model.aggregates.DeviceDetails;
import com.edgeapi.service.fastporteiot.domain.model.entities.ReadingHistory;
import com.edgeapi.service.fastporteiot.domain.model.queries.GetAllDevicesDetailsQuery;
import com.edgeapi.service.fastporteiot.domain.model.queries.GetDeviceDetailsQuery;
import com.edgeapi.service.fastporteiot.domain.model.queries.GetDeviceDetailsReadingHistoryQuery;

import java.util.List;
import java.util.Optional;

public interface DeviceDetailsQueryService {
    Optional<DeviceDetails> handle(GetDeviceDetailsQuery query);
    List<DeviceDetails> handle(GetAllDevicesDetailsQuery query);
    List<ReadingHistory> handle(GetDeviceDetailsReadingHistoryQuery query);
}
