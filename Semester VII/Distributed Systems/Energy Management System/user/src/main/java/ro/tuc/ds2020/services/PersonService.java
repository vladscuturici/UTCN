package ro.tuc.ds2020.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ro.tuc.ds2020.controllers.handlers.exceptions.model.ResourceNotFoundException;
import ro.tuc.ds2020.dtos.PersonDTO;
import ro.tuc.ds2020.dtos.PersonDetailsDTO;
import ro.tuc.ds2020.dtos.builders.PersonBuilder;
import ro.tuc.ds2020.entities.Person;
import ro.tuc.ds2020.repositories.PersonRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PersonService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PersonService.class);
    private final PersonRepository personRepository;

    @Autowired
    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    // Create
    public UUID insertPerson(PersonDetailsDTO personDTO) {
        Person person = PersonBuilder.toEntity(personDTO);
        person = personRepository.save(person);
        LOGGER.debug("Person with id {} was inserted in db", person.getId());
        return person.getId();
    }

    // Read all
    public List<PersonDTO> findAllPersons() {
        List<Person> personList = personRepository.findAll();
        return personList.stream()
                .map(PersonBuilder::toPersonDTO)
                .collect(Collectors.toList());
    }

    // Read by ID
    public PersonDetailsDTO findPersonById(UUID id) {
        Optional<Person> personOptional = personRepository.findById(id);
        if (!personOptional.isPresent()) {
            LOGGER.error("Person with id {} was not found in db", id);
            throw new ResourceNotFoundException(Person.class.getSimpleName() + " with id: " + id);
        }
        return PersonBuilder.toPersonDetailsDTO(personOptional.get());
    }

    // Update
    public UUID updatePerson(UUID id, PersonDetailsDTO personDTO) {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Person not found with id " + id));

        person.setName(personDTO.getName());
        person.setPassword(personDTO.getPassword());
        person.setRole(personDTO.getRole());

        person = personRepository.save(person);
        LOGGER.debug("Person with id {} was updated in db", person.getId());
        return person.getId();
    }

    // Delete
    public void deletePerson(UUID id) {
        if (!personRepository.existsById(id)) {
            throw new ResourceNotFoundException("Person not found with id " + id);
        }
        personRepository.deleteById(id);
        LOGGER.debug("Person with id {} was deleted from db", id);
    }

    public PersonDTO login(String name, String password) {
        return personRepository.findByName(name)
                .filter(person -> person.getPassword().equals(password))
                .map(PersonBuilder::toPersonDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid username or password"));
    }

    public PersonDTO findPersonByUsername(String username) {
        Person person = personRepository.findByName(username)
                .orElseThrow(() -> new ResourceNotFoundException("Person not found with username: " + username));
        return PersonBuilder.toPersonDTO(person);
    }

}
