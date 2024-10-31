package com.edgeapi.service.fastporteiot.application.internal.eventhandlers;

import com.edgeapi.service.fastporteiot.domain.model.events.ThresholdExceededEvent;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.time.Instant;


@Service
@Slf4j
public class ThresholdExceededEventHandler {
    private static final Logger logger = LoggerFactory.getLogger(ThresholdExceededEventHandler.class);

    @EventListener
    public void handle(ThresholdExceededEvent event) {
        logger.warn("ALERT: {} threshold exceeded for device {}. Current: {}, Max: {}",
                event.getThresholdType(),
                event.getMacAddress(),
                event.getReading().getValueForType(event.getThresholdType()),
                event.getThresholdValue()
        );
    }
}
