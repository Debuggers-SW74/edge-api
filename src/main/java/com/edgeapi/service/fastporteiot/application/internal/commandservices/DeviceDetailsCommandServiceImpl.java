package com.edgeapi.service.fastporteiot.application.internal.commandservices;

import com.edgeapi.service.fastporteiot.domain.exceptions.DeviceNotFoundException;
import com.edgeapi.service.fastporteiot.domain.model.aggregates.DeviceDetails;
import com.edgeapi.service.fastporteiot.domain.model.commands.RegisterDeviceDetailsCommand;
import com.edgeapi.service.fastporteiot.domain.model.commands.UpdateDeviceDetailsHealthCommand;
import com.edgeapi.service.fastporteiot.domain.model.commands.UpdateDeviceDetailsReadingCommand;
import com.edgeapi.service.fastporteiot.domain.model.commands.UpdateDeviceDetailsThresholdsCommand;
import com.edgeapi.service.fastporteiot.domain.model.entities.ReadingHistory;
import com.edgeapi.service.fastporteiot.domain.model.events.ThresholdExceededEvent;
import com.edgeapi.service.fastporteiot.domain.model.valueobjects.SensorReading;
import com.edgeapi.service.fastporteiot.domain.model.valueobjects.ThresholdSettings;
import com.edgeapi.service.fastporteiot.domain.services.DeviceDetailsCommandService;
import com.edgeapi.service.fastporteiot.infrastructure.persistence.jpa.repositories.DeviceDetailsRepository;
import com.edgeapi.service.fastporteiot.infrastructure.persistence.jpa.repositories.ReadingHistoryRepository;
import com.edgeapi.service.iam.domain.model.aggregates.Device;
import com.edgeapi.service.iam.infrastructure.persistence.jpa.repositories.DeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class DeviceDetailsCommandServiceImpl implements DeviceDetailsCommandService {
    private final DeviceDetailsRepository deviceDetailsRepository;
    private final ReadingHistoryRepository readingHistoryRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final DeviceRepository deviceRepository;

    @Autowired
    public DeviceDetailsCommandServiceImpl(
            DeviceDetailsRepository deviceDetailsRepository,
            ReadingHistoryRepository readingHistoryRepository,
            ApplicationEventPublisher eventPublisher, DeviceRepository deviceRepository
    ) {
        this.deviceDetailsRepository = deviceDetailsRepository;
        this.readingHistoryRepository = readingHistoryRepository;
        this.eventPublisher = eventPublisher;
        this.deviceRepository = deviceRepository;
    }

    @Override
    public DeviceDetails handle(RegisterDeviceDetailsCommand command) {
        DeviceDetails deviceDetails = new DeviceDetails(
                command.macAddress(),
                command.initialThresholds()
        );
        return deviceDetailsRepository.save(deviceDetails);
    }

    @Override
    public DeviceDetails handle(UpdateDeviceDetailsReadingCommand command) {
        Device device = deviceRepository
                .findByMacAddress(command.macAddress())
                .orElseThrow(() -> new DeviceNotFoundException(command.macAddress()));

        DeviceDetails deviceDetails = deviceDetailsRepository
                .findByMacAddress(command.macAddress())
                .orElseGet(() -> new DeviceDetails(
                        command.macAddress(),
                        new ThresholdSettings(40.0f, 60.0f, 100.0f)
                ));

        ReadingHistory history = new ReadingHistory(
                command.macAddress(),
                command.reading()
        );
        readingHistoryRepository.save(history);

        deviceDetails.updateReading(command.reading());
        return deviceDetailsRepository.save(deviceDetails);
    }

    @Override
    public DeviceDetails handle(UpdateDeviceDetailsThresholdsCommand command) {
        Device device = deviceRepository
                .findByMacAddress(command.macAddress())
                .orElseThrow(() -> new DeviceNotFoundException(command.macAddress()));

        DeviceDetails deviceDetails = deviceDetailsRepository
                .findByMacAddress(command.macAddress())
                .orElseGet(() -> new DeviceDetails(
                        command.macAddress(),
                        new ThresholdSettings(40.0f, 60.0f, 100.0f)
                ));
        deviceDetails.updateThresholds(command.thresholds());
        return deviceDetailsRepository.save(deviceDetails);
    }

    @Override
    public DeviceDetails handle(UpdateDeviceDetailsHealthCommand command) {
        var deviceDetails = deviceDetailsRepository.findByMacAddress(command.macAddress())
                .orElseThrow(() -> new DeviceNotFoundException(command.macAddress()));
        deviceDetails.updateHealth(command.status(), Instant.now());
        return deviceDetailsRepository.save(deviceDetails);
    }
}
