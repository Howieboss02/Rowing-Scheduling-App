package nl.tudelft.sem.template;

import static nl.tudelft.sem.template.shared.enums.Outcome.*;

import lombok.Data;
import nl.tudelft.sem.template.shared.entities.Event;
import nl.tudelft.sem.template.shared.entities.User;
import nl.tudelft.sem.template.shared.enums.MicroservicePorts;
import nl.tudelft.sem.template.shared.enums.Outcome;
import org.springframework.web.client.RestTemplate;

@Data
public class PlatformStrategy implements Strategy {
    private static final String apiPrefix = "http://localhost:";
    private static final String userPath = "/api/user";

    private RestTemplate restTemplate;

    /**
     * Empty constructor for PlatformStrategy.
     */
    public PlatformStrategy() {
        restTemplate = new RestTemplate();
    }

    /**
     * Constructor.
     *
     * @param restTemplate the rest template to use
     */
    public PlatformStrategy(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

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
            message = user.getUserInfo().getName() + ", you have been accepted to " + event.messageConverter();
        } else {
            message = user.getUserInfo().getName() + ", you have been rejected from " + event.messageConverter();
        }
        restTemplate.put(apiPrefix + MicroservicePorts.USER.port + userPath + "/notification/"
                + user.getId() + "/?notification=" + message, null);

        return message;
    }
}