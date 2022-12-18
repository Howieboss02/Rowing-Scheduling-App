package nl.tudelft.sem.template.controllers;

import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.template.services.EventService;
import nl.tudelft.sem.template.shared.domain.Position;
import nl.tudelft.sem.template.shared.entities.Event;
import nl.tudelft.sem.template.shared.entities.EventModel;
import nl.tudelft.sem.template.shared.entities.User;
import nl.tudelft.sem.template.shared.enums.Certificate;
import nl.tudelft.sem.template.shared.enums.EventType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Transient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
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
    public List<Event> getEvents() {
        return eventService.getAllEvents();
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
     * Add and event to the database.
     *
     * @param eventModel the event to add
     * @return The event that was added
     * @throws Exception exception
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
                    eventModel.getOrganisation());

            Event receivedEvent = eventService.insert(event);
            return ResponseEntity.ok(receivedEvent);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Delete an event by id.
     *
     * @param eventId id of the event to delete
     * @return response entity
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
     * Edit an event by id.
     *
     * @param eventId id of the event to edit
     * @return response entity
     */
    @PutMapping("edit/{eventId}")
    public ResponseEntity<?> updateEvent(@PathVariable("eventId") Long eventId,
                                         @RequestBody EventModel eventModel) {
        Optional<Event> returned = eventService.updateById(eventModel.getOwningUser(), eventId, eventModel.getLabel(),
                eventModel.getPositions(), eventModel.getStartTime(), eventModel.getEndTime(),
                eventModel.getCertificate(), eventModel.getType(), eventModel.getOrganisation());
        if (!returned.isPresent()) {
            return ResponseEntity.ok(returned.get());
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

}
