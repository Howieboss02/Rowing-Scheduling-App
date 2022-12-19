package nl.tudelft.sem.template.services;

import nl.tudelft.sem.template.shared.enums.MicroservicePorts;
import nl.tudelft.sem.template.shared.models.AuthenticationRequestModel;
import nl.tudelft.sem.template.shared.models.AuthenticationResponseModel;
import nl.tudelft.sem.template.shared.models.RegistrationRequestModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Transient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GatewayService {

    private static final String apiPrefix = "http://localhost:";
    private static final String userPath = "/api/user";
    @Autowired
    private static RestTemplate restTemplate;

    //TODO: add other services and try catch statements along with proper ResponseEntity return types

    // Auhtehtication microservice

    /**
     * Registers a user.
     *
     * @param request the registration request
     * @return the response entity
     */
    public ResponseEntity registerUser(RegistrationRequestModel request) {
        return restTemplate.postForObject(this.apiPrefix + MicroservicePorts.AUTHENTICATION.port
                + "/register", request, ResponseEntity.class);
    }

    /**
     * Logs in a user.
     *
     * @param request the login request
     * @return the response entity
     */
    public AuthenticationResponseModel login(AuthenticationRequestModel request) {
        return restTemplate.postForObject(this.apiPrefix + MicroservicePorts.AUTHENTICATION.port
                + "/authenticate", request, AuthenticationResponseModel.class);
    }

    // User microservice

    /**
     * Gets all users.
     */
    public ResponseEntity getAllUsers() {
        return restTemplate.getForEntity(this.apiPrefix + MicroservicePorts.USER.port + userPath
                + "/all", ResponseEntity.class);
    }

    /**
     * get all notifications for a user.
     */
    public ResponseEntity getAllNotifications(String userId) {
        return restTemplate.getForEntity(this.apiPrefix + MicroservicePorts.USER.port + userPath
                + "/getNotifications/{ userId }", ResponseEntity.class);
    }


    // TODO register user endpoint! Should it be done here or should it only be done by the authentication microservice?

    public ResponseEntity deleteUser(String userId) {
        return restTemplate.getForEntity(this.apiPrefix + MicroservicePorts.USER.port + userPath
                + "/delete/{ userId }", ResponseEntity.class);
    }

}
