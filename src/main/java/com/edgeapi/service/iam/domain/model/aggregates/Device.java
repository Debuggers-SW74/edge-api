package com.edgeapi.service.iam.domain.model.aggregates;

import com.edgeapi.service.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Device aggregate root
 * This class represents the aggregate root for the Device entity.
 *
 * @see AuditableAbstractAggregateRoot
 */
@Getter
@Setter
@Entity(name = "devices")
@AllArgsConstructor
@NoArgsConstructor
public class Device extends AuditableAbstractAggregateRoot<Device> {
    @NotBlank
    @Size(max = 50)
    @Column(unique = true)
    private String macAddress;

    @NotBlank
    @Size(max = 120)
    private String secret;

    private boolean isActive;

    public Device(String macAddress, String secret) {
        this.macAddress = macAddress;
        this.secret = secret;
        this.isActive = true;
    }

    public void deactivateDevice() {
        this.isActive = false;
    }

    public void activateDevice() {
        this.isActive = true;
    }
}
