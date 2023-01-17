import static nl.tudelft.sem.template.shared.enums.Outcome.ACCEPTED;
import static org.assertj.core.api.FactoryBasedNavigableListAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.Arrays;
import nl.tudelft.sem.template.Notification;
import nl.tudelft.sem.template.NotificationController;
import nl.tudelft.sem.template.shared.domain.Node;
import nl.tudelft.sem.template.shared.domain.TimeSlot;
import nl.tudelft.sem.template.shared.entities.Event;
import nl.tudelft.sem.template.shared.entities.User;
import nl.tudelft.sem.template.shared.enums.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@ExtendWith(MockitoExtension.class)
public class NotificationControllerTest {
    private final Long eventId = 1L;
    private final String netId = "testNetId";
    private final Outcome outcome = ACCEPTED;

    @Mock
    private Notification notification;

    @InjectMocks
    private NotificationController notificationController = new NotificationController(notification, new RestTemplate());

    @Mock
    private WebClient client;

    private WebClient.RequestHeadersUriSpec requestHeadersUriSpecMock = Mockito.mock(WebClient.RequestHeadersUriSpec.class);
    final WebClient.RequestHeadersSpec headersSpecMock = Mockito.mock(WebClient.RequestHeadersSpec.class);
    final WebClient.ResponseSpec responseSpecMock = Mockito.mock(WebClient.ResponseSpec.class);

    @Test
    public void testSendNotification() throws JsonProcessingException {
        User user = new User("testNetId", "name", "email");
        user.setNetId(netId);
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
        when(client.get()).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.uri(ArgumentMatchers.<String>notNull())).thenReturn(headersSpecMock);
        when(headersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(User.class)).thenReturn(Mono.just(user));
        when(responseSpecMock.bodyToMono(Event.class)).thenReturn(Mono.just(event));
        notificationController.setClient(client);
        when(notification.sendNotification(user, event, outcome)).thenReturn("Notification sent");

        ResponseEntity<String> response = notificationController.sendNotification(eventId, netId, outcome);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testSendNotification404Event() throws JsonProcessingException {
        User user = new User("testNetId", "name", "email");
        user.setNetId(netId);
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
        when(client.get()).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.uri(ArgumentMatchers.<String>notNull())).thenReturn(headersSpecMock);
        when(headersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(User.class)).thenReturn(Mono.just(user));
        when(responseSpecMock.bodyToMono(Event.class)).thenReturn(Mono.empty());
        notificationController.setClient(client);

        ResponseEntity<String> response = notificationController.sendNotification(eventId, netId, outcome);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testSendNotification404User() throws JsonProcessingException {
        User user = new User("testNetId", "name", "email");
        user.setNetId(netId);
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
        when(client.get()).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.uri(ArgumentMatchers.<String>notNull())).thenReturn(headersSpecMock);
        when(headersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(User.class)).thenReturn(Mono.empty());
        when(responseSpecMock.bodyToMono(Event.class)).thenReturn(Mono.just(event));
        notificationController.setClient(client);

        ResponseEntity<String> response = notificationController.sendNotification(eventId, netId, outcome);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testWithoutSettingUpWebClientThrowsException() {
        assertThrows(NullPointerException.class, () -> notificationController.sendNotification(eventId, netId, outcome));
    }

}

