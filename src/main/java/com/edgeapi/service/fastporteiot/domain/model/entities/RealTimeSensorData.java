package com.edgeapi.service.fastporteiot.domain.model.entities;

import com.edgeapi.service.fastporteiot.domain.model.valueobjects.SensorReading;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RealTimeSensorData {
    private final List<SensorData> sensorDataList = new ArrayList<>();
    public void addReading(int tripId, SensorReading reading, String timestamp) {
        if (tripId < 0) throw new IllegalArgumentException("TripId cannot be negative");
        if (timestamp == null || timestamp.isBlank()) throw new IllegalArgumentException("Timestamp cannot be null or empty");
        if (reading == null) throw new IllegalArgumentException("SensorReading cannot be null");

        sensorDataList.add(new SensorData(tripId, reading.temperature(), reading.humidity(),
                reading.pressure(), reading.gas(), timestamp));
    }

    public List<SensorData> getSensorDataList() {
        return Collections.unmodifiableList(sensorDataList);
    }

    public void clear() {
        sensorDataList.clear();
    }

    public static class SensorData {
        private final int tripId;
        private final float temperatureValue;
        private final float humidityValue;
        private final float pressureValue;
        private final float gasValue;
        private final String timestamp;

        public SensorData(int tripId, float temperatureValue, float humidityValue, float pressureValue, float gasValue, String timestamp) {
            this.tripId = tripId;
            this.temperatureValue = temperatureValue;
            this.humidityValue = humidityValue;
            this.pressureValue = pressureValue;
            this.gasValue = gasValue;
            this.timestamp = timestamp;
        }

        public int getTripId() {
            return tripId;
        }
        public float getTemperatureValue() {
            return temperatureValue;
        }
        public float getHumidityValue() {
            return humidityValue;
        }
        public float getPressureValue() {
            return pressureValue;
        }
        public float getGasValue() {
            return gasValue;
        }
        public String getTimestamp() {
            return timestamp;
        }
    }
}
