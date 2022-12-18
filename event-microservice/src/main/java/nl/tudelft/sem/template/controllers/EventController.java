package nl.tudelft.sem.template.controllers;

import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.template.services.EventService;
import nl.tudelft.sem.template.shared.entities.Event;
import nl.tudelft.sem.template.shared.entities.EventModel;
import nl.tudelft.sem.template.shared.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@RestController
public class EventController {
    private final EventService eventService;
    private WebClient client;

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

    @DeleteMapping("/{eventId}")
    public ResponseEntity<?> deleteEvent(@PathVariable("eventId") Long eventId){
        try{
            eventService.deleteById(eventId);
        }catch(Exception e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
        return ResponseEntity.ok().build();
    }

    @PutMapping( "/{eventId}")
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

    /**
     * PUT API for enqueueing a user to an event
     * @param eventId the id of the event
     * @param userId the id of the user
     * @return "NOT_FOUND" if the ids don't match something or "ENQUEUED" if task gets completed
     */
    @PutMapping("/enqueue/{eventId}/")
    public ResponseEntity<String> enqueue(@PathVariable("eventId") Long eventId, @RequestParam("userId") Long userId) {
        Optional<Event> event = eventService.getById(eventId);
        if(event.isEmpty()) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        //Getting the User info from the database
        this.client = WebClient.create();
        Mono<User> response = client.get().uri("http://localhost:8084/api/user/" + userId).retrieve().bodyToMono(User.class).log();
        User user = response.block();
        event.get().enqueue(user);
        return ResponseEntity.ok("ENQUEUED");
    }
}
