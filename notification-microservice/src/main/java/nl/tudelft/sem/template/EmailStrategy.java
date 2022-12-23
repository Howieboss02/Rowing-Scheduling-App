package nl.tudelft.sem.template;

import static nl.tudelft.sem.template.shared.enums.Outcome.*;

import lombok.NoArgsConstructor;
import nl.tudelft.sem.template.shared.entities.Event;
import nl.tudelft.sem.template.shared.entities.User;
import nl.tudelft.sem.template.shared.enums.Outcome;

@NoArgsConstructor
public class EmailStrategy implements Strategy {

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
            message = user.getName() + ", you have been accepted to " + event.messageConverter();
        } else if (outcome == REJECTED) {
            message = user.getName() + ", you have been rejected from " + event.messageConverter();
        } else {
            message = "";
        }
        return "Email has been sent to " + user.getEmail() + " with the message: \n" + message;
    }
}
