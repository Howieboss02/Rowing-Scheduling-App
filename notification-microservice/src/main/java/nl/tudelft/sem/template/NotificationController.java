package nl.tudelft.sem.template;

import nl.tudelft.sem.template.services.EventService;
import nl.tudelft.sem.template.services.UserService;
import nl.tudelft.sem.template.shared.entities.*;
import nl.tudelft.sem.template.shared.enums.Outcome;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping(path = "api/notification")
public class NotificationController {

  private final Notification notification;
  private final EventService eventService;
  private final UserService userService;

  @Autowired
  public NotificationController(Notification notification, EventService eventService, UserService userService){
    this.notification = notification;
    this.eventService = eventService;
    this.userService = userService;
  }

  /**
   * API for sending a notification for an event to a specific user
   * @param idEvent the id of the event the user is getting notified
   * @param idUser the id of the user we want to send the message
   * @param outcome the outcome of enqueueing
   * @return either a "NOT_FOUND" if the ids are invalid or the final message of the notification
   */
  @PostMapping(path = "addNotification/{eventId}")
  public ResponseEntity<String> sendNotification(@PathVariable ("eventId") Long idEvent, @RequestBody long idUser, @RequestBody Outcome outcome){
    Optional<Event> event = eventService.getById(idEvent);
    if(event.isEmpty()) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    Optional<User> user = userService.getById(idUser);
    if(user.isEmpty()) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    return ResponseEntity.ok(notification.sendNotification(user.get(), event.get(), outcome));
  }

}
