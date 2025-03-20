package ro.tuc.ds2020.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ro.tuc.ds2020.dtos.HourlyEnergyConsumptionDTO;
import ro.tuc.ds2020.entities.HourlyEnergyConsumption;
import ro.tuc.ds2020.entities.Measurement;
import ro.tuc.ds2020.repositories.HourlyEnergyConsumptionRepository;
import ro.tuc.ds2020.repositories.MeasurementRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class HourlyEnergyConsumptionService {

    @Autowired
    private HourlyEnergyConsumptionRepository hourlyEnergyConsumptionRepository;

    @Autowired
    private MeasurementRepository measurementRepository;

    public void register(UUID deviceId, UUID userId, double maxHourlyConsumption, double consumption) {
        HourlyEnergyConsumption x = new HourlyEnergyConsumption(deviceId, userId, maxHourlyConsumption, consumption);
        hourlyEnergyConsumptionRepository.save(x);
    }
}