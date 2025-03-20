package ro.tuc.ds2020.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ro.tuc.ds2020.entities.HourlyEnergyConsumption;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface HourlyEnergyConsumptionRepository extends JpaRepository<HourlyEnergyConsumption, Long> {

    @Query("SELECT h FROM HourlyEnergyConsumption h WHERE h.deviceId = :deviceId AND h.currentConsumption IS NOT NULL AND h.maxHourlyConsumption IS NOT NULL")
    HourlyEnergyConsumption findByDeviceId(UUID deviceId);

    @Query("SELECT h FROM HourlyEnergyConsumption h WHERE h.deviceId = :deviceId AND h.currentConsumption >= h.maxHourlyConsumption")
    List<HourlyEnergyConsumption> findExceededConsumptions(UUID deviceId);
}
