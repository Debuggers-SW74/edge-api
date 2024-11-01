package com.edgeapi.service.iam.domain.services;

import com.edgeapi.service.iam.domain.model.aggregates.Device;
import com.edgeapi.service.iam.domain.model.commands.SignInCommand;
import com.edgeapi.service.iam.domain.model.commands.SignUpCommand;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.Optional;

public interface DeviceCommandService {
    Optional<ImmutablePair<Device, String>> handle(SignInCommand command);
    Optional<Device> handle(SignUpCommand command);
}
