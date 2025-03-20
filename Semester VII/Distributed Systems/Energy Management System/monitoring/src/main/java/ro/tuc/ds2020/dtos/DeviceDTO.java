package ro.tuc.ds2020.dtos;

import java.util.UUID;

public class DeviceDTO {
    UUID deviceId;
    Double maxHourlyConsumption;
    UUID userId;

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
