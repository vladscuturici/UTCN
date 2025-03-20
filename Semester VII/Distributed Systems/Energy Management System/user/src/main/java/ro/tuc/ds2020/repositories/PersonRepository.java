package ro.tuc.ds2020.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ro.tuc.ds2020.entities.Person;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PersonRepository extends JpaRepository<Person, UUID> {

    /**
     * Finds a single Person by their name, intended for authentication purposes.
     * Assumes only one result or none will match.
     */
    @Query(value = "SELECT p " +
            "FROM Person p " +
            "WHERE p.name = :name")
    Optional<Person> findByName(@Param("name") String name);
}
