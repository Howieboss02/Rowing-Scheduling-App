package nl.tudelft.sem.template;

import nl.tudelft.sem.template.shared.entities.Event;
import nl.tudelft.sem.template.shared.entities.User;
import nl.tudelft.sem.template.shared.enums.Outcome;

public interface Strategy {

    /**
    * Method that will send notification by different ways.
    *
    * @param user the user that will receive the notification
    * @param event the event the notification is about
    * @param outcome the answer from the event: accept or reject
    * @return a formatted message
    */
    String sendNotification(User user, Event event, Outcome outcome);
}