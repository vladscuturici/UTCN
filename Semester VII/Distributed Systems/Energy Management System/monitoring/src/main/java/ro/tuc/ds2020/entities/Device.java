package ro.tuc.ds2020.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "device")
public class Device {

    @Id
    @Column(name = "deviceId", nullable = false)
    private UUID deviceId;

    @Column(name = "maxHourlyConsumption", nullable = false)
    private Double maxHourlyConsumption;

    @Column(name = "userId", nullable = false)
    private UUID userId;

    // Default Constructor
    public Device() {
    }

    // All-Args Constructor
    public Device(UUID deviceId, Double maxHourlyConsumption, UUID userId) {
        this.deviceId = deviceId;
        this.maxHourlyConsumption = maxHourlyConsumption;
        this.userId = userId;
    }

    // Getter and Setter for deviceId
    public UUID getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(UUID deviceId) {
        this.deviceId = deviceId;
    }

    // Getter and Setter for maxHourlyConsumption
    public Double getMaxHourlyConsumption() {
        return maxHourlyConsumption;
    }

    public void setMaxHourlyConsumption(Double maxHourlyConsumption) {
        this.maxHourlyConsumption = maxHourlyConsumption;
    }

    // Getter and Setter for userId
    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}
