package nl.tudelft.sem.template.shared.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import nl.tudelft.sem.template.shared.entities.User;

import java.util.ArrayList;

/**
 * Model representing a registration request.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class RegistrationRequestModel extends User {
    private String password;

    public User getUser() {
        return new User( 0L, this.getNetId(), this.getEmail(), this.getName(), this.getOrganization(), this.getGender(), this.getCertificate(), this.getPositions());
    }
}