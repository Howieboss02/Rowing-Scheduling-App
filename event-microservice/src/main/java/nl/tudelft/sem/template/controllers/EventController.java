package nl.tudelft.sem.template.controllers;

import java.sql.Timestamp;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import nl.tudelft.sem.template.services.EventService;
import nl.tudelft.sem.template.shared.domain.Request;
import nl.tudelft.sem.template.shared.entities.Event;
import nl.tudelft.sem.template.shared.entities.EventModel;
import nl.tudelft.sem.template.shared.enums.PositionName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(path = "/api/event")
public class EventController {
    private final transient EventService eventService;

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
    public List<Event> getEvents(@RequestParam(required = false) Optional<Long> owner,
                                 @RequestParam(required = false) Optional<Long> match) {
        if (owner.isPresent()) {
            return eventService.getAllEventsByUser(owner.get());
        } else if (match.isPresent()) {
            try {
                return eventService.getMatchedEvents(match.get());
            } catch (IllegalArgumentException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
            }
        } else {
            return eventService.getAllEvents();
        }
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
        return event.map(ResponseEntity::ok).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
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
            Event receivedEvent = eventService.insert(eventModel);
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
        try {
            eventService.deleteById(id);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
        return ResponseEntity.ok("DELETED");
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
        Optional<Event> returned = eventService.updateById(id, eventModel, updateIsCompetitive);
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

        long weekTime = eventService.getCurrentWeekTime();

        if (eventService.enqueueById(eventId, userId, position, weekTime)) {
            return ResponseEntity.ok("ENQUEUED");
        }
        return ResponseEntity.ok("NOT ENQUEUED");
    }

    /**
     * PUT API for rejecting a user who wants to join an event.
     *
     * @param id the id of the event
     * @param request the request should be rejected
     * @return "NOT_FOUND" if the event doesn't exist, badRequest if there is no matching request, otherwise "REJECTED"
     */
    @PostMapping("/{id}/accept")
    public ResponseEntity<String> accept(@PathVariable("id") Long id,
                                         @RequestBody Request request,
                                         @RequestParam() boolean outcome) {
        boolean dequeueSuccess;
        try {
            dequeueSuccess = eventService.dequeueById(id, request);

            try {
                if (!outcome && dequeueSuccess) {
                    String mess = eventService.sendNotification(id, request.getName(), "REJECTED");
                    return ResponseEntity.ok("REJECTED\n" + mess);
                } else if (outcome && dequeueSuccess) {
                    String mess = eventService.sendNotification(id, request.getName(), "ACCEPTED");
                    return ResponseEntity.ok("ACCEPTED\n" + mess);
                } else {
                    return ResponseEntity.badRequest().build();
                }
            } catch (Exception e) {
                if (dequeueSuccess && !outcome) {
                    return ResponseEntity.ok("REJECTED, notification not sent");
                } else if (dequeueSuccess && outcome) {
                    return ResponseEntity.ok("ACCEPTED, notification not sent");
                } else {
                    return ResponseEntity.badRequest().build();
                }
            }
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }  catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
