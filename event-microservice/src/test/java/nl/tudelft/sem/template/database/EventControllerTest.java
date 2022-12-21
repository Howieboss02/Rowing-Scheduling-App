package nl.tudelft.sem.template.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.template.controllers.EventController;
import nl.tudelft.sem.template.services.EventService;
import nl.tudelft.sem.template.shared.domain.Position;
import nl.tudelft.sem.template.shared.domain.Request;
import nl.tudelft.sem.template.shared.entities.Event;
import nl.tudelft.sem.template.shared.entities.EventModel;
import nl.tudelft.sem.template.shared.entities.User;
import nl.tudelft.sem.template.shared.enums.Certificate;
import nl.tudelft.sem.template.shared.enums.EventType;
import nl.tudelft.sem.template.shared.enums.PositionName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;

public class EventControllerTest {

    public TestEventRepository repo;
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
     * @return a new event
     */
    private static Event getEvent(String s, Long l, Certificate c, EventType t) {
        return new Event(l, s, createPositions(), s, s, c, t, false, s);
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
    private static EventModel getEventModel(String s, Long l, Certificate c, EventType t) {
        return new EventModel(l, s, createPositions(), s, s, c, t, false, s);
    }

    /**
     * Method to set up dependencies for every test.
     */
    @BeforeEach
    public void setup() {
        service = new EventService(repo);
        sut = new EventController(service);

        mockedService = mock(EventService.class);
        mockedSut = new EventController(mockedService);
        mockedClient = mock(WebClient.class);
    }

    /**
     * Testing if adding an event also keeps the data correctly.
     */
    @Test
    public void addEventTest() {
        try {
            var actual = sut.registerNewEvent(getEventModel("A", 1L, Certificate.B2, EventType.COMPETITION));
            assertEquals(actual.getBody().getId(), 1);
            assertEquals(actual.getBody().getLabel(), "A");
            assertEquals(actual.getBody().getCertificate(), Certificate.B2);
            assertEquals(actual.getBody().getType(), EventType.TRAINING);
            assertEquals(actual.getBody().getQueue().size(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void addEventFailTest() {
        EventModel eventModel = getEventModel("A", 1L, Certificate.B2, EventType.COMPETITION);
        when(mockedService.insert(any(Event.class))).thenThrow(IllegalArgumentException.class);

        var actual = mockedSut.registerNewEvent(eventModel);
        assertEquals(HttpStatus.BAD_REQUEST, actual.getStatusCode());

    }


    /**
     * Testing if adding events has an impact on the database.
     */
    @Test
    public void getAllTest() {
        try {
            sut.registerNewEvent(getEventModel("B", 1L, Certificate.B5, EventType.COMPETITION));
            sut.registerNewEvent(getEventModel("A", 2L, Certificate.B2, EventType.TRAINING));
            assertEquals(sut.getEvents().size(), 2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Testing if deleting an event has an impact on the database.
     */
    @Test
    public void deleteTest() {
        try {
            sut.registerNewEvent(getEventModel("B", 1L, Certificate.B5, EventType.COMPETITION));
            sut.registerNewEvent(getEventModel("A", 2L, Certificate.B2, EventType.COMPETITION));
            assertEquals(sut.getEvents().size(), 2);

            Event ev = getEvent("B", 1L, Certificate.B5, EventType.COMPETITION);
            assertEquals(sut.deleteEvent(1L), ev);
            assertEquals(sut.getEvents().size(), 1);

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
            Event ev = getEvent("B", 1L, Certificate.B5, EventType.COMPETITION);
            sut.registerNewEvent(getEventModel("A", 2L, Certificate.B2, EventType.COMPETITION));
            sut.updateEvent(1L, getEventModel("B", 1L, Certificate.B5, EventType.COMPETITION));
            assertEquals(sut.getEvents().get(0), ev);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void updateTestFail() {
        try {
            Event ev = getEvent("B", 1L, Certificate.B5, EventType.COMPETITION);
            assertEquals(sut.updateEvent(1L,
                    getEventModel("B", 1L, Certificate.B5, EventType.COMPETITION)).getStatusCode(),
                    HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getEventsByUserTest() {
        try {
            Event event = getEvent("B", 2L, Certificate.B5, EventType.COMPETITION);
            sut.registerNewEvent(getEventModel("A", 2L, Certificate.B2, EventType.COMPETITION));
            sut.registerNewEvent(getEventModel("B", 1L, Certificate.B5, EventType.COMPETITION));
            assertEquals(sut.getEventsByUser(2L), List.of(event));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getRequestsTest() {
        Request r = new Request("A", PositionName.Cox);
        when(mockedService.getRequests(1L)).thenReturn(List.of(r));
        assertEquals(mockedSut.getRequests(1L), List.of(r));
    }

    @Test
    public void matchEventsTest() {
        User u = new User("A", "A", "A", "A", "A", Certificate.B5, List.of(new Position()));
        Event event = getEvent("B", 2L, Certificate.B5, EventType.COMPETITION);
        when(mockedService.getMatchedEvents(u)).thenReturn(List.of(event));
        assertEquals(List.of(event), mockedSut.matchEvents(u));

    }

    @Test
    public void matchEventsTestNoCertificate() {
        User u = new User("A", "A", "A", "A", "A", null, List.of(new Position()));
        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            mockedSut.matchEvents(u);
        });
        assertEquals("Profile is not (fully) completed", e.getMessage());
    }

    @Test
    public void deleteEventTest() {
        try {
            var response = mockedSut.deleteEvent(1L);
            verify(mockedService, times(1)).deleteById(1L);
            assertEquals(response.getStatusCode(), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
        }

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
    public void acceptTestNoEvent() {
        Request r = new Request("A", PositionName.Cox);
        when(mockedService.getById(1L)).thenReturn(Optional.empty());
        assertEquals(mockedSut.accept(1L, r).getStatusCode(), HttpStatus.NOT_FOUND);
    }

    @Test
    public void acceptTestNoRequest() {
        Event event = getEvent("B", 2L, Certificate.B5, EventType.COMPETITION);
        Request r = new Request("A", PositionName.Cox);
        when(mockedService.getById(1L)).thenReturn(Optional.of(event));
        when(mockedService.dequeueById(1L, r)).thenReturn(false);
        assertEquals(mockedSut.accept(1L, r).getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void acceptTestFilledPosition() {
        Event event = getEvent("B", 2L, Certificate.B5, EventType.COMPETITION);
        Request r = new Request("A", PositionName.Cox);
        when(mockedService.getById(1L)).thenReturn(Optional.of(event));
        when(mockedService.dequeueById(1L, r)).thenReturn(true);
        when(mockedService.removePositionById(1L, PositionName.Cox)).thenReturn(false);
        assertEquals(mockedSut.accept(1L, r).getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void acceptTest() {
        Event event = getEvent("B", 2L, Certificate.B5, EventType.COMPETITION);
        Request r = new Request("A", PositionName.Cox);
        when(mockedService.getById(1L)).thenReturn(Optional.of(event));
        when(mockedService.dequeueById(1L, r)).thenReturn(true);
        when(mockedService.removePositionById(1L, PositionName.Cox)).thenReturn(true);
        assertEquals(mockedSut.accept(1L, r).getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void rejectTestNoEvent() {
        Request r = new Request("A", PositionName.Cox);
        when(mockedService.getById(1L)).thenReturn(Optional.empty());
        assertEquals(mockedSut.reject(1L, r).getStatusCode(), HttpStatus.NOT_FOUND);
    }

    @Test
    public void rejectTestNoRequest() {
        Event event = getEvent("B", 2L, Certificate.B5, EventType.COMPETITION);
        Request r = new Request("A", PositionName.Cox);
        when(mockedService.getById(1L)).thenReturn(Optional.of(event));
        when(mockedService.dequeueById(1L, r)).thenReturn(false);
        assertEquals(mockedSut.reject(1L, r).getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void rejectTest() {
        Event event = getEvent("B", 2L, Certificate.B5, EventType.COMPETITION);
        Request r = new Request("A", PositionName.Cox);
        when(mockedService.getById(1L)).thenReturn(Optional.of(event));
        when(mockedService.dequeueById(1L, r)).thenReturn(true);
        assertEquals(mockedSut.reject(1L, r).getStatusCode(), HttpStatus.OK);
    }
}
