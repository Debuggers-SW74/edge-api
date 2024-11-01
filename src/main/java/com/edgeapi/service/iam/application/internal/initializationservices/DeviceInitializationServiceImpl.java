package com.edgeapi.service.iam.application.internal.initializationservices;

import com.edgeapi.service.iam.domain.model.commands.SignUpCommand;
import com.edgeapi.service.iam.domain.services.DeviceCommandService;
import com.edgeapi.service.iam.domain.services.DeviceInitializationService;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeviceInitializationServiceImpl implements DeviceInitializationService {
    private final DeviceCommandService deviceCommandService;

    public DeviceInitializationServiceImpl(DeviceCommandService deviceCommandService) {
        this.deviceCommandService = deviceCommandService;
    }

    @PostConstruct
    @Override
    public void init() {
        List<SignUpCommand> devicesToRegister = List.of(
                new SignUpCommand("34:98:7A:1B:2C:3D", "fastporteA1B2C3"),
                new SignUpCommand("A4:B1:C2:D3:E4:F5", "fastporteX9Y8Z7"),
                new SignUpCommand("00:1A:2B:3C:4D:5E", "fastporteM4N5P6"),
                new SignUpCommand("12:34:56:78:9A:BC", "fastporteQ3R2S1"),
                new SignUpCommand("FE:DC:BA:98:76:54", "fastporteJ7K8L9")
        );

        for (SignUpCommand command : devicesToRegister) {
            try {
                deviceCommandService.handle(command);
            } catch (RuntimeException e) {
                System.out.println("The device already exists");
            }
        }
    }
}
