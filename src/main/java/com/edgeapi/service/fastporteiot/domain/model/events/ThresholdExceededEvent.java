package com.edgeapi.service.fastporteiot.domain.model.events;

import com.edgeapi.service.fastporteiot.domain.model.valueobjects.SensorReading;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ThresholdExceededEvent extends ApplicationEvent {
    private final String macAddress;
    private final SensorReading reading;
    private final String thresholdType;
    private final float thresholdValue;

    public ThresholdExceededEvent(Object source, String macAddress, SensorReading reading, String thresholdType, float thresholdValue) {
        super(source);
        this.macAddress = macAddress;
        this.reading = reading;
        this.thresholdType = thresholdType;
        this.thresholdValue = thresholdValue;
    }
}
