package ro.tuc.ds2020.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import ro.tuc.ds2020.authorization.JwtTokenGenerator;
import ro.tuc.ds2020.authorization.JwtTokenValidator;
import ro.tuc.ds2020.dtos.PersonDTO;
import ro.tuc.ds2020.dtos.PersonDetailsDTO;
import ro.tuc.ds2020.services.PersonService;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping(value = "/person")
public class PersonController {

    private final PersonService personService;
    private RestTemplate restTemplate;

    @Autowired
    private final JwtTokenValidator validator;

    @Autowired
    public PersonController(PersonService personService, JwtTokenValidator validator) {

        this.personService = personService;
        this.validator = validator;
        this.restTemplate = new RestTemplate();
    }
    @Autowired
    private JwtTokenGenerator jwtTokenGenerator;

    @GetMapping()
    public ResponseEntity<List<PersonDTO>> getPersons() {
        List<PersonDTO> dtos = personService.findAllPersons();
        for (PersonDTO dto : dtos) {
            Link personLink = linkTo(methodOn(PersonController.class)
                    .getPersonById(dto.getId())).withRel("personDetails");
            dto.add(personLink);
        }
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<PersonDetailsDTO> getPersonById(@PathVariable("id") UUID personId) {
        PersonDetailsDTO dto = personService.findPersonById(personId);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<UUID> insertPerson(@Valid @RequestBody PersonDetailsDTO personDTO) {
        UUID personID = personService.insertPerson(personDTO);
        return new ResponseEntity<>(personID, HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<UUID> updatePerson(@PathVariable("id") UUID personId,
                                             @Valid @RequestBody PersonDetailsDTO personDTO) {
        UUID updatedPersonId = personService.updatePerson(personId, personDTO);
        return new ResponseEntity<>(updatedPersonId, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deletePerson(@PathVariable("id") UUID personId) {
//        restTemplate.delete("http://user.localhost/person-device/deleteWherePerson/" + personId);
//        restTemplate.postForEntity(
//                "http://user.localhost/person-device/deleteWherePerson/" + personId,
//                null,
//                Void.class
//        );
        personService.deletePerson(personId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody PersonDTO personDTO) {
        System.out.println("Received login attempt: " + personDTO.getName() + " " + personDTO.getPassword());
        PersonDTO foundPerson = personService.login(personDTO.getName(), personDTO.getPassword());
        if (foundPerson != null) {
//            return new ResponseEntity<>(foundPerson, HttpStatus.OK);
            String token = jwtTokenGenerator.generateJwtToken(foundPerson.getId(), foundPerson.getName(), foundPerson.getRole());
            Map<String, Object> response = new HashMap<>();
            response.put("user", foundPerson);
            response.put("token", token);
            return ResponseEntity.ok(response);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

    }

    @GetMapping(value = "/username/{id}")
    public ResponseEntity<String> getUsernameById(@PathVariable("id") UUID personId) {
        PersonDetailsDTO dto = personService.findPersonById(personId);
        if (dto != null) {
            return new ResponseEntity<>(dto.getName(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Person not found", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "/id/{username}")
    public ResponseEntity<UUID> getIdByUsername(@PathVariable("username") String username) {
        System.out.println(username);
        PersonDTO person = personService.findPersonByUsername(username);
        System.out.println(person);
        if (person != null) {
            return new ResponseEntity<>(person.getId(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/validate-token")
    public ResponseEntity<Map<String, Object>> validateToken(@RequestHeader("Authorization") String authorizationHeader) {
        Map<String, Object> response = new HashMap<>();
        String token = authorizationHeader.startsWith("Bearer ") ? authorizationHeader.substring(7) : authorizationHeader;

        System.out.println("Token: " + token);
        if (validator.validateJwtToken(token)) {
            String username = validator.extractUsernameFromToken(token);
            String uuid = validator.extractUuidFromToken(token);
            String role = validator.extractRoleFromToken(token);

            response.put("valid", true);
            response.put("username", username);
            response.put("uuid", uuid);
            response.put("role", role);
            return ResponseEntity.ok(response);
        } else {
            response.put("valid", false);
            response.put("message", "Invalid or expired token");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }

}