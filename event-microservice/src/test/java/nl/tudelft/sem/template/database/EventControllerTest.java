package nl.tudelft.sem.template.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.template.controllers.EventController;
import nl.tudelft.sem.template.services.EventService;
import nl.tudelft.sem.template.shared.domain.Node;
import nl.tudelft.sem.template.shared.domain.Position;
import nl.tudelft.sem.template.shared.domain.Request;
import nl.tudelft.sem.template.shared.domain.TimeSlot;
import nl.tudelft.sem.template.shared.entities.Event;
import nl.tudelft.sem.template.shared.entities.EventModel;
import nl.tudelft.sem.template.shared.entities.User;
import nl.tudelft.sem.template.shared.enums.Certificate;
import nl.tudelft.sem.template.shared.enums.Day;
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
            sut.registerNewEvent(getEventModel("B", 1L, Certificate.B5, EventType.COMPETITION,
                    new TimeSlot(1, Day.MONDAY, new Node(1, 2))));
            sut.registerNewEvent(getEventModel("A", 2L, Certificate.B2, EventType.TRAINING,
                    new TimeSlot(1, Day.MONDAY, new Node(1, 2))));
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
            sut.registerNewEvent(getEventModel("B", 1L, Certificate.B5, EventType.COMPETITION,
                    new TimeSlot(1, Day.MONDAY, new Node(1, 2))));
            sut.registerNewEvent(getEventModel("A", 2L, Certificate.B2, EventType.COMPETITION,
                    new TimeSlot(1, Day.MONDAY, new Node(1, 2))));
            assertEquals(sut.getEvents().size(), 2);

            Event ev = getEvent("B", 1L, Certificate.B5, EventType.COMPETITION,
                    new TimeSlot(1, Day.MONDAY, new Node(1, 2)));
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
            Event ev = getEvent("B", 1L, Certificate.B5, EventType.COMPETITION,
                    new TimeSlot(1, Day.MONDAY, new Node(1, 2)));
            sut.registerNewEvent(getEventModel("A", 2L, Certificate.B2, EventType.COMPETITION,
                    new TimeSlot(1, Day.MONDAY, new Node(1, 2))));
            sut.updateEvent(1L, getEventModel("B", 1L, Certificate.B5, EventType.COMPETITION,
                    new TimeSlot(1, Day.MONDAY, new Node(1, 2))), false);
            assertEquals(sut.getEvents().get(0), ev);
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
    public void getEventsByUserTest() {
        try {
            TimeSlot t = new TimeSlot(1, Day.MONDAY, new Node(1, 2));
            Event event = getEvent("B", 2L, Certificate.B5, EventType.COMPETITION, t);
            sut.registerNewEvent(getEventModel("A", 1L, Certificate.B2, EventType.COMPETITION, t));
            sut.registerNewEvent(getEventModel("B", 2L, Certificate.B5, EventType.COMPETITION, t));
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
        TimeSlot t = new TimeSlot(1, Day.MONDAY, new Node(1, 2));
        User u = new User("A", "A", "A", "A", "A", Certificate.B5, List.of(new Position()));
        Event event = getEvent("B", 2L, Certificate.B5, EventType.COMPETITION, t);
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
        TimeSlot t = new TimeSlot(1, Day.MONDAY, new Node(1, 2));
        Event event = getEvent("B", 2L, Certificate.B5, EventType.COMPETITION, t);
        Request r = new Request("A", PositionName.Cox);
        when(mockedService.getById(1L)).thenReturn(Optional.of(event));
        when(mockedService.dequeueById(1L, r)).thenReturn(false);
        assertEquals(mockedSut.accept(1L, r).getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void acceptTestFilledPosition() {
        TimeSlot t = new TimeSlot(1, Day.MONDAY, new Node(1, 2));
        Event event = getEvent("B", 2L, Certificate.B5, EventType.COMPETITION, t);
        Request r = new Request("A", PositionName.Cox);
        when(mockedService.getById(1L)).thenReturn(Optional.of(event));
        when(mockedService.dequeueById(1L, r)).thenReturn(true);
        when(mockedService.removePositionById(1L, PositionName.Cox)).thenReturn(false);
        assertEquals(mockedSut.accept(1L, r).getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void acceptTest() {
        TimeSlot t = new TimeSlot(1, Day.MONDAY, new Node(1, 2));
        Event event = getEvent("B", 2L, Certificate.B5, EventType.COMPETITION, t);
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
        TimeSlot t = new TimeSlot(1, Day.MONDAY, new Node(1, 2));
        Event event = getEvent("B", 2L, Certificate.B5, EventType.COMPETITION, t);
        Request r = new Request("A", PositionName.Cox);
        when(mockedService.getById(1L)).thenReturn(Optional.of(event));
        when(mockedService.dequeueById(1L, r)).thenReturn(false);
        assertEquals(mockedSut.reject(1L, r).getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void rejectTest() {
        TimeSlot t = new TimeSlot(1, Day.MONDAY, new Node(1, 2));
        Event event = getEvent("B", 2L, Certificate.B5, EventType.COMPETITION, t);
        Request r = new Request("A", PositionName.Cox);
        when(mockedService.getById(1L)).thenReturn(Optional.of(event));
        when(mockedService.dequeueById(1L, r)).thenReturn(true);
        assertEquals(mockedSut.reject(1L, r).getStatusCode(), HttpStatus.OK);
    }
}
