package com.edgeapi.service.fastporteiot.domain.exceptions;

public class DeviceNotFoundException extends RuntimeException{
    public DeviceNotFoundException(String macAddress) {
        super("Could not find device with MAC address: " + macAddress);
    }
}
