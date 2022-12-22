package nl.tudelft.sem.template;

import nl.tudelft.sem.template.shared.entities.Event;
import nl.tudelft.sem.template.shared.entities.User;
import nl.tudelft.sem.template.shared.enums.Outcome;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(path = "api/notification")
public class NotificationController {

  private Notification notification;
  public WebClient client;

  @Autowired
  public NotificationController(Notification notification){
    this.notification = notification;
  }

  @PostMapping(path = "/{eventId}/{netId}/")
  public ResponseEntity<String> sendNotification(@PathVariable ("eventId") Long id,
                                                 @PathVariable("netId") String netId,
                                                 @RequestParam("outcome") Outcome outcome) {
    this.client = WebClient.create();
    Mono<User> response = client.get().uri("http://localhost:8084/api/user/netId/?netId=" + netId)
            .retrieve().bodyToMono(User.class).log();
    if (Boolean.FALSE.equals(response.hasElement().block())) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    User user = response.block();

    Mono<Event> responseEvent = client.get().uri("http://localhost:8083/" + id).retrieve().bodyToMono(Event.class).log();
    if (Boolean.FALSE.equals(responseEvent.hasElement().block())) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    Event event = responseEvent.block();

    notification.setStrategy(new PlatformStrategy());
    String message = notification.sendNotification(user, event, outcome);

    notification.setStrategy(new EmailStrategy());
    message += "\n" + notification.sendNotification(user, event, outcome);
    return ResponseEntity.ok(message);
  }

}
