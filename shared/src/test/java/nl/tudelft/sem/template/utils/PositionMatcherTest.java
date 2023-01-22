package nl.tudelft.sem.template.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import nl.tudelft.sem.template.shared.domain.Node;
import nl.tudelft.sem.template.shared.domain.Position;
import nl.tudelft.sem.template.shared.domain.TimeSlot;
import nl.tudelft.sem.template.shared.entities.Event;
import nl.tudelft.sem.template.shared.entities.User;
import nl.tudelft.sem.template.shared.enums.Certificate;
import nl.tudelft.sem.template.shared.enums.Day;
import nl.tudelft.sem.template.shared.enums.EventType;
import nl.tudelft.sem.template.shared.enums.PositionName;
import nl.tudelft.sem.template.shared.utils.PositionMatcher;
import org.junit.jupiter.api.Test;


public class PositionMatcherTest {

    private User getUser() {
        User user = new User("user1", "user1@email.com", "A", "test@email.com", "A",
                Certificate.B3, Arrays.asList(new Position(PositionName.Coach, true)));
        user.setId(2L);
        return user;
    }

    private List<Event> getEvents() {
        Event e1 = new Event(1L, "competition", Arrays.asList(PositionName.Coach),
                new TimeSlot(1, Day.FRIDAY, new Node(1, 2)), Certificate.B5, EventType.COMPETITION, true, "A", "A");
        Event e2 = new Event(1L, "training", Arrays.asList(PositionName.Coach, PositionName.Coach),
                new TimeSlot(2, Day.FRIDAY, new Node(1, 2)), Certificate.B3, EventType.TRAINING, false, "A", "A");
        Event e3 = new Event(1L, "competition", Arrays.asList(PositionName.Cox),
                new TimeSlot(3, Day.FRIDAY, new Node(1, 2)), Certificate.B5, EventType.COMPETITION, true, "A", "A");

        return Arrays.asList(e1, e2, e3);
    }

    @Test
    public void testMatchPositions() {
        User user = getUser();
        user.addRecurringSlot(new TimeSlot(-1, Day.FRIDAY, new Node(1, 2)));
        List<Event> events = getEvents();
        List<Position> positions = user.getPositions();
        List<Event> matchedEvents = PositionMatcher.matchPositions(positions, events, user);

        assertEquals(2, matchedEvents.size());
        assertTrue(matchedEvents.contains(events.get(0)));
        assertTrue(matchedEvents.contains(events.get(1)));
    }

    @Test
    public void testMatchPositions_emptyEventsList() {
        User user = getUser();
        user.addRecurringSlot(new TimeSlot(-1, Day.FRIDAY, new Node(1, 2)));

        List<Event> events = new ArrayList<>();
        List<Position> positions = user.getPositions();
        List<Event> matchedEvents = PositionMatcher.matchPositions(positions, events, user);

        assertEquals(0, matchedEvents.size());
    }

    @Test
    public void testMatchPositions_emptySchedule() {
        User user = getUser();

        List<Event> events = getEvents();
        List<Position> positions = user.getPositions();
        List<Event> matchedEvents = PositionMatcher.matchPositions(positions, events, user);

        assertEquals(0, matchedEvents.size());
    }

    @Test
    public void testMatchPositions_competitiveness() {
        User user = new User("user1", "user1@email.com", "A", "test@email.com", "A",
                Certificate.B3, Arrays.asList(new Position(PositionName.Coach, false)));
        user.setId(2L);
        user.addRecurringSlot(new TimeSlot(-1, Day.FRIDAY, new Node(1, 2)));

        List<Event> events = getEvents();
        List<Position> positions = user.getPositions();
        List<Event> matchedEvents = PositionMatcher.matchPositions(positions, events, user);

        assertEquals(1, matchedEvents.size());
        assertTrue(matchedEvents.contains(events.get(1)));
    }
}
