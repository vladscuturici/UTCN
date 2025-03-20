package ro.tuc.ds2020.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ro.tuc.ds2020.dtos.MeasurementDTO;
import ro.tuc.ds2020.services.MeasurementService;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@CrossOrigin
@RestController
public class MonitoringController {
    @Autowired
    private MeasurementService measurementService;

    @GetMapping("/monitoring/{date}/{id}")
    ResponseEntity<List<Double>> getValuesForDateByDevice(@PathVariable("id") UUID deviceId, @PathVariable("date") LocalDate date) {
        List<Double> measurements = measurementService.getHourlyConsumptionsByDate(deviceId, date);
        return ResponseEntity.ok(measurements);
    }
}
