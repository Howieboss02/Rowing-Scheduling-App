package nl.tudelft.sem.template;

import static nl.tudelft.sem.template.shared.enums.Outcome.*;

import lombok.NoArgsConstructor;
import nl.tudelft.sem.template.shared.entities.Event;
import nl.tudelft.sem.template.shared.entities.User;
import nl.tudelft.sem.template.shared.enums.Outcome;
import org.springframework.web.client.RestTemplate;

@NoArgsConstructor
public class PlatformStrategy implements Strategy {

    /**
    * Sends a notification using the platform strategy.
    *
    * @param user the user that will receive the notification
    * @param event the event the notification is about
    * @param outcome the answer from the event: accept or reject
    * @return the message that was sent as a notification
    */
    public String sendNotification(User user, Event event, Outcome outcome) {
        String message;
        if (outcome == ACCEPTED) {
            message = user.getName() + ", you have been accepted to " + event.messageConverter();
        } else if (outcome == REJECTED) {
            message = user.getName() + ", you have been rejected from " + event.messageConverter();
        } else {
            message = "";
        }

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.put("http://localhost:8084/api/user/notification/"
                + user.getId() + "/?notification=" + message, null);

        return message;
    }
}