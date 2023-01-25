package nl.tudelft.sem.template.database;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.nio.charset.StandardCharsets;
import java.util.*;
import nl.tudelft.sem.template.controllers.EventController;
import nl.tudelft.sem.template.services.EventService;
import nl.tudelft.sem.template.shared.authentication.JwtAuthenticationEntryPoint;
import nl.tudelft.sem.template.shared.authentication.JwtRequestFilter;
import nl.tudelft.sem.template.shared.domain.Node;
import nl.tudelft.sem.template.shared.domain.Request;
import nl.tudelft.sem.template.shared.domain.TimeSlot;
import nl.tudelft.sem.template.shared.entities.Event;
import nl.tudelft.sem.template.shared.entities.EventModel;
import nl.tudelft.sem.template.shared.enums.Certificate;
import nl.tudelft.sem.template.shared.enums.Day;
import nl.tudelft.sem.template.shared.enums.EventType;
import nl.tudelft.sem.template.shared.enums.PositionName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.reactive.function.client.WebClient;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EventControllerTest {

    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(), StandardCharsets.UTF_8);

    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;

    @MockBean
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    @MockBean
    private JwtRequestFilter jwtRequestFilter;

    @MockBean
    private EventService eventService;

    public TestEventRepository repo;

    private RestTemplate restTemplate;
    public EventService service;
    private EventController sut;

    public EventService mockedService;
    public EventController mockedSut;

    public WebClient mockedClient;

    /**
     * Method to create a list of positions for testing.
     *
     * @return the list for positions needed for an event
     */
    private static List<PositionName> createPositions() {
        List<PositionName> list = new ArrayList<>();
        list.add(PositionName.Cox);
        list.add(PositionName.PortSideRower);
        list.add(PositionName.PortSideRower);
        return list;
    }

    /**
     * Method to create an event for testing.
     *
     * @param s a string to be used
     * @param l a long to be used
     * @param c a certificate needed
     * @param t the type of event
     * @param ts a timeslot
     * @return a new event
     */
    private static Event getEvent(String s, Long l, Certificate c, EventType t, TimeSlot ts) {
        return new Event(l, s, createPositions(), ts, c, t, false, s, s);
    }

    /**
     * Method to create an event model for testing.
     *
     * @param s a string needed
     * @param l a long needed
     * @param c a certificate
     * @param t an event type
     * @return a new event model
     */
    private static EventModel getEventModel(String s, Long l, Certificate c, EventType t, TimeSlot ts) {
        return new EventModel(l, s, createPositions(), ts, c, t, false, s, s);
    }

    /**
     * Method to set up dependencies for every test.
     */
    @BeforeEach
    public void setup() {
        repo = new TestEventRepository();
        service = new EventService(repo, restTemplate);
        sut = new EventController(service);

        mockedService = mock(EventService.class);
        mockedSut = new EventController(mockedService);
        mockedClient = mock(WebClient.class);
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    /**
     * Testing if adding an event also keeps the data correctly.
     */
    @Test
    public void addEventTest() {
        try {
            var actual = sut.registerNewEvent(getEventModel("A", 1L, Certificate.B2, EventType.COMPETITION,
                    new TimeSlot(1, Day.MONDAY, new Node(1, 2))));
            assertEquals(actual.getBody().getLabel(), "A");
            assertEquals(actual.getBody().getCertificate(), Certificate.B2);
            assertEquals(actual.getBody().getType(), EventType.COMPETITION);
            assertEquals(actual.getBody().getQueue().size(), 0);
            assertEquals(actual.getBody().getTimeslot(), new TimeSlot(1, Day.MONDAY, new Node(1, 2)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void addEventFailTest() {
        TimeSlot t = new TimeSlot(1, Day.MONDAY, new Node(1, 2));
        EventModel eventModel = getEventModel("A", 1L, Certificate.B2, EventType.COMPETITION, t);
        when(mockedService.insert(any(EventModel.class))).thenThrow(IllegalArgumentException.class);

        var actual = mockedSut.registerNewEvent(eventModel);
        assertEquals(HttpStatus.BAD_REQUEST, actual.getStatusCode());

    }


    /**
     * Testing if adding events has an impact on the database.
     */
    @Test
    public void getAllTest() {
        try {
            sut.registerNewEvent(getEventModel("B", 1L, Certificate.B5, EventType.COMPETITION,
                    new TimeSlot(1, Day.MONDAY, new Node(1, 2))));
            sut.registerNewEvent(getEventModel("A", 2L, Certificate.B2, EventType.TRAINING,
                    new TimeSlot(1, Day.MONDAY, new Node(1, 2))));
            assertEquals(sut.getEvents(Optional.empty(), Optional.empty()).size(), 2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getAllTestSendRequest() throws Exception {
        List<Event> expectedEvents = Arrays.asList(getEvent("B", 1L, Certificate.B5, EventType.COMPETITION,
               new TimeSlot(1, Day.MONDAY, new Node(1, 2))),
               getEvent("A", 2L, Certificate.B2, EventType.TRAINING,
                       new TimeSlot(1, Day.MONDAY, new Node(1, 2))));
        given(eventService.getAllEvents()).willReturn(expectedEvents);
        mockMvc.perform(get("/api/event/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(eventService, times(1)).getAllEvents();
    }

    @Test
    public void getByIdExistsRequest() throws Exception {
        Event expectedEvent = getEvent("Event 1", 2L, Certificate.B2, EventType.TRAINING,
                new TimeSlot(1, Day.MONDAY, new Node(1, 2)));
        given(eventService.getById(1L)).willReturn(Optional.of(expectedEvent));

        mockMvc.perform(get("/api/event/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Event 1")));

        verify(eventService, times(1)).getById(1L);
    }

    @Test
    public void getByIdNoEventRequest() throws Exception {
        given(eventService.getById(1L)).willReturn(Optional.empty());

        mockMvc.perform(get("/api/event/1"))
                .andExpect(status().isNotFound());

        verify(eventService, times(1)).getById(1L);
    }


    @Test
    public void getAllEventsByUserRequest() throws Exception {
        List<Event> expectedEvents = Arrays.asList(getEvent("B", 1L, Certificate.B5, EventType.COMPETITION,
                        new TimeSlot(1, Day.MONDAY, new Node(1, 2))),
                getEvent("A", 1L, Certificate.B2, EventType.TRAINING,
                        new TimeSlot(1, Day.MONDAY, new Node(1, 2))));
        given(eventService.getAllEventsByUser(1L)).willReturn(expectedEvents);

        mockMvc.perform(get("/api/event/all?owner=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].owningUser").value(1))
                .andExpect(jsonPath("$[1].owningUser").value(1));

        verify(eventService, times(1)).getAllEventsByUser(1L);
    }

    @Test
    public void getMatchedEventsRequest() throws Exception {
        List<Event> expectedEvents = Arrays.asList(getEvent("B", 1L, Certificate.B5, EventType.COMPETITION,
                        new TimeSlot(1, Day.MONDAY, new Node(1, 2))),
                getEvent("A", 1L, Certificate.B2, EventType.TRAINING,
                        new TimeSlot(1, Day.MONDAY, new Node(1, 2))));
        given(eventService.getMatchedEvents(1L)).willReturn(expectedEvents);

        mockMvc.perform(get("/api/event/all?match=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(eventService, times(1)).getMatchedEvents(1L);
    }

    @Test
    public void getMatchedEventsExceptionRequest() throws Exception {
        given(eventService.getMatchedEvents(1L)).willThrow(
                new IllegalArgumentException("User does not have enough information to be matched"));

        mockMvc.perform(get("/api/event/all?match=1"))
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertEquals("400 BAD_REQUEST \"User does not have enough information to be matched\"",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));

        verify(eventService, times(1)).getMatchedEvents(1L);
    }

    @Test
    public void registerNewEventRequest() throws Exception {
        EventModel eventModel = getEventModel("A", 1L, Certificate.B2, EventType.COMPETITION,
                new TimeSlot(1, Day.MONDAY, new Node(1, 2)));
        Event expectedEvent = getEvent("A", 1L, Certificate.B2, EventType.COMPETITION,
                new TimeSlot(1, Day.MONDAY, new Node(1, 2)));
        given(eventService.insert(eventModel)).willReturn(expectedEvent);

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(eventModel);

        mockMvc.perform(post("/api/event/register")
                        .contentType((APPLICATION_JSON_UTF8))
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedEvent.getId()));

        verify(eventService, times(1)).insert(eventModel);
    }

    @Test
    public void deleteEventRequest() throws Exception {
        mockMvc.perform(delete("/api/event/1"))
                .andExpect(status().isOk()).andExpect(content().string("DELETED"));

        verify(eventService, times(1)).deleteById(1L);
    }

    @Test
    public void updateEventRequest() throws Exception {
        EventModel eventModel = getEventModel("A", 1L, Certificate.B2, EventType.COMPETITION,
                new TimeSlot(1, Day.MONDAY, new Node(1, 2)));
        Event expectedEvent = getEvent("A", 1L, Certificate.B2, EventType.COMPETITION,
                new TimeSlot(1, Day.MONDAY, new Node(1, 2)));
        given(eventService.updateById(1L, eventModel, false)).willReturn(Optional.of(expectedEvent));

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(eventModel);

        mockMvc.perform(put("/api/event/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedEvent.getId()));

        verify(eventService, times(1)).updateById(1L, eventModel, false);
    }

    @Test
    public void enqueueRequest() throws Exception {

        Event event = getEvent("A", 1L, Certificate.B2, EventType.COMPETITION,
                new TimeSlot(1, Day.MONDAY, new Node(1, 2)));
        given(eventService.getById(1L)).willReturn(Optional.of(event));
        given(eventService.getCurrentWeekTime()).willReturn(1L);
        given(eventService.enqueueById(1L, 2L, PositionName.Cox, 1L)).willReturn(true);

        mockMvc.perform(post("/api/event/1/enqueue/2")
                        .param("position", "Cox"))
                .andExpect(status().isOk())
                .andExpect(content().string("ENQUEUED"));

        verify(eventService, times(1)).getById(1L);
        verify(eventService, times(1)).getCurrentWeekTime();
        verify(eventService, times(1)).enqueueById(1L, 2L, PositionName.Cox, 1L);
    }

    @Test
    public void enqueueRequestFailed() throws Exception {

        Event event = getEvent("A", 1L, Certificate.B2, EventType.COMPETITION,
                new TimeSlot(1, Day.MONDAY, new Node(1, 2)));
        given(eventService.getById(1L)).willReturn(Optional.of(event));
        given(eventService.getCurrentWeekTime()).willReturn(1L);
        given(eventService.enqueueById(1L, 2L, PositionName.Cox, 1L)).willReturn(false);

        mockMvc.perform(post("/api/event/1/enqueue/2")
                        .param("position", "Cox"))
                .andExpect(status().isOk())
                .andExpect(content().string("NOT ENQUEUED"));

        verify(eventService, times(1)).getById(1L);
        verify(eventService, times(1)).getCurrentWeekTime();
        verify(eventService, times(1)).enqueueById(1L, 2L, PositionName.Cox, 1L);
    }



    @Test
    public void enqueueRequestEmpty() throws Exception {
        given(eventService.getById(1L)).willReturn(Optional.empty());

        mockMvc.perform(post("/api/event/1/enqueue/2")
                        .param("position", "Cox"))
                .andExpect(status().isNotFound());

        verify(eventService, times(1)).getById(1L);
    }

    @Test
    public void acceptEventRequest() throws Exception {
        Request request = new Request();
        request.setName("Test");
        when(eventService.dequeueById(1L, request)).thenReturn(true);
        when(eventService.sendNotification(1L, request.getName(), "ACCEPTED")).thenReturn("Notification sent successfully");
        mockMvc.perform(post("/api/event/1/accept")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request))
                        .param("outcome", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string("ACCEPTED\nNotification sent successfully"));
    }

    @Test
    public void acceptEventFailedRequest() throws Exception {
        Request request = new Request();
        request.setName("Test");
        when(eventService.dequeueById(1L, request)).thenReturn(false);
        mockMvc.perform(post("/api/event/1/accept")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request))
                        .param("outcome", "true"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void acceptEventEmptyRequest() throws Exception {
        Request request = new Request();
        request.setName("Test");
        when(eventService.dequeueById(1L, request)).thenThrow(new NoSuchElementException());
        mockMvc.perform(post("/api/event/1/accept")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request))
                        .param("outcome", "true"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void acceptEventExceptionAcceptedRequest() throws Exception {
        Request request = new Request();
        request.setName("Test");
        when(eventService.dequeueById(1L, request)).thenReturn(true);
        when(eventService.sendNotification(1L, request.getName(), "ACCEPTED")).thenThrow(new RestClientException("error"));
        mockMvc.perform(post("/api/event/1/accept")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request))
                        .param("outcome", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string("ACCEPTED, notification not sent"));
    }

    @Test
    public void acceptEventRejectedRequest() throws Exception {
        Request request = new Request();
        request.setName("Test");
        when(eventService.dequeueById(1L, request)).thenReturn(true);
        when(eventService.sendNotification(1L, request.getName(), "REJECTED")).thenThrow(new RestClientException("error"));
        mockMvc.perform(post("/api/event/1/accept")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request))
                        .param("outcome", "false"))
                .andExpect(status().isOk())
                .andExpect(content().string("REJECTED, notification not sent"));
    }

    @Test
    public void acceptEventBadRequest() throws Exception {
        Request request = new Request();
        request.setName("Test");
        when(eventService.dequeueById(1L, request)).thenReturn(false);
        mockMvc.perform(post("/api/event/1/accept")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request))
                        .param("outcome", "false"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void acceptEventBadRequest1() throws Exception {
        Request request = new Request();
        request.setName("Test");
        when(eventService.dequeueById(1L, request)).thenThrow(new IllegalArgumentException());
        mockMvc.perform(post("/api/event/1/accept")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request))
                        .param("outcome", "false"))
                .andExpect(status().isBadRequest());
    }

    /**
     * Testing if deleting an event has an impact on the database.
     */
    @Test
    public void deleteTest() {
        try {
            sut.registerNewEvent(getEventModel("B", 1L, Certificate.B5, EventType.COMPETITION,
                    new TimeSlot(1, Day.MONDAY, new Node(1, 2))));
            sut.registerNewEvent(getEventModel("A", 2L, Certificate.B2, EventType.COMPETITION,
                    new TimeSlot(1, Day.MONDAY, new Node(1, 2))));
            assertEquals(sut.getEvents(Optional.empty(), Optional.empty()).size(), 2);

            Event ev = getEvent("B", 1L, Certificate.B5, EventType.COMPETITION,
                    new TimeSlot(1, Day.MONDAY, new Node(1, 2)));
            assertEquals(sut.deleteEvent(1L), ev);
            assertEquals(sut.getEvents(Optional.empty(), Optional.empty()).size(), 1);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Testing if updating an event does change it also in the database.
     */
    @Test
    public void updateTest() {
        try {
            Event ev = getEvent("B", 1L, Certificate.B5, EventType.COMPETITION,
                    new TimeSlot(1, Day.MONDAY, new Node(1, 2)));
            sut.registerNewEvent(getEventModel("A", 2L, Certificate.B2, EventType.COMPETITION,
                    new TimeSlot(1, Day.MONDAY, new Node(1, 2))));
            sut.updateEvent(1L, getEventModel("B", 1L, Certificate.B5, EventType.COMPETITION,
                    new TimeSlot(1, Day.MONDAY, new Node(1, 2))), false);
            assertEquals(sut.getEvents(Optional.empty(), Optional.empty()).get(0), ev);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void updateTestFail() {
        assertEquals(sut.updateEvent(1L,
                getEventModel("B", 1L, Certificate.B5, EventType.COMPETITION,
                        new TimeSlot(1, Day.MONDAY, new Node(1, 2))), false).getStatusCode(),
                HttpStatus.BAD_REQUEST);
    }

    @Test
    public void deleteEventTestFail() {
        try {
            doThrow(new Exception()).when(mockedService).deleteById(1L);
            assertEquals(mockedSut.deleteEvent(1L).getStatusCode(), HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void rejectTestNoEvent() {
        Request r = new Request("A", PositionName.Cox);
        when(mockedService.getById(1L)).thenReturn(Optional.empty());
        when(mockedService.dequeueById(1L, r)).thenThrow(new NoSuchElementException());
        assertEquals(HttpStatus.NOT_FOUND, mockedSut.accept(1L, r, false).getStatusCode());
    }

    @Test
    public void rejectTestNoRequest() {
        TimeSlot t = new TimeSlot(1, Day.MONDAY, new Node(1, 2));
        Event event = getEvent("B", 2L, Certificate.B5, EventType.COMPETITION, t);
        Request r = new Request("A", PositionName.Cox);
        when(mockedService.getById(1L)).thenReturn(Optional.of(event));
        when(mockedService.dequeueById(1L, r)).thenReturn(false);
        assertEquals(mockedSut.accept(1L, r, false).getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void rejectTest() {
        TimeSlot t = new TimeSlot(1, Day.MONDAY, new Node(1, 2));
        Event event = getEvent("B", 2L, Certificate.B5, EventType.COMPETITION, t);
        Request r = new Request("A", PositionName.Cox);
        when(mockedService.getById(1L)).thenReturn(Optional.of(event));
        when(mockedService.dequeueById(1L, r)).thenReturn(true);
        assertEquals(mockedSut.accept(1L, r, false).getStatusCode(), HttpStatus.OK);
    }
}
