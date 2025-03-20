package ro.tuc.ds2020.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ro.tuc.ds2020.entities.Device;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeviceRepository extends JpaRepository<Device, UUID> {

    /**
     * Finds devices by description, assuming descriptions may not be unique.
     * Returns a list of devices matching the specified description.
     */
    @Query(value = "SELECT d " +
            "FROM Device d " +
            "WHERE d.description = :description")
    List<Device> findByDescription(@Param("description") String description);

    /**
     * Finds a single Device by its ID, if exists.
     */
    Optional<Device> findById(UUID id);
}
