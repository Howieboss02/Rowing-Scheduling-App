package nl.tudelft.sem.template;

import lombok.Data;
import lombok.NoArgsConstructor;
import nl.tudelft.sem.template.shared.entities.*;
import nl.tudelft.sem.template.shared.enums.Outcome;
import org.springframework.stereotype.Service;

@Data
@NoArgsConstructor
@Service
public class Notification {

    private Strategy strategy;

    /**
     * Method for setting the strategy the notification will use.
     *
     * @param strategy the type of strategy
     */
    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }

    /**
     * The method of sending the notification using the "nl.tudelft.sem.template.Strategy" design pattern.
     *
     * @param user the user that will receive it
     * @param event the event the notification is about
     * @param outcome accept/reject
     * @return a formatted message
     */
    public String sendNotification(User user, Event event, Outcome outcome) {
        return strategy.sendNotification(user, event, outcome);
    }
}