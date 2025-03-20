package ro.tuc.ds2020.dtos;

import org.springframework.hateoas.RepresentationModel;

import java.util.Objects;
import java.util.UUID;

public class PersonDeviceDTO extends RepresentationModel<PersonDeviceDTO> {

    private UUID deviceId;
    private UUID personId;

    public PersonDeviceDTO() {
    }

    public PersonDeviceDTO(UUID deviceId, UUID personId) {
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
        PersonDeviceDTO that = (PersonDeviceDTO) o;
        return Objects.equals(deviceId, that.deviceId) &&
                Objects.equals(personId, that.personId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(deviceId, personId);
    }
}
