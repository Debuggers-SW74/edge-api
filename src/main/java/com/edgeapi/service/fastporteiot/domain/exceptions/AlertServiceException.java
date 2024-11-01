package com.edgeapi.service.fastporteiot.domain.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class AlertServiceException extends RuntimeException {
    public AlertServiceException(String message) {
        super(message);
    }
}
