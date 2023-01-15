package nl.tudelft.sem.template.controllers;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.template.services.EventService;
import nl.tudelft.sem.template.shared.domain.Request;
import nl.tudelft.sem.template.shared.entities.Event;
import nl.tudelft.sem.template.shared.entities.EventModel;
import nl.tudelft.sem.template.shared.entities.User;
import nl.tudelft.sem.template.shared.enums.EventType;
import nl.tudelft.sem.template.shared.enums.PositionName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(path = "/api/event")
public class EventController {
    private final transient EventService eventService;
    private static WebClient client = WebClient.create();

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
     * @param id the id of the event
     * @return List of requests for that event
     */
    @GetMapping("/{id}/queue")
    public List<Request> getRequests(@PathVariable("id") Long id) {
        return eventService.getRequests(id);
    }

    /**
     * Get event by id.
     *
     * @param id the id of the event
     * @return a specific event
     */
    @GetMapping("/{id}")
    public ResponseEntity<Event> getById(@PathVariable("id") Long id) {
        Optional<Event> event = eventService.getById(id);
        if (event.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(event.get());
    }

    /** matches suitable events with a user.
     *
     * @param userId the user id to match the events to
     * @return the events that match the user
     * @throws IllegalArgumentException if the user profile is not full
     */
    @GetMapping("/match/{userId}")
    public ResponseEntity<List<Event>> matchEvents(@PathVariable("userId") Long userId) throws IllegalArgumentException {
        try {
            User user = eventService.getUserById(userId);

            if (user.getCertificate() == null || user.getPositions() == null || user.getPositions().size() == 0
                    || user.getOrganization() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
            (eventService.getMatchedEvents(user)).forEach(System.out::println);
            List<Event> response = eventService.getMatchedEvents(user);
            if (response == null || response.size() == 0) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }
            System.out.println(response);
            return ResponseEntity.ok(response);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatus()).build();
        }


    }

    /**
     * POST API to register an event to the platform.
     *
     * @param eventModel a dummy-like event object
     * @return the newly added object
     * @throws Exception if something goes wrong
     */
    @PostMapping("/register")
    public ResponseEntity<Event> registerNewEvent(@RequestBody EventModel eventModel) {
        try {
            Event event = new Event(eventModel.getOwningUser(),
                    eventModel.getLabel(),
                    eventModel.getPositions(),
                    eventModel.getTimeslot(),
                    eventModel.getCertificate(),
                    eventModel.getType(),
                    eventModel.isCompetitive(),
                    eventModel.getGender(),
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
     * @param id the event's id
     * @return an ok message if it goes right
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable("id") Long id) {
        System.out.println("id: " + id);
        try {
            eventService.deleteById(id);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
        return ResponseEntity.ok().build();
    }

    /**
     * PUT API to update an already existent event.
     *
     * @param id the event's id
     * @param eventModel a dummy-like event object
     * @return the newly updated event
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateEvent(@PathVariable("id") Long id,
                                         @RequestBody EventModel eventModel,
                                         @RequestParam(required = false) boolean updateIsCompetitive) {
        Optional<Event> returned = eventService.updateById(eventModel.getOwningUser(), id, eventModel.getLabel(),
            eventModel.getPositions(), eventModel.getTimeslot(),
            eventModel.getCertificate(), eventModel.getType(), eventModel.isCompetitive(),
                eventModel.getGender(), eventModel.getOrganisation(), updateIsCompetitive);
        if (returned.isPresent()) {
            return ResponseEntity.ok(returned.get());
        } else {
            return ResponseEntity.badRequest().build();
        }
    }


    /**
     * POST API for enqueueing a user to an event.
     *
     * @param eventId the id of the event
     * @param userId the id of the user
     * @return "NOT_FOUND" if the ids don't match something or "ENQUEUED" if task gets completed
     */
    @PostMapping("{eventId}/enqueue/{userId}")
    public ResponseEntity<String> enqueue(@PathVariable("eventId") Long eventId,
                                          @PathVariable("userId") Long userId,
                                          @RequestParam PositionName position) {
        Optional<Event> event = eventService.getById(eventId);
        if (event.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Getting the User info from the database
        // We recreate the client if it does not exist
        // This makes it easier to test
        if (client == null) {
            client = WebClient.create();
        }
        Mono<User> response = client.get().uri("http://localhost:8084/api/user/" + userId)
            .retrieve().bodyToMono(User.class).log();
        if (Boolean.FALSE.equals(response.hasElement().block())) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        User user = response.block();
        //Timestamp time = (Timestamp) response.timestamp().block().get(0);
        //long weekTime = time.getDay() * 1440L + 60 * time.getHours() + time.getMinutes();

        if (eventService.enqueueById(eventId, user, position, 0)) {
            return ResponseEntity.ok("ENQUEUED");
        }
        return ResponseEntity.ok("NOT ENQUEUED");
    }


    /**
     * POST API for accepting a user into the event.
     *
     * @param id the id of the event
     * @param request the request should be accepted
     * @return "NOT_FOUND" if the event doesn't exist, badRequest if there is no matching request, otherwise "ACCEPTED"
     */
    @PostMapping("/{id}/accept")
    public ResponseEntity<String> accept(@PathVariable("id") Long id,
                                          @RequestBody Request request) {
        Optional<Event> event = eventService.getById(id);
        if (event.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        boolean processed = eventService.dequeueById(id, request);
        if (!processed) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // request doesn't exist
        }
        // if the request exists...
        boolean positionFilled = eventService.removePositionById(id, request.getPosition());

        if (!positionFilled) {
            return ResponseEntity.badRequest().build(); // position couldn't be filled
        }

        //send notification
        try {
            String mess = client.post().uri("http://localhost:8085/api/notification/" + id + "/" + request.getName()
                + "/?outcome=ACCEPTED").retrieve().bodyToMono(String.class).block();
            return ResponseEntity.ok("ACCEPTED\n" + mess);
        } catch (Exception e) {
            return ResponseEntity.ok("ACCEPTED");
        }
    }

    /**
     * PUT API for rejecting a user who wants to join an event.
     *
     * @param id the id of the event
     * @param request the request should be rejected
     * @return "NOT_FOUND" if the event doesn't exist, badRequest if there is no matching request, otherwise "REJECTED"
     */
    @PostMapping("/{id}/reject")
    public ResponseEntity<String> reject(@PathVariable("id") Long id,
                                         @RequestBody Request request) {
        Optional<Event> event = eventService.getById(id);
        if (event.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        boolean processed = eventService.dequeueById(id, request);
        if (!processed) {
            return ResponseEntity.badRequest().build(); // request doesn't exist
        }
        //send notification
        try {
            String mess = client.post().uri("http://localhost:8085/api/notification/" + event.get().getId() + "/" + id
                + "/?outcome=REJECTED").retrieve().bodyToMono(String.class).block();
            return ResponseEntity.ok("REJECTED\n" + mess);
        } catch (Exception e) {
            return ResponseEntity.ok("REJECTED");
        }

    }
}
