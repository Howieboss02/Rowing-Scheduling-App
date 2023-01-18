import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import nl.tudelft.sem.template.Notification;
import nl.tudelft.sem.template.PlatformStrategy;
import nl.tudelft.sem.template.Strategy;
import nl.tudelft.sem.template.shared.domain.Node;
import nl.tudelft.sem.template.shared.domain.TimeSlot;
import nl.tudelft.sem.template.shared.entities.*;
import nl.tudelft.sem.template.shared.enums.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

public class PlatformStrategyTest {

    public User user = new User("BobID", "Bob", null, "Bob@b.ob", null, null, null);
    public Event event = new Event(1L, "Training 1", new ArrayList<>(),
            new TimeSlot(1, Day.MONDAY, new Node(12 * 60, 14 * 60 + 30)), Certificate.B1, EventType.TRAINING,
            true, "M",  "Bob's camp");

    public RestTemplate mockRestTemplate;
    public Notification notification;
    public Strategy strategy;

    /**
     * Sets up the mocks.
     */
    @BeforeEach
    public void setUp() {
        mockRestTemplate = mock(RestTemplate.class);
        strategy = new PlatformStrategy(mockRestTemplate);
        notification = new Notification();
        notification.setStrategy(strategy);
    }

    @Test
    public void testPlatformAccepted() {
        String message = notification.sendNotification(user, event, Outcome.ACCEPTED);
        String correctMessage = "Bob, you have been accepted "
                + "to Training 1 - TRAINING from 12:00 until 14:30 in week 1, on MONDAY.\n";

        verify(mockRestTemplate, times(1)).put(
                "http://localhost:8084/api/user/notification/null/?notification=" + correctMessage, null);

        assertEquals(correctMessage, message);
    }

    @Test
    public void testPlatformRejected() {
        String message = notification.sendNotification(user, event, Outcome.REJECTED);
        String correctMessage = "Bob, you have been rejected "
                + "from Training 1 - TRAINING from 12:00 until 14:30 in week 1, on MONDAY.\n";

        verify(mockRestTemplate, times(1)).put(
                "http://localhost:8084/api/user/notification/null/?notification=" + correctMessage, null);

        assertEquals(correctMessage, message);
    }
}