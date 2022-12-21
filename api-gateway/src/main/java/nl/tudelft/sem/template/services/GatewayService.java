package nl.tudelft.sem.template.services;

import java.util.List;
import nl.tudelft.sem.template.shared.domain.TimeSlot;
import nl.tudelft.sem.template.shared.entities.User;
import nl.tudelft.sem.template.shared.enums.MicroservicePorts;
import nl.tudelft.sem.template.shared.models.AuthenticationRequestModel;
import nl.tudelft.sem.template.shared.models.AuthenticationResponseModel;
import nl.tudelft.sem.template.shared.models.RegistrationRequestModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class GatewayService {

    private static final String apiPrefix = "http://localhost:";
    private static final String userPath = "/api/user";

    @Autowired
    private transient RestTemplate restTemplate;

    public GatewayService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    //TODO: add other services and try catch statements along with proper ResponseEntity return types

    // Authentication microservice

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
    public List<User> getAllUsers() {
        return restTemplate.getForObject(this.apiPrefix + MicroservicePorts.USER.port + userPath
                + "/all", List.class);
    }

    /**
     * Get all notifications for a user.
     */
    public List<String> getAllNotifications(Long userId) {
        return restTemplate.getForObject(this.apiPrefix + MicroservicePorts.USER.port + userPath
                + "/getNotifications/{" + userId + "}", List.class);
    }

    /**
     * Delete a user.
     */
    public void deleteUser(Long userId) {
        restTemplate.delete(this.apiPrefix + MicroservicePorts.USER.port + userPath
                + "/delete/{" + userId + "}");
    }

    /**
     * Update a user.
     */
    public User updateUser(Long userId, User user) {
        return restTemplate.patchForObject(this.apiPrefix + MicroservicePorts.USER.port + userPath
                + "/update/{" + userId + "}", user, User.class);
    }

    /**
     * Add a recurring time slot.
     */
    public TimeSlot addRecurring(Long userId, TimeSlot timeSlot) {
        return restTemplate.postForObject(this.apiPrefix + MicroservicePorts.USER.port + userPath
                + "/schedule/add/{" + userId + "}", timeSlot, TimeSlot.class);
    }

    /**
     * Remove a recurring time slot.
     */
    public TimeSlot removeRecurring(Long userId, TimeSlot timeSlot) {
        return restTemplate.postForObject(this.apiPrefix + MicroservicePorts.USER.port + userPath
                + "/schedule/remove/{" + userId + "}", timeSlot, TimeSlot.class);
    }

    /**
     * Add a one-time time slot.
     */
    public TimeSlot addOneTimeTimeSlot(Long userId, TimeSlot timeSlot) {
        return restTemplate.postForObject(this.apiPrefix + MicroservicePorts.USER.port + userPath
                + "/schedule/include/{" + userId + "}", timeSlot, TimeSlot.class);
    }

    /**
     * Remove a one-time time slot.
     */
    public TimeSlot removeOneTimeTimeSlot(Long userId, TimeSlot timeSlot) {
        return restTemplate.postForObject(this.apiPrefix + MicroservicePorts.USER.port + userPath
                + "/schedule/exclude/{" + userId + "}", timeSlot, TimeSlot.class);
    }
}
