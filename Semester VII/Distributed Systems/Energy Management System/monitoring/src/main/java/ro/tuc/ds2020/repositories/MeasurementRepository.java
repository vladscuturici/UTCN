package ro.tuc.ds2020.repositories;

import ro.tuc.ds2020.entities.Measurement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public interface MeasurementRepository extends JpaRepository<Measurement, Long> {

    @Query("SELECT m FROM Measurement m WHERE m.deviceId = :deviceId AND DATE(m.timestamp) = :date")
    List<Measurement> getByDeviceAndDate(UUID deviceId, LocalDate date);

    List<Measurement> findByDeviceIdAndTimestampAfter(UUID deviceId, LocalDateTime oneHourAgo);
}