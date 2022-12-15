package nl.tudelft.sem.template.controllers;

import nl.tudelft.sem.template.services.EventService;
import nl.tudelft.sem.template.shared.entities.Event;
import nl.tudelft.sem.template.shared.entities.EventModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
public class EventController {
    private final EventService eventService;

    @Autowired
    public EventController(EventService eventService){
        this.eventService = eventService;
    }

    @GetMapping("/all")
    public List<Event> getEvents(){
        return eventService.getAllEvents();
    }

    @PostMapping("/register")
    public ResponseEntity<Event> registerNewEvent( @RequestBody EventModel eventModel ) throws Exception{
        Event receivedEvent;
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
            receivedEvent = eventService.insert(event);
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(receivedEvent);
    }

    @DeleteMapping("/delete/{eventId}")
    public ResponseEntity<?> deleteEvent(@PathVariable("eventId") Long eventId){
        try{
            eventService.deleteById(eventId);
        }catch(Exception e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
        return ResponseEntity.ok().build();
    }

    @PutMapping( "edit/{eventId}")
    public ResponseEntity<?> updateEvent( @PathVariable("eventId") Long eventId,
                                          @RequestBody EventModel eventModel,
                                          @RequestParam("editCompetition") boolean editCompetition){
        Optional<Event> returned = eventService.updateById(eventModel.getOwningUser(), eventId, eventModel.getLabel(), eventModel.getPositions(), eventModel.getStartTime(), eventModel.getEndTime(), eventModel.getCertificate(), eventModel.isCompetitive(), eventModel.getType(), eventModel.getOrganisation(), editCompetition);
        if(returned.isEmpty()){
            return ResponseEntity.badRequest().build();
        }else {
            return ResponseEntity.ok(returned.get());
        }
    }

}
