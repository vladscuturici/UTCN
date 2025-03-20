package ro.tuc.ds2020.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

public class MeasurementDTO {
    Long measurementId;
    UUID deviceId;
    Double value;
    LocalDateTime timestamp;

    public MeasurementDTO(Long measurementId, UUID deviceId, Double value, LocalDateTime timestamp) {
        this.measurementId = measurementId;
        this.deviceId = deviceId;
        this.value = value;
        this.timestamp = timestamp;
    }

    // Getter and Setter for measurementId
    public Long getMeasurementId() {
        return measurementId;
    }

    public void setMeasurementId(Long measurementId) {
        this.measurementId = measurementId;
    }

    // Getter and Setter for deviceId
    public UUID getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(UUID deviceId) {
        this.deviceId = deviceId;
    }

    // Getter and Setter for value
    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    // Getter and Setter for timestamp
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}