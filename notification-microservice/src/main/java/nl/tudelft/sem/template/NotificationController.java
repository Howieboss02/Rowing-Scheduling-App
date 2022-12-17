package nl.tudelft.sem.template;

import nl.tudelft.sem.template.services.EventService;
import nl.tudelft.sem.template.services.UserService;
import nl.tudelft.sem.template.shared.entities.*;
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
   * @param message the message containing the user who will receive it and the outcome
   * @return either a "NOT_FOUND" if the ids are invalid or the final message of the notification
   */
  @PostMapping(path = "/{idEvent}")
  public ResponseEntity<String> sendNotification(@PathVariable("idEvent") Long idEvent, @RequestBody Message message){
    Optional<User> user = userService.getById(message.idUser);
    if(user.isEmpty()) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    Optional<Event> event = eventService.getById(idEvent);
    if(event.isEmpty()) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

    String messages = "";
    notification.setStrategy(new EmailStrategy());
    messages += notification.sendNotification(user.get(), event.get(), message.outcome) + "\n";
    notification.setStrategy((new PlatformStrategy()));
    messages += notification.sendNotification(user.get(), event.get(), message.outcome);
    return ResponseEntity.ok(messages);
  }

}
