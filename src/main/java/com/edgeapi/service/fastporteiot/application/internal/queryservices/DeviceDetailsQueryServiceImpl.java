package com.edgeapi.service.fastporteiot.application.internal.queryservices;

import com.edgeapi.service.fastporteiot.domain.model.aggregates.DeviceDetails;
import com.edgeapi.service.fastporteiot.domain.model.entities.ReadingHistory;
import com.edgeapi.service.fastporteiot.domain.model.queries.GetAllDevicesDetailsQuery;
import com.edgeapi.service.fastporteiot.domain.model.queries.GetDeviceDetailsQuery;
import com.edgeapi.service.fastporteiot.domain.model.queries.GetDeviceDetailsReadingHistoryQuery;
import com.edgeapi.service.fastporteiot.domain.services.DeviceDetailsQueryService;
import com.edgeapi.service.fastporteiot.infrastructure.persistence.jpa.repositories.DeviceDetailsRepository;
import com.edgeapi.service.fastporteiot.infrastructure.persistence.jpa.repositories.ReadingHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DeviceDetailsQueryServiceImpl implements DeviceDetailsQueryService {
    private final DeviceDetailsRepository deviceDetailsRepository;
    private final ReadingHistoryRepository readingHistoryRepository;

    @Autowired
    public DeviceDetailsQueryServiceImpl(
            DeviceDetailsRepository deviceDetailsRepository,
            ReadingHistoryRepository readingHistoryRepository
    ) {
        this.deviceDetailsRepository = deviceDetailsRepository;
        this.readingHistoryRepository = readingHistoryRepository;
    }


    @Override
    public Optional<DeviceDetails> handle(GetDeviceDetailsQuery query) {
        return deviceDetailsRepository.findByMacAddress(query.macAddress());
    }

    @Override
    public List<DeviceDetails> handle(GetAllDevicesDetailsQuery query) {
        return deviceDetailsRepository.findAll();
    }

    @Override
    public List<ReadingHistory> handle(GetDeviceDetailsReadingHistoryQuery query) {
        return readingHistoryRepository.findByMacAddressAndTimestampBetween(
                query.macAddress(),
                query.startTime(),
                query.endTime()
        );
    }
}
