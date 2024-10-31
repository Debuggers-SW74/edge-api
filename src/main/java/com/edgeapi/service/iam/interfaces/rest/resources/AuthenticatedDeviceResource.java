package com.edgeapi.service.iam.interfaces.rest.resources;

public record AuthenticatedDeviceResource(Long id, String macAddress, String token) {
}
