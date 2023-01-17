package nl.tudelft.sem.template;

import lombok.Data;
import nl.tudelft.sem.template.shared.entities.Event;
import nl.tudelft.sem.template.shared.entities.User;
import nl.tudelft.sem.template.shared.enums.MicroservicePorts;
import nl.tudelft.sem.template.shared.enums.Outcome;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Data
@RestController
@RequestMapping(path = "api/notification")
public class NotificationController {
    private Notification notification;

    private static final String apiPrefix = "http://localhost:";
    private static final String userPath = "/api/user";
    private static final String eventPath = "/api/event";

    private RestTemplate restTemplate;

    @Autowired
    public NotificationController(Notification notification, RestTemplate restTemplate) {
        this.notification = notification;
        this.restTemplate = restTemplate;
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

        User user = restTemplate.getForObject(apiPrefix + MicroservicePorts.USER.port
                + "/netId/?netId=" + netId, User.class);

        Event event = restTemplate.getForObject(apiPrefix + MicroservicePorts.EVENT.port
                + id, Event.class);

        notification.setStrategy(new PlatformStrategy());
        String message = notification.sendNotification(user, event, outcome);

        notification.setStrategy(new EmailStrategy());
        message += "\n" + notification.sendNotification(user, event, outcome);
        return ResponseEntity.ok(message);
    }

}
