package nl.tudelft.sem.template.database;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import nl.tudelft.sem.template.controllers.EventController;
import nl.tudelft.sem.template.services.EventService;
import nl.tudelft.sem.template.shared.domain.Position;
import nl.tudelft.sem.template.shared.entities.Event;
import nl.tudelft.sem.template.shared.entities.EventModel;
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
    public WebClient client;
    private EventController sut;

    /**
     * Method to create a list of positions for testing.
     *
     * @return the list fo positions needed for an event
     */
    private static List<Position> createPositions() {
        List<Position> list = new ArrayList<>();
        list.add(new Position(PositionName.Cox, false));
        list.add(new Position(PositionName.PortSideRower, false));
        list.add(new Position(PositionName.PortSideRower, true));
        return list;
    }

    /**
     * Method to create an event for testing.
     *
     * @param s a string to be used
     * @param l a long to be used
     * @param b a boolean to be used
     * @param c a certificate needed
     * @param t the type of event
     * @return a new event
     */
    private static Event getEvent(String s, Long l, boolean b, Certificate c, EventType t) {
        return new Event(l, s, createPositions(), s, s, c, b, t, s);
    }

    /**
     * Method to create an event model for testing.
     *
     * @param s a string needed
     * @param l a long needed
     * @param b a boolean needed
     * @param c a certificate
     * @param t an event type
     * @return a new event model
     */
    private static EventModel getEventModel(String s, Long l, boolean b, Certificate c, EventType t) {
        return new EventModel(l, s, createPositions(), s, s, c, b, t, s);
    }

    /**
     * Method to set up dependencies for every test.
     */
    @BeforeEach
    public void setup() {
        service = new EventService(repo);
        sut = new EventController(service);
    }

    /**
     * Testing if adding an event also keeps the data correctly.
     */
    @Test
    public void addEventTest() {
        try {
            var actual = sut.registerNewEvent(getEventModel("A", 1L, true, Certificate.B2, EventType.COMPETITION));
            assertEquals(actual.getBody().getId(), 1);
            assertEquals(actual.getBody().getLabel(), "A");
            assertEquals(actual.getBody().getCertificate(), Certificate.B2);
            assertEquals(actual.getBody().getType(), EventType.TRAINING);
            assertEquals(actual.getBody().getQueue().size(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Testing to verify if only complete events can be added.
     */
    @Test
    public void addEventFailTest() {
        try {
            var actual = sut.registerNewEvent(getEventModel(null, 1L, true, Certificate.B2, EventType.TRAINING));
            assertEquals(actual.getStatusCode(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Testing if adding events has an impact on the database.
     */
    @Test
    public void getAllTest() {
        try {
            sut.registerNewEvent(getEventModel("B", 1L, false, Certificate.B5, EventType.COMPETITION));
            sut.registerNewEvent(getEventModel("A", 2L, true, Certificate.B2, EventType.COMPETITION));
            assertEquals(sut.getEvents().size(), 2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
