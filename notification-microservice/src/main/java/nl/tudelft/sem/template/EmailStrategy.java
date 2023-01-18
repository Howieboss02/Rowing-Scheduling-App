package nl.tudelft.sem.template;

import static nl.tudelft.sem.template.shared.enums.Outcome.*;

import lombok.Data;
import lombok.NoArgsConstructor;
import nl.tudelft.sem.template.shared.entities.Event;
import nl.tudelft.sem.template.shared.entities.User;
import nl.tudelft.sem.template.shared.enums.Outcome;
import org.springframework.web.client.RestTemplate;

@Data
public class EmailStrategy implements Strategy {

    private RestTemplate restTemplate;

    /**
     * Empty constructor for PlatformStrategy.
     */
    public EmailStrategy() {
        restTemplate = new RestTemplate();
    }

    /**
     * Constructor.
     *
     * @param restTemplate the rest template to use
     */
    public EmailStrategy(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Sends a notification using the email strategy.
     *
     * @param user the user that will receive the notification
     * @param event the event the notification is about
     * @param outcome the answer from the event: accept or reject
     * @return the message of the notification that was sent
     */
    public String sendNotification(User user, Event event, Outcome outcome) {
        String message;
        if (outcome == ACCEPTED) {
            message = user.getUserInfo().getName() + ", you have been accepted to " + event.messageConverter();
        } else {
            message = user.getUserInfo().getName() + ", you have been rejected from " + event.messageConverter();
        }
        return "Email has been sent to " + user.getUserInfo().getEmail() + " with the message: \n" + message;
    }
}
