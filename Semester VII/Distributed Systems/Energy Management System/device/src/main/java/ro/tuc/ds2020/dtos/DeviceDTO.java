package ro.tuc.ds2020.dtos;

import org.springframework.hateoas.RepresentationModel;

import java.util.Objects;
import java.util.UUID;

public class DeviceDTO extends RepresentationModel<DeviceDTO> {
    private UUID id;
    private String description;
    private String address;
    private Double maxHourlyEnergyConsumption;

    public DeviceDTO() {
    }

//    public DeviceDTO(UUID id, String description, String address, Double maxHourlyEnergyConsumption) {
//        this.id = id;
//        this.description = description;
//        this.address = address;
//        this.maxHourlyEnergyConsumption = maxHourlyEnergyConsumption
//    }

    public DeviceDTO(UUID id, String description, String address, Double maxHourlyEnergyConsumption) {
        this.id = id;
        this.description = description;
        this.address = address;
        this.maxHourlyEnergyConsumption = maxHourlyEnergyConsumption;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getMaxHourlyEnergyConsumption() {
        return maxHourlyEnergyConsumption;
    }

    public void setMaxHourlyEnergyConsumption(Double maxHourlyEnergyConsumption) {
        this.maxHourlyEnergyConsumption = maxHourlyEnergyConsumption;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeviceDTO deviceDTO = (DeviceDTO) o;
        return Objects.equals(id, deviceDTO.id) &&
                Objects.equals(description, deviceDTO.description) &&
                Objects.equals(address, deviceDTO.address) &&
                Objects.equals(maxHourlyEnergyConsumption, deviceDTO.maxHourlyEnergyConsumption);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, description, address, maxHourlyEnergyConsumption);
    }
}
