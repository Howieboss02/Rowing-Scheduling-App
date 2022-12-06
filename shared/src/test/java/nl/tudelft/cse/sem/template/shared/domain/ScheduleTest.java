package nl.tudelft.cse.sem.template.shared.domain;

import org.junit.jupiter.api.Test;
import org.springframework.data.util.Pair;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ScheduleTest {
    @Test
    void constructor() {
        Schedule s = new Schedule();
        assertEquals(s.getAddedSlots(), new ArrayList<>());
        assertEquals(s.getRemovedSlots(), new ArrayList<>());
        assertEquals(s.getRecurringSlots().size(), 7);
    }
    @Test
    void addRecurringSlot() {
        Schedule s = new Schedule();
        s.addRecurringSlot(1, Pair.of(1, 2));
        List<Pair<Integer, Integer>> p = new ArrayList<>();

        p.add(Pair.of(1, 2));
        assertEquals(s.getRecurringSlots().get(1), p);
    }

    @Test
    void removeSlot() {
        Schedule s = new Schedule();
        TimeSlot t = new TimeSlot(1,1, Pair.of(1, 2));
        s.removeSlot(t);
        List<TimeSlot> p = new ArrayList<>();

        assertEquals(s.getRemovedSlots(), p);
    }

    @Test
    void removeSlot2() {
        Schedule s = new Schedule();
        TimeSlot t = new TimeSlot(1,1, Pair.of(1, 2));
        s.addRecurringSlot(1, Pair.of(1, 2));
        s.removeSlot(t);
        List<TimeSlot> p = new ArrayList<>();
        p.add(t);

        assertEquals(s.getRemovedSlots(), p);
    }

    @Test
    void addSlot() {
        Schedule s = new Schedule();
        TimeSlot t = new TimeSlot(1,1, Pair.of(1, 2));
        s.addSlot(t);
        List<TimeSlot> p = new ArrayList<>();
        p.add(t);

        assertEquals(s.getAddedSlots(), p);
    }

    @Test
    void cleanSlots() {
        Schedule s = new Schedule();
        TimeSlot t = new TimeSlot(1,1, Pair.of(1, 2));
        s.addSlot(t);
        s.cleanSlots(2);

        assertEquals(s.getAddedSlots(), new ArrayList<TimeSlot>());
    }
}