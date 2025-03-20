package ro.tuc.ds2020.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HourlyEnergyConsumptionDTO {
    private Long hourlyConsumptionId;
    private UUID deviceId;
    private UUID userId;
    private Double maxHourlyConsumption;
    private Double currentConsumption;
}
