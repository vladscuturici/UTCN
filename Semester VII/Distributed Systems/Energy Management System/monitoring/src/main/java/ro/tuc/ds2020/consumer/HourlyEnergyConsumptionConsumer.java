package ro.tuc.ds2020.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ro.tuc.ds2020.dtos.MeasurementDTO;
import ro.tuc.ds2020.entities.Device;
import ro.tuc.ds2020.entities.Measurement;
import ro.tuc.ds2020.repositories.DeviceRepository;
import ro.tuc.ds2020.repositories.MeasurementRepository;
import ro.tuc.ds2020.services.HourlyEnergyConsumptionService;
import ro.tuc.ds2020.services.MeasurementService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class HourlyEnergyConsumptionConsumer {

    @Autowired
    private SimpMessagingTemplate template;
    @Autowired
    private DeviceRepository deviceRepository;
    @Autowired
    private MeasurementService measurementService;
    @Autowired
    private MeasurementRepository measurementRepository;
    @Autowired
    private HourlyEnergyConsumptionService hourlyEnergyConsumptionService;

    @Scheduled(fixedRate = 5000)
    public void verifyConsumptions() {
        System.out.println("Verifying consumptions");
        List<Device> devices = deviceRepository.findAll();
        for (Device device : devices) {
            List<MeasurementDTO> measurements = measurementService.getLastHourMeasurements(device.getDeviceId());
            Double consumption = (double) 0;

            for(MeasurementDTO measurement : measurements) {
                consumption += measurement.getValue();
            }

            System.out.println("Device: " + device.getDeviceId() + ", consumption: " + consumption + ", date" + LocalDateTime.now());
            hourlyEnergyConsumptionService.register(device.getDeviceId(), device.getUserId(), device.getMaxHourlyConsumption(), consumption);
            if (consumption > device.getMaxHourlyConsumption()) {
                String notificationMessage = "Device " + device.getDeviceId() + " has exceeded the maximum hourly consumption limit!";
                template.convertAndSend("/topic/notifications/" + device.getUserId(), notificationMessage);
                System.out.println("Limit exceeded for user: " + device.getUserId());
            }
        }
    }
}
