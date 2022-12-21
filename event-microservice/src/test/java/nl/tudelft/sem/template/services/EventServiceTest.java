package nl.tudelft.sem.template.services;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.template.database.TestEventRepository;
import nl.tudelft.sem.template.shared.domain.Request;
import nl.tudelft.sem.template.shared.entities.Event;
import nl.tudelft.sem.template.shared.entities.User;
import nl.tudelft.sem.template.shared.enums.Certificate;
import nl.tudelft.sem.template.shared.enums.EventType;
import nl.tudelft.sem.template.shared.enums.PositionName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EventServiceTest {
    public TestEventRepository repo;

    private EventService service;

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

    @BeforeEach
    public void setup() {
        repo = new TestEventRepository();
        service = new EventService(repo);
    }

    @Test
    void testGetAllEvents() {
        try {
            Event event = getEvent("A", 1L, Certificate.B2, EventType.COMPETITION);
            List<Event> events = List.of(event);

            service.insert(event);
            assertEquals(events, service.getAllEvents());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testGetAllEventsByUser() {
        try {
            Event event = getEvent("A", 1L, Certificate.B2, EventType.COMPETITION);
            Event event2 = getEvent("A", 2L, Certificate.B2, EventType.COMPETITION);
            List<Event> events = List.of(event);

            service.insert(event);
            service.insert(event2);
            assertEquals(events, service.getAllEventsByUser(1L));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testInsertFail() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            service.insert(null);
        });

        assertEquals("Event cannot be null", exception.getMessage());
    }

    @Test
    void testDeleteById() {
        try {
            Event event = getEvent("A", 1L, Certificate.B2, EventType.COMPETITION);

            service.insert(event);
            service.deleteById(1L);
            assertEquals(new ArrayList<Event>(), service.getAllEvents());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testDeleteByIdFail() {
        Exception exception = assertThrows(Exception.class, () -> {
            service.deleteById(1L);
        });

        assertEquals("ID does not exist", exception.getMessage());
    }

    @Test
    void testGetById() {
        try {
            Event event = getEvent("A", 4L, Certificate.B2, EventType.COMPETITION);
            Event event2 = getEvent("B", 5L, Certificate.B2, EventType.COMPETITION);

            service.insert(event);
            service.insert(event2);
            assertEquals(event, service.getById(1L).get());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testGetByIdFail() {
        try {
            Event event = getEvent("A", 4L, Certificate.B2, EventType.COMPETITION);
            Event event2 = getEvent("B", 5L, Certificate.B2, EventType.COMPETITION);

            service.insert(event);
            service.insert(event2);

            assertEquals(Optional.empty(), service.getById(3L));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testUpdateById() {
        try {
            Event event = getEvent("A", 4L, Certificate.B2, EventType.COMPETITION);
            Event updated = getEvent("B", 1L, Certificate.B5, EventType.COMPETITION);

            service.insert(event);

            assertEquals(event, service.updateById(1L, 1L, "B", createPositions(), "B",
                    "B", Certificate.B5, EventType.COMPETITION, true, "B").get());
            assertEquals(updated, service.getById(1L).get());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testUpdateByIdNoChanges() {
        try {
            Event event = getEvent("A", 4L, Certificate.B2, EventType.COMPETITION);

            service.insert(event);

            assertEquals(event, service.updateById(null, null, null, null, null, null, null, null, false, null).get());
            assertEquals(event, service.getById(1L).get());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void updateByIdNoEvent() {
        assertEquals(Optional.empty(), service.updateById(null, null, null, null, null, null, null, null, false, null));
    }

    @Test
    void testGetRequests() {
        try {
            Request r = new Request("Bob", PositionName.Cox);
            Event event = getEvent("A", 4L, Certificate.B2, EventType.COMPETITION);
            event.getQueue().add(r);

            service.insert(event);

            assertEquals(List.of(r), service.getRequests(1L));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testGetRequestsFail() {
        assertEquals(new ArrayList<Request>(), service.getRequests(1L));
    }

    @Test
    void testEnqueueById() {
        try {
            Request r = new Request("Bob", PositionName.Cox);
            Event event = getEvent("A", 4L, Certificate.B2, EventType.COMPETITION);
            Event correctEvent = getEvent("A", 4L, Certificate.B2, EventType.COMPETITION);
            correctEvent.getQueue().add(r);
            User user = new User();
            user.setNetId("Bob");

            service.insert(event);
            service.enqueueById(1L, user, PositionName.Cox);

            assertEquals(List.of(r), service.getById(1L).get().getQueue());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testEnqueueByIdNoEvent() {
        User user = new User();
        user.setNetId("Bob");
        service.enqueueById(1L, user, PositionName.Cox);

        assertEquals(new ArrayList<>(), service.getAllEvents());

    }

    @Test
    void testDequeueById() {
        try {
            Event event = getEvent("A", 4L, Certificate.B2, EventType.COMPETITION);
            User user = new User();
            user.setNetId("Bob");

            service.insert(event);
            service.enqueueById(1L, user, PositionName.Cox);

            assertTrue(service.dequeueById(1L, new Request("Bob", PositionName.Cox)));
            assertEquals(new ArrayList<>(), service.getById(1L).get().getQueue());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testDequeueByIdNoEvent() {
        Request r = new Request("Bob", PositionName.Cox);
        assertFalse(service.dequeueById(1L, r));
    }

    @Test
    void testDequeueByIdNoRequest() {
        try {
            Request r = new Request("Bob", PositionName.Cox);
            Event event = getEvent("A", 4L, Certificate.B2, EventType.COMPETITION);

            service.insert(event);

            assertFalse(service.dequeueById(1L, r));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void removePositionById() {
        try {
            Event event = getEvent("A", 4L, Certificate.B2, EventType.COMPETITION);
            List<PositionName> positions = createPositions();
            positions.remove(PositionName.Cox);

            service.insert(event);

            assertTrue(service.removePositionById(1L, PositionName.Cox));
            assertEquals(positions, service.getById(1L).get().getPositions());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void removePositionByIdFail() {
        assertFalse(service.removePositionById(1L, PositionName.Cox));
    }

    @Test
    void getMatchedEvents() {
    }
}