package nl.tudelft.sem.template.database;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import nl.tudelft.sem.template.controllers.EventController;
import nl.tudelft.sem.template.services.EventService;
import nl.tudelft.sem.template.shared.entities.Event;
import nl.tudelft.sem.template.shared.entities.EventModel;
import nl.tudelft.sem.template.shared.enums.Certificate;
import nl.tudelft.sem.template.shared.enums.EventType;
import nl.tudelft.sem.template.shared.enums.PositionName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EventControllerTest {

    public TestEventRepository repo;
    public EventService service;
    private EventController sut;

    /**
     * Method to create a list of positions for testing.
     *
     * @return the list fo positions needed for an event
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

    /*
    @Test
    public void addEventFailTest() {
        try {
            var actual = sut.registerNewEvent(getEventModel(null, 1L, true, Certificate.B2, EventType.TRAINING));
            assertEquals(actual.getStatusCode(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
     */

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

}
