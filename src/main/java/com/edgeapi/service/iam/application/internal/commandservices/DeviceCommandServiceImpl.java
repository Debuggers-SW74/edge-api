package com.edgeapi.service.iam.application.internal.commandservices;

import com.edgeapi.service.iam.application.internal.outboundservices.hashing.HashingService;
import com.edgeapi.service.iam.application.internal.outboundservices.tokens.TokenService;
import com.edgeapi.service.iam.domain.model.aggregates.Device;
import com.edgeapi.service.iam.domain.model.commands.SignInCommand;
import com.edgeapi.service.iam.domain.model.commands.SignUpCommand;
import com.edgeapi.service.iam.domain.services.DeviceCommandService;
import com.edgeapi.service.iam.infrastructure.encryption.EncryptionService;
import com.edgeapi.service.iam.infrastructure.persistence.jpa.repositories.DeviceRepository;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Device command service implementation
 * <p>
 *     This class implements the {@link DeviceCommandService} interface and provides the implementation for the
 *     {@link SignInCommand} and {@link SignUpCommand} commands.
 * </p>
 * @version 1.0.0
 */
@Service
public class DeviceCommandServiceImpl implements DeviceCommandService {
    private final DeviceRepository deviceRepository;
    private final HashingService hashingService;
    private final TokenService tokenService;
    private final EncryptionService encryptionService;

    public DeviceCommandServiceImpl(DeviceRepository deviceRepository, HashingService hashingService, TokenService tokenService, EncryptionService encryptionService) {
        this.deviceRepository = deviceRepository;
        this.hashingService = hashingService;
        this.tokenService = tokenService;
        this.encryptionService = encryptionService;
    }

    /**
     * Handle the sign-in command
     * <p>
     *     This method handles the {@link SignInCommand} command and returns the device and the token.
     *     If the device is not found or the secret is invalid, a {@link RuntimeException} will be thrown.
     * </p>
     * @param command the sign-in command containing the deviceId and secret
     * @return and optional containing the device matching the deviceId and the generated token
     * @throws RuntimeException if the device is not found or the secret is invalid
     */
    @Override
    public Optional<ImmutablePair<Device, String>> handle(SignInCommand command) {
        var device = deviceRepository.findByMacAddress(command.macAddress());

        if (device.isEmpty())
            throw new RuntimeException("Device not found");

        String decryptedSecret;
        try {
            decryptedSecret = encryptionService.decrypt(device.get().getSecret());
        } catch (Exception e) {
            throw new RuntimeException("Error decrypting secret", e);
        }

        if (!command.secret().equals(decryptedSecret)) {
            throw new RuntimeException("Invalid secret");
        }

        var token = tokenService.generateToken(device.get().getMacAddress());
        return Optional.of(ImmutablePair.of(device.get(), token));
    }

    /**
     * Handle the sign-up command
     * <p>
     *     This method handles the {@link SignUpCommand} command and creates a new device.
     * </p>
     * @param command the sign-up command containing the deviceId and secret
     * @return an optional containing the created user
     */
    @Override
    public Optional<Device> handle(SignUpCommand command) {
        if(deviceRepository.existsByMacAddress(command.macAddress())) {
            throw new RuntimeException("Device already exists");
        }
        String encryptedSecret = encryptionService.encrypt(command.secret());
        var device = new Device(command.macAddress(), encryptedSecret);
        deviceRepository.save(device);
        return deviceRepository.findByMacAddress(command.macAddress());
    }
}
