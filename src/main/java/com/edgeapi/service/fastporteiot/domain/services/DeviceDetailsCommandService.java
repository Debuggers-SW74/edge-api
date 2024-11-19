package com.edgeapi.service.fastporteiot.domain.services;

import com.edgeapi.service.fastporteiot.domain.model.aggregates.DeviceDetails;
import com.edgeapi.service.fastporteiot.domain.model.commands.RegisterDeviceDetailsCommand;
import com.edgeapi.service.fastporteiot.domain.model.commands.UpdateDeviceDetailsHealthCommand;
import com.edgeapi.service.fastporteiot.domain.model.commands.UpdateDeviceDetailsReadingCommand;

public interface DeviceDetailsCommandService {
    DeviceDetails handle(RegisterDeviceDetailsCommand command);
    DeviceDetails handle(UpdateDeviceDetailsReadingCommand command);
    DeviceDetails handle(UpdateDeviceDetailsHealthCommand command);
}
