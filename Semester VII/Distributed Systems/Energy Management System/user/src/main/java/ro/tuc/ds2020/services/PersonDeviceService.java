package ro.tuc.ds2020.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.tuc.ds2020.controllers.handlers.exceptions.model.ResourceNotFoundException;
import ro.tuc.ds2020.dtos.PersonDeviceDTO;
import ro.tuc.ds2020.dtos.PersonDeviceDetailsDTO;
import ro.tuc.ds2020.dtos.builders.PersonDeviceBuilder;
import ro.tuc.ds2020.entities.PersonDevice;
import ro.tuc.ds2020.repositories.PersonDeviceRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PersonDeviceService {

    private final PersonDeviceRepository personDeviceRepository;

    @Autowired
    public PersonDeviceService(PersonDeviceRepository personDeviceRepository) {
        this.personDeviceRepository = personDeviceRepository;
    }

    // Find all devices and convert to PersonDeviceDTO
    public List<PersonDeviceDTO> findAllPersonDevices() {
        List<PersonDevice> personDevices = personDeviceRepository.findAll();
        return personDevices.stream()
                .map(PersonDeviceBuilder::toPersonDeviceDTO)
                .collect(Collectors.toList());
    }

    // Find device by deviceId and convert to PersonDeviceDetailsDTO
    public PersonDeviceDetailsDTO findPersonDeviceById(UUID deviceId) {
        PersonDevice personDevice = personDeviceRepository.findByDeviceId(deviceId)
                .orElseThrow(() -> new ResourceNotFoundException("PersonDevice not found with deviceId " + deviceId));
        return PersonDeviceBuilder.toPersonDeviceDetailsDTO(personDevice);
    }

    // Find devices by personId and convert to List of PersonDeviceDTO
    public List<PersonDeviceDTO> findPersonDevicesByPersonId(UUID personId) {
        List<PersonDevice> personDevices = personDeviceRepository.findByPersonId(personId);
        return personDevices.stream()
                .map(PersonDeviceBuilder::toPersonDeviceDTO)
                .collect(Collectors.toList());
    }

    // Insert a new PersonDevice and return its deviceId
    public UUID insertPersonDevice(PersonDeviceDetailsDTO personDeviceDetailsDTO) {
        PersonDevice personDevice = PersonDeviceBuilder.toEntity(personDeviceDetailsDTO);
        personDevice = personDeviceRepository.save(personDevice);
        return personDevice.getDeviceId();
    }

    // Update an existing PersonDevice by deviceId
    public UUID updatePersonDevice(UUID deviceId, PersonDeviceDetailsDTO personDeviceDetailsDTO) {
        PersonDevice existingPersonDevice = personDeviceRepository.findById(deviceId)
                .orElseThrow(() -> new ResourceNotFoundException("PersonDevice not found with deviceId " + deviceId));

        existingPersonDevice.setPersonId(personDeviceDetailsDTO.getPersonId());

        personDeviceRepository.save(existingPersonDevice);
        return existingPersonDevice.getDeviceId();
    }

    // Delete a PersonDevice by deviceId
    public void deletePersonDevice(UUID deviceId) {
        if (!personDeviceRepository.existsById(deviceId)) {
            throw new ResourceNotFoundException("PersonDevice not found with deviceId " + deviceId);
        }
        personDeviceRepository.deleteById(deviceId);
    }

    public UUID mapDeviceToUser(UUID userId, UUID deviceId) {
        System.out.println(userId);
        System.out.println(deviceId);
        PersonDevice personDevice = new PersonDevice();
        personDevice.setPersonId(userId);
        personDevice.setDeviceId(deviceId);
        PersonDevice savedMapping = personDeviceRepository.save(personDevice);
        return savedMapping.getDeviceId();
    }

    @Transactional
    public void deleteByPersonId(UUID personId) {
        personDeviceRepository.deleteByPersonId(personId);
    }

    @Transactional
    public void deleteByDeviceId(UUID deviceId) {
        personDeviceRepository.deleteByDeviceId(deviceId);
    }
}
