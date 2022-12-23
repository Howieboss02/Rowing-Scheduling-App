import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import nl.tudelft.sem.template.EmailStrategy;
import nl.tudelft.sem.template.Notification;
import nl.tudelft.sem.template.shared.domain.Node;
import nl.tudelft.sem.template.shared.domain.TimeSlot;
import nl.tudelft.sem.template.shared.entities.*;
import nl.tudelft.sem.template.shared.enums.*;
import org.junit.jupiter.api.Test;

public class EmailStrategyTest {
    public User user = new User("BobID", "Bob", "Bob@b.ob");
    public Event event = new Event(1L, "Training 1", new ArrayList<>(),
            new TimeSlot(1, Day.MONDAY, new Node(12 * 60, 14 * 60)), Certificate.B1, EventType.TRAINING,
            true, "M", "Bob's camp");

    @Test
    public void testEmailAccepted() {
        Notification n = new Notification();
        n.setStrategy(new EmailStrategy());
        String message = n.sendNotification(user, event, Outcome.ACCEPTED);
        assertEquals(0, user.getNotifications().size());
        assertEquals("Email has been sent to Bob@b.ob with the message: \n"
                + "Bob, you have been accepted to Training 1 - TRAINING from 12:00 until 14:00 in week 1, on MONDAY.\n",
                message);
    }

    @Test
    public void testEmailRejected() {
        Notification n = new Notification();
        n.setStrategy(new EmailStrategy());
        String message = n.sendNotification(user, event, Outcome.REJECTED);
        assertEquals(0, user.getNotifications().size());
        assertEquals("Email has been sent to Bob@b.ob with the message: \n"
                + "Bob, you have been rejected from Training 1 - TRAINING from 12:00 until 14:00 in week 1, on MONDAY.\n",
                message);
    }
}