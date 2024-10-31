package com.edgeapi.service.iam.interfaces.rest;

import com.edgeapi.service.iam.domain.model.commands.SignInCommand;
import com.edgeapi.service.iam.domain.model.queries.GetDeviceByMacAddressQuery;
import com.edgeapi.service.iam.domain.model.queries.GetSecretByMacAddressQuery;
import com.edgeapi.service.iam.domain.services.DeviceCommandService;
import com.edgeapi.service.iam.domain.services.DeviceQueryService;
import com.edgeapi.service.iam.infrastructure.tokens.jwt.BearerTokenService;
import com.edgeapi.service.iam.interfaces.rest.resources.AuthenticatedDeviceResource;
import com.edgeapi.service.iam.interfaces.rest.resources.SignInResource;
import com.edgeapi.service.iam.interfaces.rest.resources.SignUpResource;
import com.edgeapi.service.iam.interfaces.rest.resources.DeviceResource;
import com.edgeapi.service.iam.interfaces.rest.transform.AuthenticatedDeviceResourceFromEntityAssembler;
import com.edgeapi.service.iam.interfaces.rest.transform.SignInCommandFromResourceAssembler;
import com.edgeapi.service.iam.interfaces.rest.transform.SignUpCommandFromResourceAssembler;
import com.edgeapi.service.iam.interfaces.rest.transform.DeviceResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication Controller
 * This controller is responsible for handling all the requests related to authentication
 * @version 1.0
 */
@RestController
@RequestMapping(value = "/api/v1/authentication", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Authentication", description = "Authentication Endpoints")
public class AuthenticationController {
    private final DeviceCommandService deviceCommandService;
    private final BearerTokenService tokenService;
    private final DeviceQueryService deviceQueryService;

    public AuthenticationController(DeviceCommandService deviceCommandService, BearerTokenService tokenService, DeviceQueryService deviceQueryService) {
        this.deviceCommandService = deviceCommandService;
        this.tokenService = tokenService;
        this.deviceQueryService = deviceQueryService;
    }

    /**
     * Handles the sign-in request
     * @param signInResource the sign-in request body
     * @return the authenticated device resource
     */
    @PostMapping("/sign-in")
    public ResponseEntity<AuthenticatedDeviceResource> signIn(@RequestBody SignInResource signInResource) {
        var signInCommand = SignInCommandFromResourceAssembler.toCommandFromResource(signInResource);
        var authenticatedDevice = deviceCommandService.handle(signInCommand);
        if(authenticatedDevice.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        var authenticateUserResource = AuthenticatedDeviceResourceFromEntityAssembler.toResourceFromEntity(authenticatedDevice.get().getLeft(), authenticatedDevice.get().getRight());
        return ResponseEntity.ok(authenticateUserResource);

    }

    /**
     * Handles the sign-up request
     * @param signUpResource the sign-up request body
     * @return the created device resource
     */
    @PostMapping("/sign-up")
    public ResponseEntity<AuthenticatedDeviceResource> signUp(@RequestBody SignUpResource signUpResource) {
        var signUpCommand = SignUpCommandFromResourceAssembler.toCommandFromResource(signUpResource);
        var device = deviceCommandService.handle(signUpCommand);

        if (device.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        var signInCommand = new SignInCommand(signUpResource.macAddress(), signUpResource.secret());
        var authenticatedDevice = deviceCommandService.handle(signInCommand);

        if (authenticatedDevice.isEmpty()) {
            return ResponseEntity.internalServerError().build();
        }

        var authenticatedUserResource = AuthenticatedDeviceResourceFromEntityAssembler.toResourceFromEntity(
                authenticatedDevice.get().getLeft(), authenticatedDevice.get().getRight());
        return new ResponseEntity<>(authenticatedUserResource, HttpStatus.CREATED);
    }

    @GetMapping("/token/{macAddress}")
    public ResponseEntity<AuthenticatedDeviceResource> getToken(@PathVariable String macAddress) {
        var query = new GetSecretByMacAddressQuery(macAddress);
        var secretOpt = deviceQueryService.getSecretByMacAddress(query);

        if (secretOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        String secret = secretOpt.get();
        var signInCommand = new SignInCommand(macAddress, secret);
        var authenticatedDevice  = deviceCommandService.handle(signInCommand);

        if (authenticatedDevice .isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        var authenticateUserResource = AuthenticatedDeviceResourceFromEntityAssembler.toResourceFromEntity(
                authenticatedDevice .get().getLeft(), authenticatedDevice .get().getRight());
        return ResponseEntity.ok(authenticateUserResource);
    }

    @GetMapping("/me")
    public ResponseEntity<DeviceResource> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String macAddress = tokenService.getUsernameFromToken(token);
        var device = deviceQueryService.handle(new GetDeviceByMacAddressQuery(macAddress));
        return device.map(value -> ResponseEntity.ok(DeviceResourceFromEntityAssembler.toResourceFromEntity(value))).orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @PostMapping("/validateToken")
    public ResponseEntity<Boolean> validateToken(@RequestParam String token) {
        boolean isValid = tokenService.validateToken(token);
        return ResponseEntity.ok(isValid);
    }
}