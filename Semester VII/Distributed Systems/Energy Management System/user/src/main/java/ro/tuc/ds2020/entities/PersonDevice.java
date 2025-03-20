package ro.tuc.ds2020.entities;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@Entity
public class PersonDevice implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "device_id", nullable = false)
    private UUID deviceId;

    @Column(name = "person_id", nullable = false)
    private UUID personId;

    public PersonDevice() {
    }

    public PersonDevice(UUID personId) {
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
}
