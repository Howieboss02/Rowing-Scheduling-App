package nl.tudelft.sem.template.event;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import nl.tudelft.sem.template.shared.domain.Node;
import nl.tudelft.sem.template.shared.domain.Request;
import nl.tudelft.sem.template.shared.domain.TimeSlot;
import nl.tudelft.sem.template.shared.entities.Event;
import nl.tudelft.sem.template.shared.entities.EventModel;
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
    public void testRemovePositionSucceeds() {
        Event e = new Event();
        e.addPosition(PositionName.Coach);
        assertTrue(e.removePosition(PositionName.Coach));
    }

    @Test
    public void testRemovePositionFails() {
        Event e = new Event();
        assertFalse(e.removePosition(PositionName.Coach));
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
    public void testAllArgsConstructor() {
        Event e = new Event(1L, 1L, "A", new ArrayList<>(),
                new TimeSlot(-1, Day.FRIDAY, new Node(1, 2)), Certificate.B5, EventType.COMPETITION,
                true, "A", "A", new ArrayList<>());
        assertEquals(1L, e.getId());
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
        e.addPosition(PositionName.Cox);
        e.enqueue("Alice", PositionName.Cox);
        Request r = e.getQueue().get(0);
        assertEquals("Alice", r.getName());
        assertEquals(PositionName.Cox, r.getPosition());
    }

    @Test
    public void testEnqueueSucceeds() {
        Event e = new Event();
        e.addPosition(PositionName.Cox);
        assertTrue(e.enqueue("Alice", PositionName.Cox));
    }

    @Test
    public void testDequeue() {
        Event e = new Event();
        e.addPosition(PositionName.Cox);
        Request r = new Request("Alice", PositionName.Cox);
        e.enqueue("Alice", PositionName.Cox);
        e.dequeue(r);

        assertEquals(new ArrayList<>(), e.getQueue());
        assertEquals(new ArrayList<>(), e.getPositions());
    }

    @Test
    public void testDequeueSucceeds() {
        Event e = new Event();
        Request r = new Request("Alice", PositionName.Cox);
        e.enqueue("Alice", PositionName.Cox);
        assertTrue(e.dequeue(r));
    }

    @Test
    public void testDequeueFails() {
        Event e = new Event();
        Request r = new Request("Alice", PositionName.Cox);
        assertFalse(e.dequeue(r));
    }

    @Test
    public void messageConverterTest() {
        Event e = new Event(1L, "competition", new ArrayList<>(),
                new TimeSlot(-1, Day.FRIDAY, new Node(1, 2)), Certificate.B5, EventType.COMPETITION, true, "A", "A");

        assertEquals("competition - COMPETITION from 00:01 until 00:02 in week -1, on FRIDAY.\n", e.messageConverter());
    }

    @Test
    public void testMerge() {
        Event e = new Event(1L, "competition", new ArrayList<>(Arrays.asList(PositionName.PortSideRower)),
                new TimeSlot(-1, Day.FRIDAY, new Node(1, 2)), Certificate.B5, EventType.COMPETITION, true, "A", "A");

        TimeSlot newTime = new TimeSlot(-1, Day.SATURDAY, new Node(2, 3));
        ArrayList<PositionName> newPos = new ArrayList<>(Arrays.asList(PositionName.Cox));

        EventModel em = new EventModel(1L, "competition 1", newPos,
                newTime, Certificate.B3, EventType.TRAINING, false, "B", "B");

        Event mergedEvent = e.merge(em, true);

        assertNotNull(mergedEvent);
        assertEquals(1L, mergedEvent.getOwningUser());
        assertEquals("competition 1", mergedEvent.getLabel());
        assertEquals(newTime, mergedEvent.getTimeslot());
        assertEquals(Certificate.B3, mergedEvent.getCertificate());
        assertEquals(EventType.TRAINING, mergedEvent.getType());
        assertFalse(mergedEvent.isCompetitive());
        assertEquals("B", mergedEvent.getGender());
        assertEquals("B", mergedEvent.getOrganisation());
        assertEquals(newPos, mergedEvent.getPositions());
    }

    @Test
    public void testMerge_invalidInput() {
        Event e = new Event(1L, "competition", new ArrayList<>(),
                new TimeSlot(-1, Day.FRIDAY, new Node(1, 2)), Certificate.B5, EventType.COMPETITION, true, "A", "A");

        EventModel em = new EventModel(2L, "competition 1", new ArrayList<>(),
                new TimeSlot(-1, Day.FRIDAY, new Node(1, 2)), Certificate.B3, EventType.TRAINING, false, "B", "B");

        Event mergedEvent = e.merge(em, true);

        assertNull(mergedEvent);
    }

    @Test
    public void testEqualsTrue() {
        Event e = new Event(1L, "A", new ArrayList<>(),
                new TimeSlot(-1, Day.FRIDAY, new Node(1, 2)), Certificate.B5, EventType.COMPETITION, true, "A", "A");
        Event f = new Event(1L, "A", new ArrayList<>(),
                new TimeSlot(-1, Day.FRIDAY, new Node(1, 2)), Certificate.B5, EventType.COMPETITION, true, "A", "A");
        assertTrue(e.equals(f));
    }

    @Test
    public void testEqualsFalse() {
        Event e = new Event(1L, "A", new ArrayList<>(),
                new TimeSlot(-1, Day.FRIDAY, new Node(1, 2)), Certificate.B5, EventType.COMPETITION, true, "A", "A");
        Event f = new Event(1L, "B", new ArrayList<>(),
                new TimeSlot(-1, Day.FRIDAY, new Node(1, 2)), Certificate.B5, EventType.COMPETITION, true, "A", "A");
        assertFalse(e.equals(f));
    }

    @Test
    public void testEqualsDifferentObjectType() {
        Event e = new Event(1L, "A", new ArrayList<>(),
                new TimeSlot(-1, Day.FRIDAY, new Node(1, 2)), Certificate.B5, EventType.COMPETITION, true, "A", "A");
        EventModel f = new EventModel(1L, "B", new ArrayList<>(),
                new TimeSlot(-1, Day.FRIDAY, new Node(1, 2)), Certificate.B5, EventType.COMPETITION, true, "A", "A");
        assertFalse(e.equals(f));
    }

    @Test
    public void testHashCode() {
        Event e = new Event(1L, "A", new ArrayList<>(),
                new TimeSlot(-1, Day.FRIDAY, new Node(1, 2)), Certificate.B5, EventType.COMPETITION, true, "A", "A");
        int hashCode = Objects.hash("A");
        assertEquals(e.hashCode(), hashCode);
    }

    @Test
    public void testToString() {
        Event e = new Event(1L, "A", new ArrayList<>(),
                new TimeSlot(-1, Day.FRIDAY, new Node(1, 2)), Certificate.B5, EventType.COMPETITION, true, "A", "A");
        String expected = "Event(id=null, owningUser=1, label=A, positions=[], " +
                "timeslot=TimeSlot(week=-1, day=FRIDAY, time=Node(first=1, second=2)), " +
                "certificate=B5, type=COMPETITION, isCompetitive=true, gender=A, organisation=A, queue=[])";
        assertEquals(expected, e.toString());
    }
}
