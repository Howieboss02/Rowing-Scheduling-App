import static nl.tudelft.sem.template.shared.enums.Outcome.ACCEPTED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import nl.tudelft.sem.template.Notification;
import nl.tudelft.sem.template.NotificationController;
import nl.tudelft.sem.template.shared.domain.Node;
import nl.tudelft.sem.template.shared.domain.TimeSlot;
import nl.tudelft.sem.template.shared.entities.Event;
import nl.tudelft.sem.template.shared.entities.User;
import nl.tudelft.sem.template.shared.enums.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class NotificationControllerTest {
    private final Long eventId = 1L;
    private final String netId = "testNetId";
    private final Outcome outcome = ACCEPTED;

    private static final String apiPrefix = "http://localhost:";
    private static final String userPath = "/api/user/";
    private static final String eventPath = "/api/event/";

    private Notification notification;

    RestTemplate restTemplate;
    NotificationController notificationController;

    @BeforeEach
    void setup() {
        restTemplate = mock(RestTemplate.class);
        notification = mock(Notification.class);
        notificationController = new NotificationController(notification, restTemplate);
    }

    @Test
    public void testSendNotification() {
        User user = new User("testNetId", "name", null, "email", null, null, null);
        user.getUserInfo().setNetId(netId);
        Event event = new Event(
                1L,
                "Event 1",
                Arrays.asList(PositionName.Startboard),
                new TimeSlot(1, Day.MONDAY, new Node(8, 14)),
                Certificate.B1,
                EventType.TRAINING,
                false,
                "M",
                "Organisation 1"
        );
        when(restTemplate.getForObject(apiPrefix + MicroservicePorts.USER.port + userPath
                + "netId/?netId=" + netId, User.class)).thenReturn(user);

        when(restTemplate.getForObject(apiPrefix + MicroservicePorts.EVENT.port + eventPath
                + eventId, Event.class)).thenReturn(event);

        when(notification.sendNotification(user, event, outcome)).thenReturn("Notification sent");

        ResponseEntity<String> response = notificationController.sendNotification(eventId, netId, outcome);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testSendNotification404Event() {
        User user = new User("testNetId", "name", null, "email", null, null, null);
        user.getUserInfo().setNetId(netId);
        Event event = new Event(
                1L,
                "Event 1",
                Arrays.asList(PositionName.Startboard),
                new TimeSlot(1, Day.MONDAY, new Node(8, 14)),
                Certificate.B1,
                EventType.TRAINING,
                false,
                "M",
                "Organisation 1"
        );
        when(restTemplate.getForObject(apiPrefix + MicroservicePorts.USER.port
                + "/netId/?netId=" + netId, User.class)).thenReturn(user);

        when(restTemplate.getForObject(apiPrefix + MicroservicePorts.EVENT.port
                + eventId, Event.class)).thenReturn(null);

        when(notification.sendNotification(user, event, outcome)).thenReturn("Notification sent");

        ResponseEntity<String> response = notificationController.sendNotification(eventId, netId, outcome);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testSendNotification404User() {
        User user = new User("testNetId", "name", null, "email", null, null, null);
        user.getUserInfo().setNetId(netId);
        Event event = new Event(
                1L,
                "Event 1",
                Arrays.asList(PositionName.Startboard),
                new TimeSlot(1, Day.MONDAY, new Node(8, 14)),
                Certificate.B1,
                EventType.TRAINING,
                false,
                "M",
                "Organisation 1"
        );
        when(restTemplate.getForObject(apiPrefix + MicroservicePorts.USER.port + userPath
                + "netId/?netId=" + netId, User.class)).thenReturn(null);

        when(restTemplate.getForObject(apiPrefix + MicroservicePorts.EVENT.port + eventPath
                + eventId, Event.class)).thenReturn(event);

        when(notification.sendNotification(user, event, outcome)).thenReturn("Notification sent");

        ResponseEntity<String> response = notificationController.sendNotification(eventId, netId, outcome);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}

