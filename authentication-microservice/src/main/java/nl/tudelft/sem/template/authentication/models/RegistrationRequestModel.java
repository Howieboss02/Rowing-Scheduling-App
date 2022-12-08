package nl.tudelft.sem.template.authentication.models;

import lombok.Data;

/**
 * Model representing a registration request.
 */
@Data
public class RegistrationRequestModel {
    private String netId;
    private String password;
    private String name;
    private String organization;
    private String email;
    private String certificate;
    private String gender;
    this.positions = positions;
}