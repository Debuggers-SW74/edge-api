package com.edgeapi.service.iam.domain.model.commands;

public record SignInCommand(String macAddress, String secret) {
}
