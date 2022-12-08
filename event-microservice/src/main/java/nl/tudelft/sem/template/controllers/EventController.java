package nl.tudelft.sem.template.controllers;

import nl.tudelft.sem.template.services.EventService;
import nl.tudelft.sem.template.shared.domain.Position;
import nl.tudelft.sem.template.shared.enities.Event;
import nl.tudelft.sem.template.shared.enums.Certificate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/event")
public class EventController {
    private final EventService eventService;

    @Autowired
    public EventController(EventService eventService){
        this.eventService = eventService;
    }

    @GetMapping
    public List<Event> getEvents(){
        return eventService.getAllEvents();
    }

    @PostMapping
    public ResponseEntity<Event> registerNewEvent(@RequestBody Event event){
        Event receivedEvent = eventService.insert(event);
        if(receivedEvent == null){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(receivedEvent);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteEvent(@PathVariable("eventId") Long eventId){
        if(!eventService.deleteById(eventId)){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    @PutMapping(path = "{eventId}")
    public ResponseEntity<?> updateEvent( @PathVariable("eventId") Long eventId,
                                          @RequestParam(required = true) Long userId,
                                          @RequestParam(required = false) String label,
                                          @RequestParam(required = false) List<Position> positions,
                                          @RequestParam(required = false) String startTime,
                                          @RequestParam(required = false) String endTime,
                                          @RequestParam(required = false) Certificate certificate,
                                          @RequestParam(required = false) boolean isCompetitive,
                                          @RequestParam(required = false) String organisation){
        if(eventService.updateById(userId, eventId, label, positions, startTime, endTime, certificate, isCompetitive, organisation).isEmpty()){
            return ResponseEntity.badRequest().build();
        }else {
            return ResponseEntity.ok().build();
        }
    }
}
