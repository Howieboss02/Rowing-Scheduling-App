package nl.tudelft.sem.template.services;

import nl.tudelft.sem.template.components.RestTemplateResponseErrorHandler;
import nl.tudelft.sem.template.shared.domain.Position;
import nl.tudelft.sem.template.shared.entities.User;
import nl.tudelft.sem.template.shared.enums.MicroservicePorts;
import nl.tudelft.sem.template.shared.enums.Certificate;
import nl.tudelft.sem.template.shared.domain.TimeSlot;
import nl.tudelft.sem.template.shared.models.AuthenticationRequestModel;
import nl.tudelft.sem.template.shared.models.AuthenticationResponseModel;
import nl.tudelft.sem.template.shared.models.RegistrationRequestModel;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class GatewayService {

    private static final String apiPrefix = "http://localhost:";
    private static final String userPath = "/api/user";

    @Autowired
    private RestTemplate restTemplate;

    public GatewayService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    //TODO: add other services and try catch statements along with proper ResponseEntity return types

    // Auhtehtication microservice

    /**
     * Registers a user.
     *
     * @param request the registration request
     * @return the response entity
     */
    public User registerUser(RegistrationRequestModel request) {
        return restTemplate.postForObject(this.apiPrefix + MicroservicePorts.AUTHENTICATION.port
                + "/register", request, User.class);
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
        return restTemplate.getForObject(this.apiPrefix + MicroservicePorts.USER.port + userPath
                + "/all", ResponseEntity.class);
    }

    /**
     * get all notifications for a user.
     */
    public ResponseEntity getAllNotifications(String userId) {
        return restTemplate.getForObject(this.apiPrefix + MicroservicePorts.USER.port + userPath
                + "/getNotifications/{" + userId + "}", ResponseEntity.class);
    }

    /**
     * add a notification for a user.
     */
    public ResponseEntity addNotification(String userId, String notification) {
        return restTemplate.postForObject(this.apiPrefix + MicroservicePorts.USER.port + userPath
                + "/addNotification/{" + userId + "}", notification, ResponseEntity.class);
    }

    // TODO register user endpoint! Should it be done here or should it only be done by the authentication microservice?

//    public ResponseEntity deleteUser(String userId) {
//        return restTemplate.delete(this.apiPrefix + MicroservicePorts.USER.port + userPath
//                + "/delete/{" + userId + "}", ResponseEntity.class);
//    }

    /*public ResponseEntity updateUser(String userId, String name, String organization, String email, String gender, Certificate certificate, List<Position> positions) {
        return restTemplate.put(this.apiPrefix + MicroservicePorts.USER.port + userPath
                + "/update/{" + userId + "}", ,ResponseEntity.class);
    }*/

    public ResponseEntity setName(String userId, String name) {
        return restTemplate.postForObject(this.apiPrefix + MicroservicePorts.USER.port + userPath
                        + "/name/{" + userId + "}", name, ResponseEntity.class);
    }

    public ResponseEntity setOrganization(String userId, String organization) {
        return restTemplate.postForObject(this.apiPrefix + MicroservicePorts.USER.port + userPath
                        + "/organization/{" + userId + "}", organization, ResponseEntity.class);
    }

    public ResponseEntity setCertificate(String userId, Certificate certificate) {
        return restTemplate.postForObject(this.apiPrefix + MicroservicePorts.USER.port + userPath
                        + "/certificate/{" + userId + "}", certificate, ResponseEntity.class);
    }

}
