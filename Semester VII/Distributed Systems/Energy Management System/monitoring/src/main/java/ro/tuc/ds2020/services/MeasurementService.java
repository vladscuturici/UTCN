package ro.tuc.ds2020.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.parameters.P;
import ro.tuc.ds2020.dtos.MeasurementDTO;
import ro.tuc.ds2020.entities.Measurement;
import ro.tuc.ds2020.repositories.MeasurementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MeasurementService {
    @Autowired
    private MeasurementRepository measurementRepository;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public List<MeasurementDTO> getAllMeasurementsByDate(UUID deviceId, LocalDate date) {
        List<Measurement> measurements = measurementRepository.getByDeviceAndDate(deviceId, date);
        return measurements.stream()
                .map(measurement -> new MeasurementDTO(
                        measurement.getMeasurementId(),
                        measurement.getDeviceId(),
                        measurement.getValue(),
                        measurement.getTimestamp()
                ))
                .collect(Collectors.toList());
    }

    public void register(String message) {
        try {
            JsonNode jsonNode = objectMapper.readTree(message);

            String timestampStr = jsonNode.get("timestamp").asText();
            UUID deviceId = UUID.fromString(jsonNode.get("device_id").asText());
            double measurementValue = jsonNode.get("measurement_value").asDouble();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SS");
            LocalDateTime timestamp = LocalDateTime.parse(timestampStr, formatter);

            Measurement measurement = new Measurement();
            measurement.setDeviceId(deviceId);
            measurement.setValue(measurementValue);
            measurement.setTimestamp(timestamp);

            measurementRepository.save(measurement);

            System.out.println("Measurement registered successfully: " + measurement);

        } catch (Exception e) {
            System.err.println("Error processing measurement message: " + message);
            e.printStackTrace();
        }
    }

    public List<MeasurementDTO> getLastHourMeasurements(UUID deviceId) {
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        List<Measurement> measurements = measurementRepository.findByDeviceIdAndTimestampAfter(deviceId, oneHourAgo);

        return measurements.stream()
                .map(measurement -> new MeasurementDTO(
                        measurement.getMeasurementId(),
                        measurement.getDeviceId(),
                        measurement.getValue(),
                        measurement.getTimestamp()
                ))
                .collect(Collectors.toList());
    }

    public List<Double> getHourlyConsumptionsByDate(UUID deviceId, LocalDate date) {
        Double[] consumptions = new Double[24];
        Arrays.fill(consumptions, 0.0);

        List<Measurement> measurements = measurementRepository.getByDeviceAndDate(deviceId, date);

        for (Measurement measurement : measurements) {
            int hour = measurement.getTimestamp().getHour();
            consumptions[hour] += measurement.getValue();
        }

        return Arrays.asList(consumptions);
    }

}