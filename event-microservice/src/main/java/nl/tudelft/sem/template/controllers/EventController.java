package nl.tudelft.sem.template.controllers;

import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.template.services.EventService;
import nl.tudelft.sem.template.shared.domain.Position;
import nl.tudelft.sem.template.shared.domain.Request;
import nl.tudelft.sem.template.shared.entities.Event;
import nl.tudelft.sem.template.shared.entities.EventModel;
import nl.tudelft.sem.template.shared.entities.User;
import nl.tudelft.sem.template.shared.enums.PositionName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@RestController
public class EventController {
    private final transient EventService eventService;
    private static WebClient client;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    /**
     * Get all events.
     *
     * @return List of events
     */
    @GetMapping("/all")
    public List<Event> getEvents() {
        return eventService.getAllEvents();
    }

    /**
     * Get all events belonging to a user.
     *
     * @param userId the id of the user we want to see the events of
     * @return List of events belonging to user
     */
    @GetMapping("/ownedBy/{userId}")
    public List<Event> getEventsByUser(@PathVariable("userId") Long userId) {
        return eventService.getAllEventsByUser(userId);
    }

    /**
     * Get a list of requests for a given event.
     *
     * @param eventId the id of the event
     * @return List of requests for that event
     */
    @GetMapping("/queue/{eventId}")
    public List<Request> getRequests(@PathVariable("eventId") Long eventId) {
        return eventService.getRequests(eventId);
    }

    /** matches suitable events with a user.
     *
     * @param user the user to match the events to
     * @return the events that match the user
     * @throws IllegalArgumentException if the user profile is not full
     */
    @GetMapping("/matchEvents")
    public List<Event> matchEvents(@RequestBody User user) throws IllegalArgumentException {
        if (user.getCertificate() == null || user.getPositions() == null || user.getPositions().size() == 0
                || user.getCertificate() == null || user.getOrganization() == null) {
            throw new IllegalArgumentException("Profile is not (fully) completed");
        }
        return eventService.getMatchedEvents(user);
    }

    /**
     * POST API to register an event to the platform.
     *
     * @param eventModel a dummy-like event object
     * @return the newly added object
     * @throws Exception if something goes wrong
     */
    @PostMapping("/register")
    public ResponseEntity<Event> registerNewEvent(@RequestBody EventModel eventModel) throws Exception {
        try {
            Event event = new Event(eventModel.getOwningUser(),
                    eventModel.getLabel(),
                    eventModel.getPositions(),
                    eventModel.getStartTime(),
                    eventModel.getEndTime(),
                    eventModel.getCertificate(),
                    eventModel.getType(),
                    eventModel.isCompetitive(),
                    eventModel.getOrganisation());
            Event receivedEvent = eventService.insert(event);
            return ResponseEntity.ok(receivedEvent);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * DELETE API to delete an event from the platform.
     *
     * @param eventId the event's id
     * @return an ok message if it goes right
     */
    @DeleteMapping("/delete/{eventId}")
    public ResponseEntity<?> deleteEvent(@PathVariable("eventId") Long eventId) {
        try {
            eventService.deleteById(eventId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
        return ResponseEntity.ok().build();
    }

    /**
     * PUT API to update an already existent event.
     *
     * @param eventId the event's id
     * @param eventModel a dummy-like event object
     * @return the newly updated event
     */
    @PutMapping("edit/{eventId}")
    public ResponseEntity<?> updateEvent(@PathVariable("eventId") Long eventId,
                                         @RequestBody EventModel eventModel) {
        Optional<Event> returned = eventService.updateById(eventModel.getOwningUser(), eventId, eventModel.getLabel(),
            eventModel.getPositions(), eventModel.getStartTime(), eventModel.getEndTime(),
            eventModel.getCertificate(), eventModel.getType(), eventModel.isCompetitive(), eventModel.getOrganisation());
        if (returned.isPresent()) {
            return ResponseEntity.ok(returned.get());
        } else {
            return ResponseEntity.badRequest().build();
        }
    }


    /**
     * PUT API for enqueueing a user to an event.
     *
     * @param eventId the id of the event
     * @param userId the id of the user
     * @return "NOT_FOUND" if the ids don't match something or "ENQUEUED" if task gets completed
     */
    @PutMapping("/enqueue/{eventId}/")
    public ResponseEntity<String> enqueue(@PathVariable("eventId") Long eventId,
                                          @RequestParam("userId") Long userId,
                                          @RequestParam PositionName position) {
        Optional<Event> event = eventService.getById(eventId);
        if (event.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        //Getting the User info from the database
        this.client = WebClient.create();
        Mono<User> response = client.get().uri("http://localhost:8084/api/user/" + userId)
            .retrieve().bodyToMono(User.class).log();
        if (!response.hasElement().block()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        User user = response.block();
        eventService.enqueueById(eventId, user, position);
        return ResponseEntity.ok("ENQUEUED");
    }


    /**
     * PUT API for accepting a user into the event
     *
     * @param eventId the id of the event
     * @param request the request should be accepted
     * @return "NOT_FOUND" if the event doesn't exist, badRequest if there is no matching request,
     * or "ACCEPTED" if task gets completed
     */
    @PutMapping("/accept/{eventId}")
    public ResponseEntity<String> accept(@PathVariable("eventId") Long eventId,
                                          @RequestBody Request request) {
        Optional<Event> event = eventService.getById(eventId);
        if (event.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        boolean processed = eventService.dequeueById(eventId, request);
        if (!processed) {
            return ResponseEntity.badRequest().build(); // request doesn't exist
        }
        // if the request exists...
        boolean positionFilled = eventService.removePositionById(eventId, request.getPosition());

        if(!positionFilled) {
            return ResponseEntity.badRequest().build(); // position couldn't be filled
        }

        //send notification


        return ResponseEntity.ok("ACCEPTED");

    }

    /**
     * PUT API for rejecting a user who wants to join an event
     *
     * @param eventId the id of the event
     * @param request the request should be rejected
     * @return "NOT_FOUND" if the event doesn't exist, badRequest if there is no matching request,
     * or "REJECTED" if task gets completed
     * */
    @PutMapping("/reject/{eventId}")
    public ResponseEntity<String> reject(@PathVariable("eventId") Long eventId,
                                         @RequestBody Request request) {
        Optional<Event> event = eventService.getById(eventId);
        if (event.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        boolean processed = eventService.dequeueById(eventId, request);
        if (!processed) {
            return ResponseEntity.badRequest().build(); // request doesn't exist
        }

        //send notification
        return ResponseEntity.ok("REJECTED");
    }
}
