package Commons;

import org.junit.jupiter.api.Test;
import org.springframework.data.util.Pair;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ScheduleTest {
    @Test
    void constructor() {
        Schedule s = new Schedule();
        assertEquals(s.addedSlots, new ArrayList<>());
        assertEquals(s.removedSlots, new ArrayList<>());
        assertEquals(s.recurringSlots, new ArrayList<>(7));
    }
    @Test
    void addRecurringSlot() {
        Schedule s = new Schedule();
        s.addRecurringSlot(1, Pair.of(1, 2));
        List<Pair<Integer, Integer>> p = new ArrayList<>();

        assertEquals(s.recurringSlots.get(1), p);
    }

    @Test
    void removeSlot() {
        Schedule s = new Schedule();
        TimeSlot t = new TimeSlot(1,1, Pair.of(1, 2));
        s.removeSlot(t);
        List<TimeSlot> p = new ArrayList<>();
        p.add(t);

        assertEquals(s.removedSlots, p);
    }

    @Test
    void addSlot() {
        Schedule s = new Schedule();
        TimeSlot t = new TimeSlot(1,1, Pair.of(1, 2));
        s.addSlot(t);
        List<TimeSlot> p = new ArrayList<>();
        p.add(t);

        assertEquals(s.addedSlots, p);
    }

    @Test
    void cleanSlots() {
        Schedule s = new Schedule();
        TimeSlot t = new TimeSlot(1,1, Pair.of(1, 2));
        s.addSlot(t);
        s.cleanSlots(2);

        assertEquals(s.addedSlots, new ArrayList<TimeSlot>());
    }

    @Test
    void testEquals() {
    }

    @Test
    void testHashCode() {
    }

    @Test
    void testToString() {
    }
}