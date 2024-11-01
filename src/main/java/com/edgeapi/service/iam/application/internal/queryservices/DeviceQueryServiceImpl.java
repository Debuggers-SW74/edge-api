package com.edgeapi.service.iam.application.internal.queryservices;

import com.edgeapi.service.iam.domain.model.aggregates.Device;
import com.edgeapi.service.iam.domain.model.queries.GetAllDevicesQuery;
import com.edgeapi.service.iam.domain.model.queries.GetDeviceByIdQuery;
import com.edgeapi.service.iam.domain.model.queries.GetDeviceByMacAddressQuery;
import com.edgeapi.service.iam.domain.model.queries.GetSecretByMacAddressQuery;
import com.edgeapi.service.iam.domain.services.DeviceQueryService;
import com.edgeapi.service.iam.infrastructure.encryption.EncryptionService;
import com.edgeapi.service.iam.infrastructure.persistence.jpa.repositories.DeviceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of {@link DeviceQueryService} interface.
 * @version 1.0
 */
@Service
public class DeviceQueryServiceImpl implements DeviceQueryService {
    private final DeviceRepository deviceRepository;
    private final EncryptionService encryptionService;
    public DeviceQueryServiceImpl(DeviceRepository deviceRepository, EncryptionService encryptionService) {
        this.deviceRepository = deviceRepository;
        this.encryptionService = encryptionService;
    }

    /**
     * This method is used to handle {@link GetAllDevicesQuery} query.
     * @param query {@link GetAllDevicesQuery} instance.
     * @return {@link List} of {@link Device} instances.
     */
    @Override
    public List<Device> handle(GetAllDevicesQuery query) {
        return deviceRepository.findAll();
    }

    /**
     * This method is used to handle {@link GetDeviceByIdQuery} query.
     * @param query {@link GetDeviceByIdQuery} instance.
     * @return {@link Optional} of {@link Device} instance.
     */
    @Override
    public Optional<Device> handle(GetDeviceByIdQuery query) {
        return deviceRepository.findById(query.id());
    }

    /**
     * This method is used to handle {@link GetDeviceByMacAddressQuery} query.
     * @param query {@link GetDeviceByMacAddressQuery} instance.
     * @return {@link Optional} of {@link Device} instance.
     */
    @Override
    public Optional<Device> handle(GetDeviceByMacAddressQuery query) {
        return deviceRepository.findByMacAddress(query.macAddress());
    }

    @Override
    public Optional<String> getSecretByMacAddress(GetSecretByMacAddressQuery query) {
        var device = deviceRepository.findByMacAddress(query.macAddress());
        if (device.isPresent()) {
            try {
                return Optional.of(encryptionService.decrypt(device.get().getSecret()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return Optional.empty();
    }
}