package event;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import nl.tudelft.sem.template.shared.domain.Node;
import nl.tudelft.sem.template.shared.domain.Request;
import nl.tudelft.sem.template.shared.domain.TimeSlot;
import nl.tudelft.sem.template.shared.entities.Event;
import nl.tudelft.sem.template.shared.enums.Certificate;
import nl.tudelft.sem.template.shared.enums.Day;
import nl.tudelft.sem.template.shared.enums.EventType;
import nl.tudelft.sem.template.shared.enums.PositionName;
import org.junit.jupiter.api.Test;

public class EventTest {

    @Test
    public void testAddPosition() {
        Event e = new Event();
        e.addPosition(PositionName.Coach);
        assertNotNull(e.getPositions());
        PositionName p = e.getPositions().get(0);
        assertEquals(PositionName.Coach, p);
    }

    @Test
    public void testRemovePosition() {
        Event e = new Event();
        e.addPosition(PositionName.Coach);
        e.addPosition(PositionName.Cox);
        e.addPosition(PositionName.PortSideRower);
        e.addPosition(PositionName.Startboard);
        e.addPosition(PositionName.ScullingRower);
        e.removePosition(PositionName.Coach);
        assertEquals(4, e.getPositions().size());
    }

    @Test
    public void testConstructor() {
        Event e = new Event(1L, "A", new ArrayList<>(),
                new TimeSlot(-1, Day.FRIDAY, new Node(1, 2)), Certificate.B5, EventType.COMPETITION, true, "A", "A");
        assertEquals(1L, e.getOwningUser());
        assertEquals("A", e.getLabel());
        assertEquals(new ArrayList<>(), e.getPositions());
        assertEquals(new TimeSlot(-1, Day.FRIDAY, new Node(1, 2)), e.getTimeslot());
        assertEquals(Certificate.B5, e.getCertificate());
        assertEquals(EventType.COMPETITION, e.getType());
        assertTrue(e.isCompetitive());
        assertEquals("A", e.getOrganisation());
        assertEquals(new ArrayList<>(), e.getQueue());

    }

    @Test
    public void testEnqueue() {
        Event e = new Event();
        Request r = new Request("Alice", PositionName.Cox);
        e.enqueue("Alice", PositionName.Cox);

        assertEquals(List.of(r), e.getQueue());
    }

    @Test
    public void testDequeue() {
        Event e = new Event();
        Request r = new Request("Alice", PositionName.Cox);
        e.enqueue("Alice", PositionName.Cox);
        e.dequeue(r);

        assertEquals(new ArrayList<>(), e.getQueue());
    }

    @Test
    public void messageConverterTest() {
        Event e = new Event(1L, "competition", new ArrayList<>(),
                new TimeSlot(-1, Day.FRIDAY, new Node(1, 2)), Certificate.B5, EventType.COMPETITION, true, "A", "A");

        assertEquals("competition - COMPETITION from 00:01 until 00:02 in week -1, on FRIDAY.\n", e.messageConverter());
    }
}
