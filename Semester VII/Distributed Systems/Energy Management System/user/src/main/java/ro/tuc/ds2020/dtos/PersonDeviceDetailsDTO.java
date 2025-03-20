package ro.tuc.ds2020.dtos;

import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.UUID;

public class PersonDeviceDetailsDTO {

    private UUID deviceId;

    @NotNull
    private UUID personId;

    public PersonDeviceDetailsDTO() {
    }

    public PersonDeviceDetailsDTO(UUID deviceId, UUID personId) {
        this.deviceId = deviceId;
        this.personId = personId;
    }

    public UUID getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(UUID deviceId) {
        this.deviceId = deviceId;
    }

    public UUID getPersonId() {
        return personId;
    }

    public void setPersonId(UUID personId) {
        this.personId = personId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PersonDeviceDetailsDTO that = (PersonDeviceDetailsDTO) o;
        return Objects.equals(deviceId, that.deviceId) &&
                Objects.equals(personId, that.personId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(deviceId, personId);
    }
}
