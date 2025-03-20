package ro.tuc.ds2020.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "measurement")
public class Measurement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "measurement_id", nullable = false, updatable = false)
    private Long measurementId;

    @Column(name = "deviceId", nullable = false)
    private UUID deviceId;

    @Column(name = "value", nullable = false)
    private Double value;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    // Default Constructor
    public Measurement() {
    }

    // All-Args Constructor
    public Measurement(Long measurementId, UUID deviceId, Double value, LocalDateTime timestamp) {
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
