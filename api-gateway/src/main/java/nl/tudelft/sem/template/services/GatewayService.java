package nl.tudelft.sem.template.services;

import java.util.List;
import nl.tudelft.sem.template.shared.domain.Request;
import nl.tudelft.sem.template.shared.domain.TimeSlot;
import nl.tudelft.sem.template.shared.entities.Event;
import nl.tudelft.sem.template.shared.entities.EventModel;
import nl.tudelft.sem.template.shared.entities.User;
import nl.tudelft.sem.template.shared.entities.UserModel;
import nl.tudelft.sem.template.shared.enums.MicroservicePorts;
import nl.tudelft.sem.template.shared.enums.PositionName;
import nl.tudelft.sem.template.shared.models.AuthenticationRequestModel;
import nl.tudelft.sem.template.shared.models.AuthenticationResponseModel;
import nl.tudelft.sem.template.shared.models.RegistrationRequestModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class GatewayService {

    private static final String apiPrefix = "http://localhost:";
    private static final String userPath = "/api/user";
    private static final String eventPath = "/api/event";

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
        return restTemplate.postForObject(apiPrefix + MicroservicePorts.AUTHENTICATION.port
                + "/register", request, User.class);
    }

    /**
     * Logs in a user.
     *
     * @param request the login request
     * @return the response entity
     */
    public AuthenticationResponseModel login(AuthenticationRequestModel request) {
        return restTemplate.postForObject(apiPrefix + MicroservicePorts.AUTHENTICATION.port
                + "/authenticate", request, AuthenticationResponseModel.class);
    }

    // User microservice

    /**
     * Gets all users.
     */
    public List<User> getAllUsers() {
        return restTemplate.getForObject(apiPrefix + MicroservicePorts.USER.port + userPath
                + "/all", List.class);
    }

    /**
     * Get all notifications for a user.
     */
    public List<String> getAllNotifications(Long userId) {
        return restTemplate.getForObject(apiPrefix + MicroservicePorts.USER.port + userPath
                + "/getNotifications/" + userId, List.class);
    }

    /**
     * Delete a user.
     */
    public Boolean deleteUser(Long userId) {
        restTemplate.delete(apiPrefix + MicroservicePorts.USER.port + userPath
                + "/delete/" + userId);
        return true;
    }

    /**
     * Update a user.
     */
    public User updateUser(Long userId, UserModel userModel) {
        HttpEntity<UserModel> requestEntity = new HttpEntity<>(userModel);
        System.out.println("got here");
        return restTemplate.exchange(apiPrefix + MicroservicePorts.USER.port + userPath
                + "/update/" + userId, HttpMethod.PUT, requestEntity, User.class).getBody();
    }

    /**
     * Add a recurring time slot.
     */
    public TimeSlot addRecurring(Long userId, TimeSlot timeSlot) {
        return restTemplate.postForObject(apiPrefix + MicroservicePorts.USER.port + userPath
                + "/schedule/add/" + userId, timeSlot, TimeSlot.class);
    }

    /**
     * Remove a recurring time slot.
     */
    public TimeSlot removeRecurring(Long userId, TimeSlot timeSlot) {
        return restTemplate.postForObject(apiPrefix + MicroservicePorts.USER.port + userPath
                + "/schedule/remove/" + userId, timeSlot, TimeSlot.class);
    }

    /**
     * Add a one-time time slot.
     */
    public TimeSlot addOneTimeTimeSlot(Long userId, TimeSlot timeSlot) {
        return restTemplate.postForObject(apiPrefix + MicroservicePorts.USER.port + userPath
                + "/schedule/include/" + userId, timeSlot, TimeSlot.class);
    }

    /**
     * Remove a one-time time slot.
     */
    public TimeSlot removeOneTimeTimeSlot(Long userId, TimeSlot timeSlot) {
        return restTemplate.postForObject(apiPrefix + MicroservicePorts.USER.port + userPath
                + "/schedule/exclude/" + userId, timeSlot, TimeSlot.class);
    }

    public User getUser(Long userId) {
        return restTemplate.getForObject(apiPrefix + MicroservicePorts.USER.port + userPath
                + "/get/" + userId, User.class);
    }

    public List<Event> getAllEvents() {
        return restTemplate.getForObject(apiPrefix + MicroservicePorts.EVENT.port + eventPath
                + "/all", List.class);
    }

    public List<Event> getAllEventsForUser(Long userId) {
        return restTemplate.getForObject(apiPrefix + MicroservicePorts.EVENT.port + eventPath
                + "/ownedBy/" + userId, List.class);
    }

    public List<Request> getAllRequestsForEvent(Long eventId) {
        return restTemplate.getForObject(apiPrefix + MicroservicePorts.EVENT.port + eventPath
                + "/" + eventId + "/queue", List.class);
    }

    public ResponseEntity<List<Event>> getMatchedEventsForUser(Long userId) {
        return restTemplate.getForObject(apiPrefix + MicroservicePorts.EVENT.port + eventPath
                + "/match/" + userId, ResponseEntity.class);
    }

    public Event addNewEvent(EventModel eventModel) {
        return restTemplate.postForObject(apiPrefix + MicroservicePorts.EVENT.port + eventPath
                + "/register", eventModel, Event.class);
    }

    /**
     * Delete an event.
     */
    public ResponseEntity<Object> deleteEvent(Long eventId) {
        restTemplate.delete(apiPrefix + MicroservicePorts.EVENT.port + eventPath
                + "/" + eventId);
        return ResponseEntity.ok().build();
    }

    /**
     * Update an event.
     */
    public Event updateEvent(EventModel eventModel, Long id) {
        HttpEntity<EventModel> requestEntity = new HttpEntity<>(eventModel);
        return restTemplate.exchange(apiPrefix + MicroservicePorts.EVENT.port + eventPath
                + "/" + id, HttpMethod.PUT, requestEntity, Event.class).getBody();
    }

    public String enqueueToEvent(Long eventId, Long userId, PositionName position) {
        return restTemplate.postForObject(apiPrefix + MicroservicePorts.EVENT.port + eventPath
                + "/" + eventId + "/enqueue/" + userId + "?position=" + position, null, String.class);
    }

    /**
     * Accept to an event.
     */
    public String acceptToEvent(Long eventId, Request request) {
        return restTemplate.postForObject(apiPrefix + MicroservicePorts.EVENT.port + eventPath
                + "/" + eventId + "/accept", request, String.class);
    }

    public String rejectFromEvent(Long eventId, Request request) {
        return restTemplate.postForObject(apiPrefix + MicroservicePorts.EVENT.port + eventPath
                + "/" + eventId + "/reject", request, String.class);
    }


}
