package com.edgeapi.service.iam.interfaces.rest;

import com.edgeapi.service.iam.application.internal.commandservices.DeviceCommandServiceImpl;
import com.edgeapi.service.iam.domain.model.queries.GetAllDevicesQuery;
import com.edgeapi.service.iam.domain.model.queries.GetDeviceByIdQuery;
import com.edgeapi.service.iam.domain.services.DeviceQueryService;
import com.edgeapi.service.iam.interfaces.rest.resources.DeviceResource;
import com.edgeapi.service.iam.interfaces.rest.transform.DeviceResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/devices", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Devices", description = "Device Management Endpoints")
public class DeviceController {
    private final DeviceQueryService userQueryService;
    private final DeviceCommandServiceImpl deviceCommandService;

    public DeviceController(DeviceQueryService deviceQueryService, DeviceCommandServiceImpl userCommandService) {
        this.userQueryService = deviceQueryService;
        this.deviceCommandService = userCommandService;
    }

    /**
     * Get all devices
     * @return list of device resources
     */
    @GetMapping("/all")
    public ResponseEntity<List<DeviceResource>> getAllDevices(){
        var getAllDevicesQuery = new GetAllDevicesQuery();
        var devices = userQueryService.handle(getAllDevicesQuery);
        var deviceResources= devices.stream().map(
                DeviceResourceFromEntityAssembler::toResourceFromEntity).toList();
        return ResponseEntity.ok(deviceResources);
    }

    /**
     * this method returns the device with the given id
     * @param id the device id
     * @return the device resource with the given id
     */
    @GetMapping(value = "/{id}")
    public ResponseEntity<DeviceResource> getDeviceById(@PathVariable Long id) {
        var getDeviceByIdQuery = new GetDeviceByIdQuery(id);
        var device = userQueryService.handle(getDeviceByIdQuery);
        return device.map(value -> ResponseEntity.ok(
                DeviceResourceFromEntityAssembler.toResourceFromEntity(value)
        )).orElseGet(() -> ResponseEntity.notFound().build());
    }
}