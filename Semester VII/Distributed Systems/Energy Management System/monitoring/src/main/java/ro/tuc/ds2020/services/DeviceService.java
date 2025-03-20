package ro.tuc.ds2020.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ro.tuc.ds2020.dtos.DeviceDTO;
import ro.tuc.ds2020.entities.Device;
import ro.tuc.ds2020.repositories.DeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DeviceService {

    @Autowired
    private DeviceRepository deviceRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private DeviceDTO parseDeviceMessage(String message) {
        String trimmedJson = message.replaceAll("[{}\"]", "");
        String[] keyValuePairs = trimmedJson.split(",");
        UUID deviceId = UUID.fromString(keyValuePairs[1].split(":")[1].trim());
        UUID userId = UUID.fromString(keyValuePairs[2].split(":")[1].trim());
        Double maxHourlyConsumption = Double.valueOf(keyValuePairs[3].split(":")[1].trim());
        DeviceDTO deviceDTO = new DeviceDTO();
        deviceDTO.setDeviceId(deviceId);
        deviceDTO.setUserId(userId);
        deviceDTO.setMaxHourlyConsumption(maxHourlyConsumption);
        return deviceDTO;
    }

    public void addMessage(String message) {
        DeviceDTO deviceDTO = parseDeviceMessage(message);
        Device device = new Device();
        device.setDeviceId(deviceDTO.getDeviceId());
        device.setUserId(deviceDTO.getUserId());
        device.setMaxHourlyConsumption(deviceDTO.getMaxHourlyConsumption());

        deviceRepository.save(device);
    }

    public void update(String message) {
        DeviceDTO deviceDTO = parseDeviceMessage(message);
        Optional<Device> optionalDevice = deviceRepository.findById(deviceDTO.getDeviceId());

        if (optionalDevice.isPresent()) {
            Device device = optionalDevice.get();

            device.setUserId(deviceDTO.getUserId());
            device.setMaxHourlyConsumption(deviceDTO.getMaxHourlyConsumption());

            deviceRepository.save(device);
        } else {
            throw new RuntimeException("Device not found: " + deviceDTO.getDeviceId());
        }
    }

    public void delete(String message) {
        UUID deviceId = parseDeviceId(message);

        if (deviceRepository.existsById(deviceId)) {
            deviceRepository.deleteById(deviceId);
        } else {
            throw new RuntimeException("Device not found: " + deviceId);
        }
    }

    private UUID parseDeviceId(String message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(message);
            return UUID.fromString(jsonNode.get("deviceId").asText());
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse deviceId from message: " + e.getMessage());
        }
    }
}