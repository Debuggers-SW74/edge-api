package com.edgeapi.service.fastporteiot.application.internal.commandservices;

import com.edgeapi.service.fastporteiot.application.external.rest.CloudThresholdService;
import com.edgeapi.service.fastporteiot.domain.exceptions.DeviceNotFoundException;
import com.edgeapi.service.fastporteiot.domain.model.aggregates.DeviceDetails;
import com.edgeapi.service.fastporteiot.domain.model.commands.RegisterDeviceDetailsCommand;
import com.edgeapi.service.fastporteiot.domain.model.commands.UpdateDeviceDetailsHealthCommand;
import com.edgeapi.service.fastporteiot.domain.model.commands.UpdateDeviceDetailsReadingCommand;
import com.edgeapi.service.fastporteiot.domain.model.entities.ReadingHistory;
import com.edgeapi.service.fastporteiot.domain.model.entities.RealTimeSensorData;
import com.edgeapi.service.fastporteiot.domain.model.entities.ThresholdManager;
import com.edgeapi.service.fastporteiot.domain.model.valueobjects.ThresholdSettings;
import com.edgeapi.service.fastporteiot.domain.services.DeviceDetailsCommandService;
import com.edgeapi.service.fastporteiot.infrastructure.persistence.jpa.repositories.DeviceDetailsRepository;
import com.edgeapi.service.fastporteiot.infrastructure.persistence.jpa.repositories.ReadingHistoryRepository;
import com.edgeapi.service.fastporteiot.infrastructure.persistence.jpa.repositories.RealTimeSensorDataRepository;
import com.edgeapi.service.iam.infrastructure.persistence.jpa.repositories.DeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class DeviceDetailsCommandServiceImpl implements DeviceDetailsCommandService {
    private final DeviceDetailsRepository deviceDetailsRepository;
    private final ReadingHistoryRepository readingHistoryRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final DeviceRepository deviceRepository;
    private final CloudThresholdService cloudThresholdService;

    private final RealTimeSensorData realTimeSensorData = new RealTimeSensorData();
    private final RealTimeSensorDataRepository realTimeSensorDataRepository;


    /**
     * Constructor para inyectar dependencias y configurar el servicio.
     *
     * @param deviceDetailsRepository repositorio para acceder a los detalles del dispositivo.
     * @param readingHistoryRepository repositorio para acceder al historial de lecturas.
     * @param eventPublisher publicador de eventos de la aplicación.
     * @param deviceRepository repositorio para acceder a los dispositivos.
     * @param cloudThresholdService servicio para gestionar umbrales en la nube.
     * @param realTimeSensorDataRepository repositorio para acceder a los datos de sensores en tiempo real.
     */
    @Autowired
    public DeviceDetailsCommandServiceImpl(
            DeviceDetailsRepository deviceDetailsRepository,
            ReadingHistoryRepository readingHistoryRepository,
            ApplicationEventPublisher eventPublisher, DeviceRepository deviceRepository, CloudThresholdService cloudThresholdService, RealTimeSensorDataRepository realTimeSensorDataRepository
    ) {
        this.deviceDetailsRepository = deviceDetailsRepository;
        this.readingHistoryRepository = readingHistoryRepository;
        this.eventPublisher = eventPublisher;
        this.deviceRepository = deviceRepository;
        this.cloudThresholdService = cloudThresholdService;
        this.realTimeSensorDataRepository = realTimeSensorDataRepository;
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
        String timestamp = Instant.now().toString();
        RealTimeSensorData entity = new RealTimeSensorData();
        entity.setTripId(command.tripId());
        entity.setTemperatureValue(command.reading().temperature());
        entity.setHumidityValue(command.reading().humidity());
        entity.setPressureValue(command.reading().pressure());
        entity.setGasValue(command.reading().gas());
        entity.setTimestamp(timestamp);
        realTimeSensorDataRepository.save(entity);

        DeviceDetails deviceDetails = deviceDetailsRepository
                .findByMacAddress(command.macAddress())
                .orElseGet(() -> new DeviceDetails(
                        command.macAddress(),
                        new ThresholdSettings(40.0f, 60.0f, 100.0f, 50.0f)
                ));

        List<ThresholdManager> thresholds = cloudThresholdService.getThresholdsByTripId(command.tripId());
        ThresholdSettings newThresholdSettings = transformToThresholdSettings(thresholds);
        deviceDetails.updateThresholds(newThresholdSettings);

        ReadingHistory history = new ReadingHistory(
                command.macAddress(),
                command.reading(),
                deviceDetails
        );
        readingHistoryRepository.save(history);

        deviceDetails.updateReading(command.reading());
        return deviceDetailsRepository.save(deviceDetails);
    }

    /*
    @Override
    public DeviceDetails handle(UpdateDeviceDetailsThresholdsCommand command) {
        Devices device = deviceRepository
                .findByMacAddress(command.macAddress())
                .orElseThrow(() -> new DeviceNotFoundException(command.macAddress()));

        DeviceDetails deviceDetails = deviceDetailsRepository
                .findByMacAddress(command.macAddress())
                .orElseGet(() -> new DeviceDetails(
                        command.macAddress(),
                        new ThresholdSettings(
                                40.0f,
                                60.0f,
                                100.0f,
                                50.0f
                        )
                ));
        deviceDetails.updateThresholds(command.thresholds());
        return deviceDetailsRepository.save(deviceDetails);
    }*/

    @Override
    public DeviceDetails handle(UpdateDeviceDetailsHealthCommand command) {
        var deviceDetails = deviceDetailsRepository.findByMacAddress(command.macAddress())
                .orElseThrow(() -> new DeviceNotFoundException(command.macAddress()));
        deviceDetails.updateHealth(command.status(), Instant.now());
        return deviceDetailsRepository.save(deviceDetails);
    }

    private ThresholdSettings transformToThresholdSettings(List<ThresholdManager> thresholds) {
        Double maxTemperature = thresholds.stream()
                .filter(threshold -> "SENSOR_TEMPERATURE".equals(threshold.getSensorType()))
                .map(ThresholdManager::getMaxThreshold)
                .findFirst()
                .orElse(0.0);

        Double maxHumidity = thresholds.stream()
                .filter(threshold -> "SENSOR_HUMIDITY".equals(threshold.getSensorType()))
                .map(ThresholdManager::getMaxThreshold)
                .findFirst()
                .orElse(0.0);

        Double maxPressure = thresholds.stream()
                .filter(threshold -> "SENSOR_PRESSURE".equals(threshold.getSensorType()))
                .map(ThresholdManager::getMaxThreshold)
                .findFirst()
                .orElse(0.0);

        Double maxGas = thresholds.stream()
                .filter(threshold -> "SENSOR_GAS".equals(threshold.getSensorType()))
                .map(ThresholdManager::getMaxThreshold)
                .findFirst()
                .orElse(0.0);
        return new ThresholdSettings(maxTemperature.floatValue(), maxHumidity.floatValue(), maxPressure.floatValue(), maxGas.floatValue());
    }
}
