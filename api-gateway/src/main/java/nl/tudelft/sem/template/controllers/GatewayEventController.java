package nl.tudelft.sem.template.controllers;

import java.util.List;
import nl.tudelft.sem.template.services.GatewayService;
import nl.tudelft.sem.template.shared.domain.Request;
import nl.tudelft.sem.template.shared.entities.Event;
import nl.tudelft.sem.template.shared.entities.EventModel;
import nl.tudelft.sem.template.shared.enums.PositionName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@RestController
@RequestMapping(path = "/api/event")
public class GatewayEventController {

    @Autowired
    private transient GatewayService gatewayService;

    /**
     * Gets all events.
     */
    @GetMapping("/all")
    public ResponseEntity<List<Event>> getEvents() {
        try {
            return ResponseEntity.ok(gatewayService.getAllEvents());
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Gets all events.
     */
    @GetMapping("/ownedBy/{userId}")
    public ResponseEntity<List<Event>> getEventsByUser(@PathVariable("userId") Long userId) {
        try {
            return ResponseEntity.ok(gatewayService.getAllEventsForUser(userId));
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * get all request for a specific event.
     */
    @GetMapping("/{eventId}/queue")
    public ResponseEntity<List<Request>> getRequests(@PathVariable("eventId") Long eventId) {
        try {
            return ResponseEntity.ok(gatewayService.getAllRequestsForEvent(eventId));
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Match events to a user.
     */
    @GetMapping("/match/{userId}")
    public ResponseEntity<List<Event>> matchEvents(@PathVariable("userId") Long userId) {
        System.out.println("matchEvents");
        try {
            return gatewayService.getMatchedEventsForUser(userId);
        } catch (ResponseStatusException e) {
            System.out.println("matchEvents exception");
            throw e;
        } catch (Exception e) {
            System.out.println("matchEvents exception 2" + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Ragister a new event.
     */
    @PostMapping("/register")
    public ResponseEntity<Event> registerNewEvent(@RequestBody EventModel eventModel) throws Exception {
        try {
            return ResponseEntity.ok(gatewayService.addNewEvent(eventModel));
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Delete an event.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity deleteEvent(@PathVariable("id") Long id) {
        try {
            System.out.println("deleteEvent");
            return gatewayService.deleteEvent(id);
        } catch (ResponseStatusException e) {
            System.out.println("deleteEvent exception " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.out.println("deleteEvent exception " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Update an event.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateEvent(@PathVariable("id") Long id,
                                         @RequestBody EventModel eventModel) {
        try {
            return ResponseEntity.ok(gatewayService.updateEvent(eventModel, id));
        } catch (ResponseStatusException e) {
            System.out.println("updateEvent exception " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.out.println("updateEvent exception " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Enquque to an event.
     */
    @PostMapping("{eventId}/enqueue/{userId}")
    public ResponseEntity<String> enqueue(@PathVariable("eventId") Long eventId,
                                          @PathVariable("userId") Long userId,
                                          @RequestParam PositionName position) {
        try {
            return ResponseEntity.ok(gatewayService.enqueueToEvent(eventId, userId, position));
        } catch (ResponseStatusException e) {
            System.out.println("enqueue exception " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.out.println("enqueue exception " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Accept an event.
     */
    @PostMapping("{id}/accept")
    public ResponseEntity<String> accept(@PathVariable("id") Long id,
                                         @RequestBody Request request) {
        try {
            return ResponseEntity.ok(gatewayService.acceptToEvent(id, request));
        } catch (ResponseStatusException e) {
            System.out.println("accept exception " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.out.println("accept exception " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Reject an event.
     */
    @PostMapping("{id}/reject")
    public ResponseEntity<String> reject(@PathVariable("id") Long id,
                                         @RequestBody Request request) {
        try {
            return ResponseEntity.ok(gatewayService.rejectFromEvent(id, request));
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }


}
