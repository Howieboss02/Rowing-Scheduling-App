package nl.tudelft.sem.template.services;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import nl.tudelft.sem.template.database.EventRepository;
import nl.tudelft.sem.template.shared.domain.Position;
import nl.tudelft.sem.template.shared.domain.Request;
import nl.tudelft.sem.template.shared.domain.TimeSlot;
import nl.tudelft.sem.template.shared.entities.Event;
import nl.tudelft.sem.template.shared.entities.EventModel;
import nl.tudelft.sem.template.shared.entities.User;
import nl.tudelft.sem.template.shared.enums.*;
import nl.tudelft.sem.template.shared.utils.PositionMatcher;
import nl.tudelft.sem.template.shared.utils.ValidityChecker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class EventService {
    private final transient EventRepository eventRepo;
    private static final String apiPrefix = "http://localhost:";
    private static final String userPath = "/api/user/";

    @Autowired
    private transient RestTemplate restTemplate;
    
    @Autowired
    public EventService(EventRepository eventRepo, RestTemplate restTemplate) {
        this.eventRepo = eventRepo;
        this.restTemplate = restTemplate;
    }

    public List<Event> getAllEvents() {
        return eventRepo.findAll();
    }

    public List<Event> getAllEventsByUser(Long userId) {
        return eventRepo.findByOwningUser(userId);
    }

    /**
     * Insert an event into the database.
     *
     * @param eventModel the type of event
     * @return List of events
     * @throws IllegalArgumentException exception when the event is not found
     */
    public Event insert(EventModel eventModel) throws IllegalArgumentException {
        Event event = eventModel.getEvent();
        return eventRepo.save(event);
    }

    /**
     * Delete an event from the database.
     *
     * @param eventId id of the event to delete
     * @throws Exception the exception when the event is not found
     */
    public void deleteById(Long eventId) throws Exception {
        if (!eventRepo.existsById(eventId)) {
            throw new Exception("ID does not exist");
        }
        eventRepo.deleteById(eventId);
    }

    /**
     * Get an event by id.
     *
     * @param id of the event to get
     * @return the event
     */
    public Optional<Event> getById(Long id) {
        if (!eventRepo.existsById(id)) {
            return Optional.empty();
        } else {
            return eventRepo.findById(id);
        }
    }

    /**
     * Update an event.
     *
     * @param eventId event id
     * @param eventModel the event model
     * @param updateIsCompetitive whether to update the isCompetitive field
     * @return the updated event
     */
    public Optional<Event> updateById(Long eventId, EventModel eventModel, boolean updateIsCompetitive) {
        Event event = null;
        Optional<Event> toUpdate = getById(eventId);
        if (toUpdate.isPresent()) {
            event = toUpdate.get().merge(eventModel, updateIsCompetitive);
            if (event == null) {
                return Optional.empty();
            }
            eventRepo.save(event);
        }
        return Optional.ofNullable(event);
    }

    /**
     * Gets the request queue for an event.
     *
     * @param id the id of the event
     * @return the queue of requests
     */
    public List<Request> getRequests(Long id) {
        Optional<Event> event = getById(id);
        if (event.isEmpty()) {
            return new ArrayList<>();
        }
        return event.get().getQueue();
    }

    /**
     * Adds a request to the queue of an event iff the user should be able to join the event.
     *
     * @param id the id of the event
     * @param userId the user who wants to enqueue
     * @param position the position the user wants to fill
     * @return whether the user was enqueued or not
     */
    public boolean enqueueById(Long id, Long userId, PositionName position, long time) {
        Optional<Event> event = getById(id);
        User user = getUserById(userId);

        if (event.isEmpty() || user.getPositions() == null) {
            return false;
        }
        Event actualEvent = event.get();

        ValidityChecker checker = new ValidityChecker(actualEvent, user);
        if (!checker.canJoin(position, time)) {
            return false;
        }

        boolean success = actualEvent.enqueue(user.getNetId(), position);
        eventRepo.save(actualEvent);
        return success;
    }

    /**
     * Removes a request from the queue of an event.
     *
     * @param id the id of the event
     * @param request the request to remove
     * @return true if the request was removed, otherwise false
     * @throws NoSuchElementException if the event does not exist or the request is not in the queue
     */
    public boolean dequeueById(Long id, Request request) throws NoSuchElementException {
        Optional<Event> event = getById(id);

        if (event.isEmpty()) {
            throw new NoSuchElementException("Event does not exist");
        } else {
            boolean success = event.get().dequeue(request);
            if (success) {
                eventRepo.save(event.get());
                return true;
            } else {
                throw new NoSuchElementException("Request does not exist");
            }
        }
    }

    public String sendNotification(Long id, String netId, String message) {
        return restTemplate.postForObject("http://localhost:8085/api/notification/" + id + "/" + netId + "/?outcome=" + message, null, String.class);
    }

    /**
     * Removes a position from the position list of an event.
     *
     * @param id the id of the event
     * @param position the name of the position to remove
     * @return true if the position was removed, otherwise false
     */
    public boolean removePositionById(Long id, PositionName position) {
        Optional<Event> event = getById(id);

        if (event.isEmpty()) {
            return false;
        } else {
            boolean success = event.get().removePosition(position);

            eventRepo.save(event.get());
            return success;
        }
    }


    /**
     * finds the events a user is suitable for.
     *
     * @param userId the user for which the returned events should match
     * @return events that match the user
     */
    public List<Event> getMatchedEvents(Long userId) throws IllegalArgumentException {

        User user = getUserById(userId);

        ValidityChecker checker = new ValidityChecker(user);
        PositionMatcher matcher = new PositionMatcher();

        if (!checker.canBeMatched()) {
            throw new IllegalArgumentException("User does not have enough information to be matched");
        }

        List<Event> e1 = eventRepo.findMatchingTrainings(user.getCertificate(), user.getId(), EventType.TRAINING);
        List<Event> e2 = eventRepo.findMatchingCompetitions(user.getCertificate(), user.getOrganization(),
                                                            user.getId(), EventType.COMPETITION, user.getGender());
        e2.forEach(e1::add);

        return PositionMatcher.matchPositions(user.getPositions(), e1, user);
    }

    public User getUserById(Long id) {
        return restTemplate.getForObject(apiPrefix + MicroservicePorts.USER.port + userPath + id, User.class);
    }
}
