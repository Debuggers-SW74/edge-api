package com.edgeapi.service.fastporteiot.interfaces.rest;

import com.edgeapi.service.fastporteiot.application.external.rest.CloudAlertService;
import com.edgeapi.service.fastporteiot.application.external.rest.CloudRealTimeSensorDataService;
import com.edgeapi.service.fastporteiot.application.external.rest.CloudThresholdService;
import com.edgeapi.service.fastporteiot.application.external.rest.CloudTripService;
import com.edgeapi.service.fastporteiot.domain.exceptions.DeviceNotFoundException;
import com.edgeapi.service.fastporteiot.domain.model.aggregates.DeviceDetails;
import com.edgeapi.service.fastporteiot.domain.model.commands.*;
import com.edgeapi.service.fastporteiot.domain.model.entities.DeviceEvent;
import com.edgeapi.service.fastporteiot.domain.model.entities.RealTimeSensorData;
import com.edgeapi.service.fastporteiot.domain.model.entities.SensorReadingWithTripId;
import com.edgeapi.service.fastporteiot.domain.model.entities.ThresholdManager;
import com.edgeapi.service.fastporteiot.domain.model.events.ThresholdExceededEvent;
import com.edgeapi.service.fastporteiot.domain.model.queries.GetDeviceDetailsQuery;
import com.edgeapi.service.fastporteiot.domain.model.queries.GetDeviceDetailsReadingHistoryQuery;
import com.edgeapi.service.fastporteiot.domain.model.valueobjects.DeviceStatus;
import com.edgeapi.service.fastporteiot.domain.model.valueobjects.SensorReading;
import com.edgeapi.service.fastporteiot.domain.model.valueobjects.ThresholdSettings;
import com.edgeapi.service.fastporteiot.domain.services.DeviceDetailsCommandService;
import com.edgeapi.service.fastporteiot.domain.services.DeviceDetailsQueryService;
import com.edgeapi.service.fastporteiot.infrastructure.persistence.jpa.repositories.DeviceDetailsRepository;
import com.edgeapi.service.fastporteiot.infrastructure.persistence.jpa.repositories.RealTimeSensorDataRepository;
import com.edgeapi.service.fastporteiot.interfaces.rest.resources.*;
import com.edgeapi.service.fastporteiot.interfaces.rest.transform.DeviceStateResourceAssembler;
import com.edgeapi.service.fastporteiot.interfaces.rest.transform.DeviceThresholdsResourceAssembler;
import com.edgeapi.service.fastporteiot.interfaces.rest.transform.TripDetailsResourceAssembler;
import com.edgeapi.service.iam.infrastructure.tokens.jwt.BearerTokenService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(value = "/api/v1/sensors", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Sensor Management", description = "Operations to manage IoT sensors")
public class DeviceDetailsController {
    private final DeviceDetailsCommandService deviceDetailsCommandService;
    private final DeviceDetailsQueryService deviceDetailsQueryService;
    private final BearerTokenService tokenService;
    private static final Logger logger = LoggerFactory.getLogger(DeviceDetailsController.class);
    private final DeviceDetailsRepository deviceDetailsRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final CloudAlertService cloudAlertService;
    private final List<DeviceEvent> eventStore = new ArrayList<>();

    private final CloudTripService cloudTripService;
    private final RealTimeSensorData realTimeSensorData = new RealTimeSensorData();
    private final CloudRealTimeSensorDataService cloudRealTimeSensorDataService;
    private final CloudThresholdService cloudThresholdService;
    private final RealTimeSensorDataRepository realTimeSensorDataRepository;

    @Autowired
    public DeviceDetailsController(DeviceDetailsCommandService deviceDetailsCommandService, DeviceDetailsQueryService deviceDetailsQueryService, ApplicationEventPublisher eventPublisher, BearerTokenService tokenService, DeviceDetailsRepository deviceDetailsRepository, CloudAlertService cloudAlertService, CloudTripService cloudTripService, CloudRealTimeSensorDataService cloudRealTimeSensorDataService, CloudThresholdService cloudThresholdService, RealTimeSensorDataRepository realTimeSensorDataRepository) {
        this.deviceDetailsCommandService = deviceDetailsCommandService;
        this.deviceDetailsQueryService = deviceDetailsQueryService;
        this.eventPublisher = eventPublisher;
        this.tokenService = tokenService;
        this.deviceDetailsRepository = deviceDetailsRepository;
        this.cloudAlertService = cloudAlertService;
        this.cloudTripService = cloudTripService;
        this.cloudRealTimeSensorDataService = cloudRealTimeSensorDataService;
        this.cloudThresholdService = cloudThresholdService;
        this.realTimeSensorDataRepository = realTimeSensorDataRepository;
    }

    private String getMacAddressFromToken(HttpServletRequest request) {
        String token = tokenService.getBearerTokenFrom(request);
        return tokenService.getUsernameFromToken(token); // Asumimos que el username es el macAddress
    }

    @Operation(summary = "Get device current state")
    @ApiResponse(responseCode = "200", description = "Device state retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Device not found")
    @GetMapping("/{macAddress}/state")
    public ResponseEntity<DeviceStateResource> getDeviceState(HttpServletRequest request) {
        String macAddress = getMacAddressFromToken(request);
        GetDeviceDetailsQuery query = new GetDeviceDetailsQuery(macAddress);
        var deviceDetails = deviceDetailsQueryService.handle(query).
                orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Device not found"));
        logger.info("Detail found {}: {}", macAddress, deviceDetails);
        return ResponseEntity.ok(DeviceStateResourceAssembler.toResource(deviceDetails));
    }

    @Operation(summary = "Update device state with new sensor readings")
    @ApiResponse(responseCode = "200", description = "Device state updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid reading values")
    @PostMapping("/{macAddress}/state")
    public ResponseEntity<DeviceStateResource> updateDeviceState(
            HttpServletRequest request,
            @RequestBody SensorReading reading,
            @RequestParam Integer tripId
    ) {
        String macAddress = getMacAddressFromToken(request);
        var command = new UpdateDeviceDetailsReadingCommand(macAddress, reading, tripId);
        var updatedDeviceDetails = deviceDetailsCommandService.handle(command);
        return ResponseEntity.ok(DeviceStateResource.fromDeviceDetails(updatedDeviceDetails));
    }

    @GetMapping("/{macAddress}/health")
    public ResponseEntity<DeviceHealthResource> getDeviceHealth(HttpServletRequest request) {
        String macAddress = getMacAddressFromToken(request);
        GetDeviceDetailsQuery query = new GetDeviceDetailsQuery(macAddress);
        var deviceDetails = deviceDetailsQueryService.handle(query)
                .orElseGet(() -> {
                    RegisterDeviceDetailsCommand command = new RegisterDeviceDetailsCommand(macAddress, ThresholdSettings.defaultSettings());
                    return deviceDetailsCommandService.handle(command);
                });
        var health = new DeviceHealthResource(
                macAddress,
                deviceDetails.getStatus().toString(),
                Instant.now()
        );
        return ResponseEntity.ok(health);
    }

    @PostMapping("/{macAddress}/health")
    public ResponseEntity<DeviceHealthResource> updateDeviceHealth(
            HttpServletRequest request,
            @RequestBody DeviceHealthUpdateResource healthUpdate) {
        String macAddress = getMacAddressFromToken(request);
        GetDeviceDetailsQuery query = new GetDeviceDetailsQuery(macAddress);
        var deviceDetails = deviceDetailsQueryService.handle(query)
                .orElseGet(() -> {
                    RegisterDeviceDetailsCommand command = new RegisterDeviceDetailsCommand(macAddress, ThresholdSettings.defaultSettings());
                    return deviceDetailsCommandService.handle(command);
                });
        logger.info("Received health update for device {}: status={}", macAddress, healthUpdate.status());

        try {
            DeviceStatus newStatus = DeviceStatus.valueOf(healthUpdate.status().toUpperCase());
            if (deviceDetails.getStatus() == newStatus) {
                logger.debug("Device {} health status unchanged: {}", macAddress, newStatus);
                return ResponseEntity.ok(new DeviceHealthResource(
                        macAddress,
                        newStatus.toString(),
                        deviceDetails.getLastHealthUpdate()
                ));
            }

            var command = new UpdateDeviceDetailsHealthCommand(macAddress, newStatus);
            var updatedDeviceDetails = deviceDetailsCommandService.handle(command);

            var healthResource = new DeviceHealthResource(
                    macAddress,
                    updatedDeviceDetails.getStatus().toString(),
                    Instant.now()
            );

            logger.debug("Health update processed successfully for device {}", macAddress);
            return ResponseEntity.ok(healthResource);

        } catch (IllegalArgumentException e) {
            logger.warn("Invalid health status received for device {}: {}. Valid statuses are: {}",
                    macAddress,
                    healthUpdate.status(),
                    Arrays.toString(DeviceStatus.values()));

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid health status. Valid values are: " + Arrays.toString(DeviceStatus.values())
            );
        }
    }

    @GetMapping("/{macAddress}/thresholds")
    public ResponseEntity<DeviceThresholdsResource> getDeviceThresholds(HttpServletRequest request, @RequestParam Integer tripId) {
        String macAddress = getMacAddressFromToken(request);
        GetDeviceDetailsQuery query = new GetDeviceDetailsQuery(macAddress);
        var deviceDetails = deviceDetailsQueryService.handle(query)
                .orElseGet(() -> {
                    RegisterDeviceDetailsCommand command = new RegisterDeviceDetailsCommand(macAddress, ThresholdSettings.defaultSettings());
                    return deviceDetailsCommandService.handle(command);
                });

        List<ThresholdManager> thresholds = cloudThresholdService.getThresholdsByTripId(tripId);
        DeviceThresholdsResource resource = DeviceThresholdsResourceAssembler.toResource(deviceDetails, thresholds);
        return ResponseEntity.ok(resource);
    }

    @GetMapping("/{macAddress}/readings/history")
    @Operation(summary = "Get device reading history within a date range")
    @ApiResponse(responseCode = "200", description = "History retrieved successfully")
    @ApiResponse(responseCode = "400", description = "Invalid date range")
    public ResponseEntity<DeviceDetailsReadingHistoryResource> getDeviceReadingHistory(
            HttpServletRequest request,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        String macAddress = getMacAddressFromToken(request);
        logger.info("Retrieving reading history for device {} from {} to {}", macAddress, startDate, endDate);

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be before end date");
        }

        Instant startTime = startDate.toLocalDate().atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant endTime = endDate.toLocalDate().plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();

        var query = new GetDeviceDetailsReadingHistoryQuery(macAddress, startTime, endTime);
        var history = deviceDetailsQueryService.handle(query);

        return ResponseEntity.ok(new DeviceDetailsReadingHistoryResource(history));
    }

    @PostMapping("/{macAddress}/events")
    @Operation(summary = "Publish device event")
    @ApiResponse(responseCode = "200", description = "Event published successfully")
    @ApiResponse(responseCode = "400", description = "Invalid event data")
    public ResponseEntity<Void> publishDeviceEvent(
            HttpServletRequest request,
            @RequestBody DeviceEventPublishResource eventPublish) {
        String macAddress = getMacAddressFromToken(request);
        logger.info("Received event for device {}: type={}, sensor={}",
                macAddress, eventPublish.eventType(), eventPublish.sensorType());

        try {
            var event = new DeviceEventResource(
                    macAddress,
                    eventPublish.eventType(),
                    eventPublish.sensorType(),
                    eventPublish.currentValue(),
                    eventPublish.thresholdValue(),
                    Instant.now()
            );

            eventStore.add(new DeviceEvent(
                    macAddress,
                    eventPublish.eventType(),
                    eventPublish.sensorType(),
                    eventPublish.currentValue(),
                    eventPublish.thresholdValue(),
                    Instant.now())
            );

            SensorReading reading = new SensorReading(
                    eventPublish.sensorType().equals("SENSOR_TEMPERATURE") ? eventPublish.currentValue() : 0,
                    eventPublish.sensorType().equals("SENSOR_HUMIDITY") ? eventPublish.currentValue() : 0,
                    eventPublish.sensorType().equals("SENSOR_PRESSURE") ? eventPublish.currentValue() : 0,
                    eventPublish.sensorType().equals("SENSOR_GAS") ? eventPublish.currentValue() : 0
            );
            var thresholdEvent = new ThresholdExceededEvent(
                    this,
                    macAddress,
                    reading,
                    eventPublish.sensorType(),
                    eventPublish.thresholdValue()
            );
            eventPublisher.publishEvent(thresholdEvent);

            CreateAlertCommand alertCommand = new CreateAlertCommand(
                    event.sensorType(),
                    (double) event.currentValue(),
                    LocalDateTime.now(),
                    eventPublish.tripId().longValue()
            );
            logger.info("Sending alert command: {}", alertCommand);
            cloudAlertService.sendAlert(alertCommand);

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error processing event for device {}: {}", macAddress, e.getMessage());
            throw e;
        }
    }

    @GetMapping("/{macAddress}/events")
    @Operation(summary = "Get device events")
    @ApiResponse(responseCode = "200", description = "Events retrieved successfully")
    public ResponseEntity<List<DeviceEvent>> getDeviceEvents(HttpServletRequest request) {
        String macAddress = getMacAddressFromToken(request);
        logger.info("Retrieving events for device {}", macAddress);
        List<DeviceEvent> filteredEvents = eventStore.stream()
                .filter(event -> event.getMacAddress().equals(macAddress))
                .toList();

        return ResponseEntity.ok(filteredEvents);
    }

    @Operation(summary = "Get current trip details for a driver")
    @ApiResponse(responseCode = "200", description = "Trip details retrieved successfully")
    @ApiResponse(responseCode = "404", description = "No trips found for the given driver ID")
    @GetMapping("/{macAddress}/trips/current")
    public ResponseEntity<TripDetailsResource> getCurrentTripDetails(
            @RequestParam Integer driverId,
            @RequestParam Integer truckId,
            HttpServletRequest request) {
        String macAddress = getMacAddressFromToken(request);
        var command = new GetTripDetailsCommand(driverId, truckId);
        TripDetailsResponseCommand tripData = cloudTripService.getTripDetails(command);
        TripDetailsResource tripDetailsResource = TripDetailsResourceAssembler.toResource(tripData);
        return ResponseEntity.ok(tripDetailsResource);
    }

    @Operation(summary = "Send accumulated sensor readings to Cloud API")
    @ApiResponse(responseCode = "200", description = "Readings sent successfully")
    @ApiResponse(responseCode = "500", description = "Error sending readings")
    @PostMapping("/{macAddress}/data-stream")
    public ResponseEntity<Void> sendReadings(HttpServletRequest request)  {
        String macAddress = getMacAddressFromToken(request);
        logger.info("Correct device {}", macAddress);

        List<RealTimeSensorData> readings = realTimeSensorDataRepository.findAll();
        if (readings.isEmpty()) {
            logger.warn("No readings available to send for device {}", macAddress);
            return ResponseEntity.badRequest().build();
        }
        try {
            cloudRealTimeSensorDataService.sendReadings(readings);
            realTimeSensorDataRepository.deleteAll(readings);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error sending readings: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
