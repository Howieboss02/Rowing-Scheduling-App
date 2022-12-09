import nl.tudelft.sem.template.shared.enities.User;
import nl.tudelft.sem.template.shared.enums.Outcome;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "api/notification")
public class NotificationController {

  private Notification notification;
  //private EventService eventService;

  @Autowired
  public NotificationController(Notification notification){
    this.notification = notification;
  }

  //@PostMapping(path = "addNotification/{eventId}")
  //public ResponseEntity<String> sendNotification(@PathVariable ("eventId") Long id, @RequestBody User user, @RequestBody Outcome outcome){
    //Event event =
 // }

}
