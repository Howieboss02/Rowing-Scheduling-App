package nl.tudelft.sem.template;

import lombok.Data;
import nl.tudelft.sem.template.shared.entities.Event;
import nl.tudelft.sem.template.shared.entities.User;
import nl.tudelft.sem.template.shared.enums.Outcome;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Data
@RestController
@RequestMapping(path = "api/notification")
public class NotificationController {
    private Notification notification;
    public WebClient client;

    @Autowired
    public NotificationController(Notification notification) {
        this.notification = notification;
    }

    public void setClient(WebClient client) {
        this.client = client;
    }

    /**
    * Sends a notification.
    *
    * @param id the id of the event with
    * @param netId the netId of the user to update
    * @param outcome the outcome to update by
    * @return the message of the notification that was sent
    */
    @PostMapping(path = "/{eventId}/{netId}/")
    public ResponseEntity<String> sendNotification(@PathVariable ("eventId") Long id,
                                                   @PathVariable("netId") String netId,
                                                   @RequestParam("outcome") Outcome outcome) {
        if (this.client == null) {
            this.client = WebClient.create();
        }
        //this.client = WebClient.create();
        Mono<User> response = client.get().uri("http://localhost:8084/api/user/netId/?netId=" + netId)
                .retrieve().bodyToMono(User.class).log();
        if (Boolean.FALSE.equals(response.hasElement().block())) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Mono<Event> responseEvent = client.get().uri("http://localhost:8083/api/event/" + id).retrieve().bodyToMono(Event.class).log();
        if (Boolean.FALSE.equals(responseEvent.hasElement().block())) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        User user = response.block();
        Event event = responseEvent.block();

        notification.setStrategy(new PlatformStrategy());
        String message = notification.sendNotification(user, event, outcome);

        notification.setStrategy(new EmailStrategy());
        message += "\n" + notification.sendNotification(user, event, outcome);
        return ResponseEntity.ok(message);
    }

}
