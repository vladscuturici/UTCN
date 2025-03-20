package ro.tuc.ds2020.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ro.tuc.ds2020.entities.PersonDevice;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PersonDeviceRepository extends JpaRepository<PersonDevice, UUID> {

    List<PersonDevice> findByPersonId(UUID personId);

    Optional<PersonDevice> findByDeviceId(UUID deviceId);

    boolean existsByDeviceId(UUID deviceId);

    @Transactional
    @Modifying
    @Query("DELETE FROM PersonDevice pd WHERE pd.personId = ?1")
    void deleteByPersonId(UUID personId);

    @Transactional
    @Modifying
    @Query("DELETE FROM PersonDevice pd WHERE pd.deviceId = ?1")
    void deleteByDeviceId(UUID deviceId);
}
