    package ro.tuc.ds2020.controllers;

    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.hateoas.Link;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;
    import ro.tuc.ds2020.dtos.PersonDeviceDTO;
    import ro.tuc.ds2020.dtos.PersonDeviceDetailsDTO;
    import ro.tuc.ds2020.services.PersonDeviceService;

    import javax.validation.Valid;
    import java.util.List;
    import java.util.Map;
    import java.util.UUID;

    import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
    import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

    @RestController
    @CrossOrigin
    @RequestMapping(value = "/person-device")
    public class PersonDeviceController {

        private final PersonDeviceService personDeviceService;

        @Autowired
        public PersonDeviceController(PersonDeviceService personDeviceService) {
            this.personDeviceService = personDeviceService;
        }

        @GetMapping()
        public ResponseEntity<List<PersonDeviceDTO>> getAllPersonDevices() {
            List<PersonDeviceDTO> dtos = personDeviceService.findAllPersonDevices();
            for (PersonDeviceDTO dto : dtos) {
                Link link = linkTo(methodOn(PersonDeviceController.class)
                        .getPersonDeviceById(dto.getDeviceId())).withRel("personDeviceDetails");
                dto.add(link);
            }
            return new ResponseEntity<>(dtos, HttpStatus.OK);
        }

        @GetMapping(value = "/{id}")
        public ResponseEntity<PersonDeviceDetailsDTO> getPersonDeviceById(@PathVariable("id") UUID deviceId) {
            PersonDeviceDetailsDTO dto = personDeviceService.findPersonDeviceById(deviceId);
            return new ResponseEntity<>(dto, HttpStatus.OK);
        }

        @GetMapping(value = "/person/{personId}")
        public ResponseEntity<List<PersonDeviceDTO>> getPersonDevicesByPersonId(@PathVariable("personId") UUID personId) {
            List<PersonDeviceDTO> dtos = personDeviceService.findPersonDevicesByPersonId(personId);
            for (PersonDeviceDTO dto : dtos) {
                Link link = linkTo(methodOn(PersonDeviceController.class)
                        .getPersonDeviceById(dto.getDeviceId())).withRel("personDeviceDetails");
                dto.add(link);
            }
            return new ResponseEntity<>(dtos, HttpStatus.OK);
        }

        @PostMapping()
        public ResponseEntity<UUID> insertPersonDevice(@Valid @RequestBody PersonDeviceDetailsDTO personDeviceDetailsDTO) {
            UUID deviceId = personDeviceService.insertPersonDevice(personDeviceDetailsDTO);
            return new ResponseEntity<>(deviceId, HttpStatus.CREATED);
        }

        @PutMapping(value = "/{id}")
        public ResponseEntity<UUID> updatePersonDevice(@PathVariable("id") UUID deviceId,
                                                       @Valid @RequestBody PersonDeviceDetailsDTO personDeviceDetailsDTO) {
            UUID updatedDeviceId = personDeviceService.updatePersonDevice(deviceId, personDeviceDetailsDTO);
            return new ResponseEntity<>(updatedDeviceId, HttpStatus.OK);
        }

        @DeleteMapping(value = "/{id}")
        public ResponseEntity<Void> deletePersonDevice(@PathVariable("id") UUID deviceId) {
            personDeviceService.deletePersonDevice(deviceId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        @PostMapping("/map")
        public ResponseEntity<String> mapDeviceToUser(@RequestBody Map<String, UUID> ids) {
            UUID userId = ids.get("userId");
            UUID deviceId = ids.get("deviceId");
            System.out.println(userId);
            System.out.println(deviceId);
            try {
                personDeviceService.mapDeviceToUser(userId, deviceId);  // Call service method
                return ResponseEntity.ok("Device mapped to user successfully!");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error mapping device");
            }
        }

        @PostMapping("/deleteWherePerson/{personId}")
        public ResponseEntity<Void> deleteWherePerson(@PathVariable("personId") UUID personId) {
            personDeviceService.deleteByPersonId(personId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        @PostMapping("/deleteWhereDevice/{deviceId}")
        public ResponseEntity<Void> deleteWhereDevice(@PathVariable("deviceId") UUID deviceId) {
            personDeviceService.deleteByDeviceId(deviceId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

    }
