package ro.tuc.ds2020.dtos.builders;

import ro.tuc.ds2020.dtos.PersonDeviceDTO;
import ro.tuc.ds2020.dtos.PersonDeviceDetailsDTO;
import ro.tuc.ds2020.entities.PersonDevice;

public class PersonDeviceBuilder {

    private PersonDeviceBuilder() {
    }

    public static PersonDeviceDTO toPersonDeviceDTO(PersonDevice personDevice) {
        return new PersonDeviceDTO(personDevice.getDeviceId(), personDevice.getPersonId());
    }

    public static PersonDeviceDetailsDTO toPersonDeviceDetailsDTO(PersonDevice personDevice) {
        return new PersonDeviceDetailsDTO(personDevice.getDeviceId(), personDevice.getPersonId());
    }

    public static PersonDevice toEntity(PersonDeviceDetailsDTO personDeviceDetailsDTO) {
        PersonDevice personDevice = new PersonDevice();
        personDevice.setDeviceId(personDeviceDetailsDTO.getDeviceId());
        personDevice.setPersonId(personDeviceDetailsDTO.getPersonId());
        return personDevice;
    }
}
