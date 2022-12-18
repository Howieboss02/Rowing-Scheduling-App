package nl.tudelft.sem.template.controllers;

import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.template.services.EventService;
import nl.tudelft.sem.template.shared.entities.Event;
import nl.tudelft.sem.template.shared.entities.EventModel;
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
                    eventModel.isCompetitive(),
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
                                         @RequestBody EventModel eventModel,
                                         @RequestParam("editCompetition") boolean editCompetition) {
        Optional<Event> returned = eventService.updateById(eventModel.getOwningUser(), eventId, eventModel.getLabel(),
                eventModel.getPositions(), eventModel.getStartTime(), eventModel.getEndTime(),
                eventModel.getCertificate(), eventModel.isCompetitive(), eventModel.getType(),
                eventModel.getOrganisation(), editCompetition);
        if (returned.isEmpty()) {
            return ResponseEntity.badRequest().build();
        } else {
            return ResponseEntity.ok(returned.get());
        }
    }

}
