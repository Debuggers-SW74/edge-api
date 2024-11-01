package com.edgeapi.service.iam.interfaces.rest.transform;

import com.edgeapi.service.iam.domain.model.commands.SignUpCommand;
import com.edgeapi.service.iam.interfaces.rest.resources.SignUpResource;

public class SignUpCommandFromResourceAssembler {
    public static SignUpCommand toCommandFromResource(SignUpResource resource) {
        return new SignUpCommand(resource.macAddress(), resource.secret());
    }
}
