package com.edgeapi.service.iam.infrastructure.persistence.jpa.repositories;

import com.edgeapi.service.iam.domain.model.aggregates.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * This interface is responsible for providing the User entity related operations.
 * It extends the JpaRepository interface.
 */
@Repository
public interface DeviceRepository extends JpaRepository<Device, Long>
{
    /**
     * This method is responsible for finding the user by username.
     * @param macAddress The username.
     * @return The user object.
     */
    Optional<Device> findByMacAddress(String macAddress);

    /**
     * This method is responsible for checking if the user exists by username.
     * @param macAddress The username.
     * @return True if the device exists, false otherwise.
     */
    boolean existsByMacAddress(String macAddress);

}
