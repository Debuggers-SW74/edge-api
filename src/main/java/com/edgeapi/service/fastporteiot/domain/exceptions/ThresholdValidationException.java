package com.edgeapi.service.fastporteiot.domain.exceptions;

public class ThresholdValidationException extends RuntimeException {
    public ThresholdValidationException(String message) {
        super(message);
    }
}
