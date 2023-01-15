package utils;

import nl.tudelft.sem.template.shared.domain.Node;
import nl.tudelft.sem.template.shared.domain.Position;
import nl.tudelft.sem.template.shared.domain.TimeSlot;
import nl.tudelft.sem.template.shared.entities.Event;
import nl.tudelft.sem.template.shared.entities.User;
import nl.tudelft.sem.template.shared.enums.Certificate;
import nl.tudelft.sem.template.shared.enums.Day;
import nl.tudelft.sem.template.shared.enums.EventType;
import nl.tudelft.sem.template.shared.enums.PositionName;
import nl.tudelft.sem.template.shared.utils.ValidityChecker;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ValidityCheckerTest {

    @Test
    public void testCanJoin_validInput() {
        User user = new User("user1", "user1@email.com", "A", "test@email.com", "A",
                Certificate.B5, Arrays.asList(new Position(PositionName.Coach, true)));
        user.setId(2L);
        user.addRecurringSlot(new TimeSlot(-1, Day.FRIDAY, new Node(1, 2)));
        Event event = new Event(1L, "competition", new ArrayList<>(),
                new TimeSlot(1, Day.FRIDAY, new Node(1, 2)), Certificate.B3, EventType.COMPETITION, true, "A", "A");

        ValidityChecker vc = new ValidityChecker(event, user);

        assertTrue(vc.canJoin(PositionName.Coach, 0));
    }

    @Test
    public void testCanJoin_onTime() {
        User user = new User("user1", "user1@email.com", "A", "test@email.com", "A",
                Certificate.B5, List.of(new Position(PositionName.Coach, true)));
        user.setId(2L);
        user.addRecurringSlot(new TimeSlot(-1, Day.FRIDAY, new Node(1, 2)));
        Event event = new Event(1L, "competition", List.of(PositionName.Coach),
                new TimeSlot(5, Day.FRIDAY, new Node(10, 20)), Certificate.B3, EventType.COMPETITION, true, "A", "A");

        ValidityChecker vc = new ValidityChecker(event, user);

        assertFalse(vc.canJoin(PositionName.Coach, 8000));
    }

    @Test
    public void testCanJoin_onTimeTraining() {
        User user = new User("user1", "user1@email.com", "A", "test@email.com", "A",
                Certificate.B5, List.of(new Position(PositionName.Coach, true)));
        user.setId(2L);
        user.addRecurringSlot(new TimeSlot(-1, Day.FRIDAY, new Node(1, 2)));
        Event event = new Event(1L, "competition", List.of(PositionName.Coach),
                new TimeSlot(5, Day.FRIDAY, new Node(10, 20)), Certificate.B3, EventType.TRAINING, true, "A", "A");

        ValidityChecker vc = new ValidityChecker(event, user);

        assertFalse(vc.canJoin(PositionName.Coach, 7260));
    }

    @Test
    public void testCanJoin_onTimeTrainingValid() {
        User user = new User("user1", "user1@email.com", "A", "test@email.com", "A",
                Certificate.B5, List.of(new Position(PositionName.Coach, true)));
        user.setId(2L);
        user.addRecurringSlot(new TimeSlot(-1, Day.FRIDAY, new Node(1, 2)));
        Event event = new Event(1L, "competition", List.of(PositionName.Coach),
                new TimeSlot(5, Day.FRIDAY, new Node(10, 20)), Certificate.B3, EventType.TRAINING, true, "A", "A");

        ValidityChecker vc = new ValidityChecker(event, user);

        assertTrue(vc.canJoin(PositionName.Coach, 5790));
    }

    @Test
    public void testCanJoin_creator() {
        User user = new User("user1", "user1@email.com", "A", "test@email.com", "A",
                Certificate.B5, Arrays.asList(new Position(PositionName.Coach, true)));
        user.setId(1L);
        user.addRecurringSlot(new TimeSlot(-1, Day.FRIDAY, new Node(1, 2)));
        Event event = new Event(1L, "competition", List.of(PositionName.Coach),
                new TimeSlot(1, Day.FRIDAY, new Node(1, 2)), Certificate.B3, EventType.COMPETITION, true, "A", "A");

        ValidityChecker vc = new ValidityChecker(event, user);

        assertFalse(vc.canJoin(PositionName.Coach, 0));
    }

    @Test
    public void testCanJoin_competitive1() {
        User user = new User("user1", "user1@email.com", "A", "test@email.com", "A",
                Certificate.B5, Arrays.asList(new Position(PositionName.Coach, false)));
        user.setId(2L);
        user.addRecurringSlot(new TimeSlot(-1, Day.FRIDAY, new Node(1, 2)));
        Event event = new Event(1L, "competition", List.of(PositionName.Coach),
                new TimeSlot(1, Day.FRIDAY, new Node(1, 2)), Certificate.B3, EventType.COMPETITION, true, "A", "A");

        ValidityChecker vc = new ValidityChecker(event, user);

        assertFalse(vc.canJoin(PositionName.Coach, 0));
    }

    @Test
    public void testCanJoin_competitive2() {
        User user = new User("user1", "user1@email.com", "A", "test@email.com", "A",
                Certificate.B5, Arrays.asList(new Position(PositionName.Coach, false)));
        user.setId(2L);
        user.addRecurringSlot(new TimeSlot(-1, Day.FRIDAY, new Node(1, 2)));
        Event event = new Event(1L, "competition", List.of(PositionName.Coach),
                new TimeSlot(1, Day.FRIDAY, new Node(1, 2)), Certificate.B3, EventType.COMPETITION, false, "A", "A");

        ValidityChecker vc = new ValidityChecker(event, user);

        assertTrue(vc.canJoin(PositionName.Coach, 0));
    }

    @Test
    public void testCanJoin_genderAndOrganization1() {
        User user = new User("user1", "user1@email.com", "A", "test@email.com", "B",
                Certificate.B5, Arrays.asList(new Position(PositionName.Coach, true)));
        user.setId(2L);
        user.addRecurringSlot(new TimeSlot(-1, Day.FRIDAY, new Node(1, 2)));
        Event event = new Event(1L, "competition", List.of(PositionName.Coach),
                new TimeSlot(1, Day.FRIDAY, new Node(1, 2)), Certificate.B3, EventType.COMPETITION, true, "A", "A");

        ValidityChecker vc = new ValidityChecker(event, user);

        assertFalse(vc.canJoin(PositionName.Coach, 0));
    }

    @Test
    public void testCanJoin_genderAndOrganization2() {
        User user = new User("user1", "user1@email.com", "B", "test@email.com", "A",
                Certificate.B5, Arrays.asList(new Position(PositionName.Coach, true)));
        user.setId(2L);
        user.addRecurringSlot(new TimeSlot(-1, Day.FRIDAY, new Node(1, 2)));
        Event event = new Event(1L, "competition", List.of(PositionName.Coach),
                new TimeSlot(1, Day.FRIDAY, new Node(1, 2)), Certificate.B3, EventType.COMPETITION, true, "A", "A");

        ValidityChecker vc = new ValidityChecker(event, user);

        assertFalse(vc.canJoin(PositionName.Coach, 0));
    }

    @Test
    public void testCanJoin_certificate() {
        User user = new User("user1", "user1@email.com", "A", "test@email.com", "A",
                Certificate.B3, Arrays.asList(new Position(PositionName.Coach, true)));
        user.setId(2L);
        user.addRecurringSlot(new TimeSlot(-1, Day.FRIDAY, new Node(1, 2)));
        Event event = new Event(1L, "competition", new ArrayList<>(),
                new TimeSlot(1, Day.FRIDAY, new Node(1, 2)), Certificate.B5, EventType.COMPETITION, true, "A", "A");

        ValidityChecker vc = new ValidityChecker(event, user);

        assertFalse(vc.canJoin(PositionName.Coach, 0));
    }

    @Test
    public void testCanJoin_training() {
        User user = new User("user1", "user1@email.com", "A", "test@email.com", "A",
                Certificate.B6, List.of(new Position(PositionName.Coach, true)));
        user.setId(2L);
        user.addRecurringSlot(new TimeSlot(-1, Day.FRIDAY, new Node(1, 2)));
        Event event = new Event(1L, "training", List.of(PositionName.Coach),
                new TimeSlot(1, Day.FRIDAY, new Node(1, 2)), Certificate.B5, EventType.TRAINING, true, "A", "A");

        ValidityChecker vc = new ValidityChecker(event, user);

        assertTrue(vc.canJoin(PositionName.Coach, 0));
    }

    @Test
    public void testCanBeMatched_validInput() {
        User user = new User("user1", "user1@email.com", "A", "test@email.com", "A",
                Certificate.B3, Arrays.asList(new Position(PositionName.Coach, true)));
        user.setId(2L);
        user.addRecurringSlot(new TimeSlot(-1, Day.FRIDAY, new Node(1, 2)));
        ValidityChecker vc = new ValidityChecker(user);

        assertTrue(vc.canBeMatched());
    }

    @Test
    public void testCanBeMatched_missingCertificate() {
        User user = new User("user1", "user1@email.com", "A", "test@email.com", "A",
                null, Arrays.asList(new Position(PositionName.Coach, true)));
        user.setId(2L);
        user.addRecurringSlot(new TimeSlot(-1, Day.FRIDAY, new Node(1, 2)));
        ValidityChecker vc = new ValidityChecker(user);

        assertFalse(vc.canBeMatched());
    }

    @Test
    public void testCanBeMatched_missingOrganization() {
        User user = new User("user1", "user1@email.com", null, "test@email.com", null,
                Certificate.B3, Arrays.asList(new Position(PositionName.Coach, true)));
        user.setId(2L);
        user.addRecurringSlot(new TimeSlot(-1, Day.FRIDAY, new Node(1, 2)));
        ValidityChecker vc = new ValidityChecker(user);

        assertFalse(vc.canBeMatched());
    }

    @Test
    public void testCanBeMatched_missingPositions() {
        User user = new User("user1", "user1@email.com", "A", "test@email.com", "A",
                Certificate.B3, null);
        user.setId(2L);
        user.addRecurringSlot(new TimeSlot(-1, Day.FRIDAY, new Node(1, 2)));
        ValidityChecker vc = new ValidityChecker(user);

        assertFalse(vc.canBeMatched());
    }

    @Test
    public void testCanBeMatched_emptyPositions() {
        User user = new User("user1", "user1@email.com", "A", "test@email.com", "A",
                Certificate.B3, new ArrayList<>());
        user.setId(2L);
        user.addRecurringSlot(new TimeSlot(-1, Day.FRIDAY, new Node(1, 2)));
        ValidityChecker vc = new ValidityChecker(user);

        assertFalse(vc.canBeMatched());
    }
}
