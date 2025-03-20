package ro.tuc.ds2020.entities;

import lombok.NoArgsConstructor;
import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.UUID;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "hourly_energy_consumption")
public class HourlyEnergyConsumption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hourlyConsumptionId", nullable = false)
    private Long hourlyConsumptionId;

    @Column(name = "deviceId", nullable = false)
    private UUID deviceId;

    @Column(name = "userId", nullable = false)
    private UUID userId;

    @Column(name = "maxHourlyConsumption", nullable = false)
    private Double maxHourlyConsumption;

    @Column(name = "currentConsumption")
    private Double currentConsumption;

    // Custom constructor (without hourlyConsumptionId)
    public HourlyEnergyConsumption(UUID deviceId, UUID userId, Double maxHourlyConsumption, Double currentConsumption) {
        this.deviceId = deviceId;
        this.userId = userId;
        this.maxHourlyConsumption = maxHourlyConsumption;
        this.currentConsumption = currentConsumption;
    }
}
