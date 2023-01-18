import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import nl.tudelft.sem.template.EmailStrategy;
import nl.tudelft.sem.template.Notification;
import nl.tudelft.sem.template.Strategy;
import nl.tudelft.sem.template.shared.domain.Node;
import nl.tudelft.sem.template.shared.domain.TimeSlot;
import nl.tudelft.sem.template.shared.entities.*;
import nl.tudelft.sem.template.shared.enums.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

public class EmailStrategyTest {
    public User user = new User("BobID", "Bob", null,  "Bob@b.ob", null, null, null);
    public Event event = new Event(1L, "Training 1", new ArrayList<>(),
            new TimeSlot(1, Day.MONDAY, new Node(12 * 60, 14 * 60)), Certificate.B1, EventType.TRAINING,
            true, "M", "Bob's camp");

    public RestTemplate mockRestTemplate;
    public Notification notification;
    public Strategy strategy;

    /**
     * Sets up the mocks.
     */
    @BeforeEach
    public void setUp() {
        mockRestTemplate = mock(RestTemplate.class);
        strategy = new EmailStrategy(mockRestTemplate);
        notification = new Notification();
        notification.setStrategy(strategy);
    }

    @Test
    public void testEmailAccepted() {
        String message = notification.sendNotification(user, event, Outcome.ACCEPTED);
        String correctMessage = "Bob, you have been accepted "
                + "to Training 1 - TRAINING from 12:00 until 14:00 in week 1, on MONDAY.\n";

        assertEquals("Email has been sent to Bob@b.ob with the message: \n" + correctMessage, message);
    }

    @Test
    public void testEmailRejected() {
        String message = notification.sendNotification(user, event, Outcome.REJECTED);
        String correctMessage = "Bob, you have been rejected "
                + "from Training 1 - TRAINING from 12:00 until 14:00 in week 1, on MONDAY.\n";

        assertEquals("Email has been sent to Bob@b.ob with the message: \n" + correctMessage, message);
    }
}