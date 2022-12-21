package nl.tudelft.sem.template.shared.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import nl.tudelft.sem.template.shared.domain.Position;
import nl.tudelft.sem.template.shared.entities.User;
import nl.tudelft.sem.template.shared.enums.Certificate;

import java.util.ArrayList;
import java.util.List;

/**
 * Model representing a registration request.
 */

@Data
public class RegistrationRequestModel {
    private String password;
    private String netId;
    private String organization;
    private String email;
    private String gender;
    private Certificate certificate;
    private List<Position> positions;
    private String name;

    public User getUser() {
        return new User(this.getNetId(), this.getEmail(), this.getName(), this.getOrganization(), this.getGender(),
                this.getCertificate(), this.getPositions());
    }
}