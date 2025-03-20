package ro.tuc.ds2020.controllers;


import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import ro.tuc.ds2020.dtos.DeviceDTO;
import ro.tuc.ds2020.dtos.DeviceDetailsDTO;
import ro.tuc.ds2020.entities.Device;
import ro.tuc.ds2020.publisher.DevicePublisher;
import ro.tuc.ds2020.services.DeviceService;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping(value = "/devices")
public class DeviceController {

    private final DeviceService deviceService;
    private RestTemplate restTemplate;

    @Autowired
    private DevicePublisher devicePublisher;

    @Autowired
    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
        this.restTemplate = new RestTemplate();
    }

    @PostConstruct
    public void onStartup() {
        List<DeviceDTO> dtos = deviceService.findAllDevices();
        for (DeviceDTO dto : dtos) {
            devicePublisher.sendPostRequest(dto);
        }
    }

    @GetMapping()
    public ResponseEntity<List<DeviceDTO>> getDevices() {
        List<DeviceDTO> dtos = deviceService.findAllDevices();
        for (DeviceDTO dto : dtos) {
            Link deviceLink = linkTo(methodOn(DeviceController.class)
                    .getDeviceById(dto.getId())).withRel("deviceDetails");
            dto.add(deviceLink);
        }
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<DeviceDetailsDTO> getDeviceById(@PathVariable("id") UUID deviceId) {
        DeviceDetailsDTO dto = deviceService.findDeviceById(deviceId);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<UUID> insertDevice(@Valid @RequestBody DeviceDetailsDTO deviceDTO) {
        UUID deviceID = deviceService.insertDevice(deviceDTO);
        DeviceDTO dto = new DeviceDTO(deviceID, deviceDTO.getDescription(), deviceDTO.getAddress(), deviceDTO.getMaxHourlyEnergyConsumption());
        devicePublisher.sendPostRequest(dto);
        return new ResponseEntity<>(deviceID, HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<UUID> updateDevice(@PathVariable("id") UUID deviceId,
                                             @Valid @RequestBody DeviceDetailsDTO deviceDTO) {
        UUID updatedDeviceId = deviceService.updateDevice(deviceId, deviceDTO);
        DeviceDTO dto = new DeviceDTO(updatedDeviceId, deviceDTO.getDescription(), deviceDTO.getAddress(), deviceDTO.getMaxHourlyEnergyConsumption());
        devicePublisher.sendPutRequest(dto);
        return new ResponseEntity<>(updatedDeviceId, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteDevice(@PathVariable("id") UUID deviceId) {
        restTemplate.postForEntity(
                "http://user.localhost/person-device/deleteWhereDevice/" + deviceId,
                null,
                Void.class
        );

        deviceService.deleteDevice(deviceId);
        devicePublisher.sendDeleteRequest(deviceId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}