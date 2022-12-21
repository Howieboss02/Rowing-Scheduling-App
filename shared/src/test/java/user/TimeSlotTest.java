package user;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import nl.tudelft.sem.template.shared.domain.Schedule;
import nl.tudelft.sem.template.shared.domain.TimeSlot;
import nl.tudelft.sem.template.shared.enums.Day;
import org.junit.jupiter.api.Test;
import org.springframework.data.util.Pair;

class TimeSlotTest {

    @Test
    void matchScheduleRecurrent() {
        TimeSlot ts = new TimeSlot(1, Day.MONDAY, Pair.of(1, 2));
        Schedule schedule = new Schedule();
        schedule.addRecurringSlot(new TimeSlot(3, Day.MONDAY, Pair.of(1, 2)));
        assertTrue(ts.matchSchedule(schedule));
    }

    @Test
    void matchScheduleRecurrentBiggerInterval() {
        TimeSlot ts = new TimeSlot(1, Day.MONDAY, Pair.of(1, 2));
        Schedule schedule = new Schedule();
        schedule.addRecurringSlot(new TimeSlot(3, Day.MONDAY, Pair.of(0, 5)));
        assertTrue(ts.matchSchedule(schedule));
    }

    @Test
    void noMatchScheduleRecurrent() {
        TimeSlot ts = new TimeSlot(1, Day.MONDAY, Pair.of(1, 2));
        Schedule schedule = new Schedule();
        schedule.addRecurringSlot(new TimeSlot(3, Day.MONDAY, Pair.of(2, 3)));
        assertFalse(ts.matchSchedule(schedule));
    }

    @Test
    void noMatchScheduleRecurrentIntersectedIntervals() {
        TimeSlot ts = new TimeSlot(1, Day.MONDAY, Pair.of(2, 5));
        Schedule schedule = new Schedule();
        schedule.addRecurringSlot(new TimeSlot(3, Day.MONDAY, Pair.of(2, 3)));
        schedule.addRecurringSlot(new TimeSlot(3, Day.MONDAY, Pair.of(3, 5)));
        assertFalse(ts.matchSchedule(schedule));
    }

    @Test
    void matchScheduleSlot() {
        TimeSlot ts = new TimeSlot(1, Day.MONDAY, Pair.of(1, 2));
        Schedule schedule = new Schedule();
        schedule.addSlot(new TimeSlot(1, Day.MONDAY, Pair.of(1, 2)));
        assertTrue(ts.matchSchedule(schedule));
    }

    @Test
    void matchScheduleSlotBiggerInterval() {
        TimeSlot ts = new TimeSlot(1, Day.MONDAY, Pair.of(1, 2));
        Schedule schedule = new Schedule();
        schedule.addSlot(new TimeSlot(1, Day.MONDAY, Pair.of(0, 5)));
        assertTrue(ts.matchSchedule(schedule));
    }

    @Test
    void noMatchScheduleSlot() {
        TimeSlot ts = new TimeSlot(1, Day.MONDAY, Pair.of(1, 2));
        Schedule schedule = new Schedule();
        schedule.addSlot(new TimeSlot(3, Day.MONDAY, Pair.of(1, 2)));
        assertFalse(ts.matchSchedule(schedule));
    }

    @Test
    void noMatchScheduleRemoved() {
        TimeSlot ts = new TimeSlot(1, Day.MONDAY, Pair.of(1, 2));
        Schedule schedule = new Schedule();
        schedule.addRecurringSlot(new TimeSlot(-1, Day.MONDAY, Pair.of(1, 2)));
        schedule.removeSlot(new TimeSlot(1, Day.MONDAY, Pair.of(1, 2)));
        assertFalse(ts.matchSchedule(schedule));
    }

    @Test
    void matchScheduleRemoved() {
        TimeSlot ts = new TimeSlot(1, Day.MONDAY, Pair.of(1, 2));
        Schedule schedule = new Schedule();
        schedule.addRecurringSlot(new TimeSlot(-1, Day.MONDAY, Pair.of(1, 2)));
        schedule.removeSlot(new TimeSlot(2, Day.MONDAY, Pair.of(1, 2)));
        assertTrue(ts.matchSchedule(schedule));
    }

    @Test
    void intersect() {
        List<TimeSlot> list = new ArrayList<>();

        list.add(new TimeSlot(-1, Day.MONDAY, Pair.of(1, 2)));
        list.add(new TimeSlot(-1, Day.MONDAY, Pair.of(4, 5)));
        list.add(new TimeSlot(-1, Day.MONDAY, Pair.of(8, 11)));

        List<TimeSlot> result = new ArrayList<>();
        result.add(new TimeSlot(1, Day.MONDAY, Pair.of(4, 5)));
        result.add(new TimeSlot(1, Day.MONDAY, Pair.of(8, 10)));

        TimeSlot slot = new TimeSlot(1, Day.MONDAY, Pair.of(2, 10));
        assertEquals(slot.intersect(list), result);

    }

    @Test
    void difference() {
        List<TimeSlot> list = new ArrayList<>();

        list.add(new TimeSlot(-1, Day.MONDAY, Pair.of(1, 2)));
        list.add(new TimeSlot(-1, Day.MONDAY, Pair.of(4, 5)));
        list.add(new TimeSlot(-1, Day.MONDAY, Pair.of(8, 11)));

        List<TimeSlot> result = new ArrayList<>();
        result.add(new TimeSlot(1, Day.MONDAY, Pair.of(2, 4)));
        result.add(new TimeSlot(1, Day.MONDAY, Pair.of(5, 8)));

        TimeSlot slot = new TimeSlot(1, Day.MONDAY, Pair.of(2, 10));
        assertEquals(slot.difference(list), result);
    }
}