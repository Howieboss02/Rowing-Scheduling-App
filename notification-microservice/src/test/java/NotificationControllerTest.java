import static nl.tudelft.sem.template.shared.enums.Outcome.ACCEPTED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import nl.tudelft.sem.template.NotificationController;
import nl.tudelft.sem.template.shared.entities.Event;
import nl.tudelft.sem.template.shared.entities.User;
import nl.tudelft.sem.template.shared.enums.Outcome;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
public class NotificationControllerTest {

    @InjectMocks
    private NotificationController notificationController;

    @Mock
    private WebClient client;

    private WebClient.RequestHeadersUriSpec requestHeadersUriSpecMock = Mockito.mock(WebClient.RequestHeadersUriSpec.class);
    final WebClient.RequestHeadersSpec headersSpecMock = Mockito.mock(WebClient.RequestHeadersSpec.class);
    final WebClient.ResponseSpec responseSpecMock = Mockito.mock(WebClient.ResponseSpec.class);

    @Test
    public void testSendNotification() {
        Long eventId = 123L;
        String netId = "abc123";
        Outcome outcome = ACCEPTED;
        User user = new User();
        user.setNetId(netId);
        Event event = new Event();
        event.setId(eventId);
        when(client.get()).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.uri(ArgumentMatchers.<String>notNull())).thenReturn(headersSpecMock);
        when(headersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(User.class)).thenReturn(Mono.just(user));
        when(responseSpecMock.bodyToMono(Event.class)).thenReturn(Mono.just(event));

        ResponseEntity<String> response = notificationController.sendNotification(eventId, netId, outcome);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}

