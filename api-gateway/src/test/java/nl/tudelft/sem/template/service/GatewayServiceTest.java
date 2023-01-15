package nl.tudelft.sem.template.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.template.services.GatewayService;
import nl.tudelft.sem.template.shared.components.RestTemplateResponseErrorHandler;
import nl.tudelft.sem.template.shared.domain.Node;
import nl.tudelft.sem.template.shared.domain.Request;
import nl.tudelft.sem.template.shared.domain.TimeSlot;
import nl.tudelft.sem.template.shared.entities.Event;
import nl.tudelft.sem.template.shared.entities.EventModel;
import nl.tudelft.sem.template.shared.entities.User;
import nl.tudelft.sem.template.shared.enums.*;
import nl.tudelft.sem.template.shared.models.AuthenticationRequestModel;
import nl.tudelft.sem.template.shared.models.AuthenticationResponseModel;
import nl.tudelft.sem.template.shared.models.RegistrationRequestModel;
import nl.tudelft.sem.template.shared.utils.JsonUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

@RestClientTest
public class GatewayServiceTest {

    private RestTemplate restTemplate;

    private GatewayService service;

    @Autowired
    private RestTemplateBuilder builder;

    @Autowired
    private MockRestServiceServer server;

    private static final String apiPrefix = "http://localhost:";
    private static final String eventPath = "/api/event";

    private static final String userPath = "/api/user";
    private static final String registerPath = "/register";
    private static final String authenticatePath = "/authenticate";

    private static final String token = "token";

    private RegistrationRequestModel registerRequest;
    private AuthenticationRequestModel loginRequest;
    private User user;
    private AuthenticationResponseModel loginResponse;

    private final Event event1 = new Event(
            1L,
            "Event 1",
            Arrays.asList(PositionName.Startboard),
            new TimeSlot(),
            Certificate.B1,
            EventType.TRAINING,
            false,
            "M",
            "Organisation 1"
    );

    private final Event event2 = new Event(
            2L,
            "Event 2",
            Arrays.asList(PositionName.Coach, PositionName.Cox),
            new TimeSlot(),
            Certificate.B3,
            EventType.COMPETITION,
            true,
            "F",
            "Organisation 2"
    );

    private final EventModel eventModel = new EventModel(
            1L,
            "Test Event",
            Arrays.asList(PositionName.Coach, PositionName.Cox),
            new TimeSlot(),
            Certificate.B1,
            EventType.TRAINING,
            true,
            "M",
            "Test Organisation"
    );


    private final Request request = new Request("testRequest", PositionName.Coach);

    /**
     * Set up the test.
     */
    @BeforeEach
    public void setUp() {
        registerRequest = new RegistrationRequestModel();
        loginRequest = new AuthenticationRequestModel();
        user = new User();
        loginResponse = new AuthenticationResponseModel(token);

        this.restTemplate = this.builder
                .errorHandler(new RestTemplateResponseErrorHandler())
                .build();
        server = MockRestServiceServer.createServer(restTemplate);
        service = new GatewayService(this.restTemplate);
    }

    @Test
    public void testRegisterUser() throws JsonProcessingException {
        server.expect(requestTo(apiPrefix + MicroservicePorts.AUTHENTICATION.port + registerPath))
                .andRespond(withSuccess(JsonUtil.serialize(user), MediaType.APPLICATION_JSON));

        User result = service.registerUser(registerRequest);

        assertThat(user).isEqualTo(result);
        server.verify();
    }

    @Test
    public void testLogin() throws JsonProcessingException {
        server.expect(requestTo(apiPrefix + MicroservicePorts.AUTHENTICATION.port + authenticatePath))
                .andRespond(withSuccess(JsonUtil.serialize(loginResponse), MediaType.APPLICATION_JSON));

        AuthenticationResponseModel result = service.login(loginRequest);

        assertThat(loginResponse).isEqualTo(result);
        server.verify();
    }

    @Test
    public void testGetAllEvents() throws JsonProcessingException {
        List<Event> expected = Arrays.asList(event1, event2);
        server.expect(requestTo(apiPrefix + MicroservicePorts.EVENT.port + eventPath + "/all"))
                .andRespond(withSuccess(JsonUtil.serialize(expected), MediaType.APPLICATION_JSON));
        List<Event> actual = service.getAllEvents(Optional.empty(), Optional.empty());

        assertThat(actual.size()).isEqualTo(2);
        server.verify();
    }

    @Test
    public void testAddNewEvent() throws JsonProcessingException {
        server.expect(requestTo(apiPrefix + MicroservicePorts.EVENT.port + eventPath + "/register"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(JsonUtil.serialize(event1), MediaType.APPLICATION_JSON));

        Event actual = service.addNewEvent(eventModel);

        assertThat(actual).isEqualTo(event1);
        server.verify();
    }

    @Test
    public void testUpdateEvent() throws JsonProcessingException {
        server.expect(requestTo(apiPrefix + MicroservicePorts.EVENT.port + eventPath + "/1"))
                .andExpect(method(HttpMethod.PUT))
                .andRespond(withSuccess(JsonUtil.serialize(event1), MediaType.APPLICATION_JSON));

        Event actual = service.updateEvent(eventModel, 1L);

        assertThat(actual).isEqualTo(event1);
        server.verify();
    }

    @Test
    public void testDeleteEvent() throws JsonProcessingException {
        server.expect(requestTo(apiPrefix + MicroservicePorts.EVENT.port + eventPath + "/1"))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withSuccess());

        ResponseEntity<Object> actual = service.deleteEvent(1L);

        assertThat(actual.getStatusCodeValue()).isEqualTo(200);
        server.verify();
    }

    @Test
    public void testAddRequestToEvent() throws JsonProcessingException {
        server.expect(requestTo(apiPrefix + MicroservicePorts.EVENT.port + eventPath
                        + "/1/enqueue/2?position=Cox")).andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(JsonUtil.serialize("ENQUEUED"), MediaType.APPLICATION_JSON));

        String actual = service.enqueueToEvent(1L, 2L, PositionName.Cox);

        assertThat(actual).isEqualTo("\"ENQUEUED\"");
        server.verify();
    }

    @Test
    public void testAccept() throws JsonProcessingException {
        server.expect(requestTo(apiPrefix + MicroservicePorts.EVENT.port + eventPath + "/1/accept"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(JsonUtil.serialize("ACCEPTED"), MediaType.APPLICATION_JSON));

        String actual = service.acceptToEvent(1L, request, true);

        assertThat(actual).isEqualTo("\"ACCEPTED\"");
        server.verify();
    }

    @Test
    public void testGetAllNotifications() throws JsonProcessingException {
        List<String> notifications = List.of("user");
        server.expect(requestTo(apiPrefix + MicroservicePorts.USER.port + userPath + "/getNotifications/1"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(JsonUtil.serialize(notifications), MediaType.APPLICATION_JSON));

        assertEquals(notifications, service.getAllNotifications(1L));

        server.verify();
    }

    @Test
    public void testDelete() throws JsonProcessingException {
        server.expect(requestTo(apiPrefix + MicroservicePorts.USER.port + userPath + "/delete/1"))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withSuccess());

        assertTrue(service.deleteUser(1L));

        server.verify();
    }

    @Test
    public void testAddRecurring() throws JsonProcessingException {
        TimeSlot timeSlot = new TimeSlot(1, Day.FRIDAY, new Node(60, 120));
        server.expect(requestTo(apiPrefix + MicroservicePorts.USER.port + userPath + "/schedule/add/1"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(JsonUtil.serialize(timeSlot), MediaType.APPLICATION_JSON));

        assertEquals(timeSlot, service.addRecurring(1L, timeSlot));

        server.verify();
    }

    @Test
    public void testRemoveRecurring() throws JsonProcessingException {
        TimeSlot timeSlot = new TimeSlot(1, Day.FRIDAY, new Node(60, 120));
        server.expect(requestTo(apiPrefix + MicroservicePorts.USER.port + userPath + "/schedule/remove/1"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(JsonUtil.serialize(timeSlot), MediaType.APPLICATION_JSON));

        assertEquals(timeSlot, service.removeRecurring(1L, timeSlot));

        server.verify();
    }

    @Test
    public void testAddOneTime() throws JsonProcessingException {
        TimeSlot timeSlot = new TimeSlot(1, Day.FRIDAY, new Node(60, 120));
        server.expect(requestTo(apiPrefix + MicroservicePorts.USER.port + userPath + "/schedule/include/1"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(JsonUtil.serialize(timeSlot), MediaType.APPLICATION_JSON));

        assertEquals(timeSlot, service.addOneTimeTimeSlot(1L, timeSlot));

        server.verify();
    }

    @Test
    public void testRemoveOneTime() throws JsonProcessingException {
        TimeSlot timeSlot = new TimeSlot(1, Day.FRIDAY, new Node(60, 120));
        server.expect(requestTo(apiPrefix + MicroservicePorts.USER.port + userPath + "/schedule/exclude/1"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(JsonUtil.serialize(timeSlot), MediaType.APPLICATION_JSON));

        assertEquals(timeSlot, service.removeOneTimeTimeSlot(1L, timeSlot));

        server.verify();
    }

    @Test
    public void testGetUser() throws JsonProcessingException {
        server.expect(requestTo(apiPrefix + MicroservicePorts.USER.port + userPath + "/get/1"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(JsonUtil.serialize(user), MediaType.APPLICATION_JSON));

        assertEquals(user, service.getUser(1L));

        server.verify();
    }
}