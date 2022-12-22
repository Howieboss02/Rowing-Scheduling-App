package converters;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import nl.tudelft.sem.template.shared.converters.ScheduleConverter;
import nl.tudelft.sem.template.shared.domain.Node;
import nl.tudelft.sem.template.shared.domain.Schedule;
import nl.tudelft.sem.template.shared.domain.TimeSlot;
import nl.tudelft.sem.template.shared.enums.Day;
import org.junit.jupiter.api.Test;
import org.springframework.data.util.Pair;

class ScheduleConverterTest {

    private final ScheduleConverter converter = new ScheduleConverter();

    @Test
    public void convertToDatabaseColumnNull() {
        assertEquals("", converter.convertToDatabaseColumn(null));
    }

    @Test
    public void convertToDatabaseColumnEmptySchedule() {
        assertEquals("//", converter.convertToDatabaseColumn(new Schedule()));
    }

    @Test
    void convertToDatabaseColumnOnlyRecurring() {
        List<TimeSlot> slots = new ArrayList<>();
        slots.add(new TimeSlot(1, Day.MONDAY, new Node(60, 61)));

        Schedule schedule = new Schedule(slots, new ArrayList<>(), new ArrayList<>());
        assertEquals("1,MONDAY,01:00-01:01//", converter.convertToDatabaseColumn(schedule));
    }

    @Test
    void convertToDatabaseColumnNoAdded() {
        List<TimeSlot> slots = new ArrayList<>();
        slots.add(new TimeSlot(1, Day.MONDAY, new Node(60, 61)));

        Schedule schedule = new Schedule(slots, slots, new ArrayList<>());
        assertEquals("1,MONDAY,01:00-01:01/1,MONDAY,01:00-01:01/", converter.convertToDatabaseColumn(schedule));
    }

    @Test
    void convertToDatabaseColumnNoRemoved() {
        List<TimeSlot> slots = new ArrayList<>();
        slots.add(new TimeSlot(1, Day.MONDAY, new Node(60, 61)));

        Schedule schedule = new Schedule(slots, new ArrayList<>(), slots);
        assertEquals("1,MONDAY,01:00-01:01//1,MONDAY,01:00-01:01", converter.convertToDatabaseColumn(schedule));
    }

    @Test
    void convertToDatabaseColumnAll() {
        List<TimeSlot> slots = new ArrayList<>();
        slots.add(new TimeSlot(1, Day.MONDAY, new Node(60, 61)));
        List<TimeSlot> slots2 = new ArrayList<>();
        slots2.add(new TimeSlot(1, Day.TUESDAY, new Node(60, 61)));
        slots2.add(new TimeSlot(2, Day.WEDNESDAY, new Node(120, 121)));

        Schedule schedule = new Schedule(slots, slots2, slots);
        assertEquals("1,MONDAY,01:00-01:01/1,TUESDAY,01:00-01:01;2,WEDNESDAY,02:00-02:01/1,MONDAY,01:00-01:01"
                , converter.convertToDatabaseColumn(schedule));
    }

    @Test
    public void testConvertToEntityAttributeNull() {
        assertEquals(new Schedule(), converter.convertToEntityAttribute(null));
    }

    @Test
    public void testConvertToEntityAttributeEmpty() {
        assertEquals(new Schedule(), converter.convertToEntityAttribute(""));
    }

    @Test
    void convertToEntityAttributeOnlyRecurring() {
        List<TimeSlot> slots = new ArrayList<>();
        slots.add(new TimeSlot(-1, Day.MONDAY, new Node(60, 61)));

        Schedule schedule = new Schedule(slots, new ArrayList<>(), new ArrayList<>());
        assertEquals(schedule, converter.convertToEntityAttribute("-1,MONDAY,01:00-01:01//"));
    }

    @Test
    void convertToEntityAttributeNoAdded() {
        List<TimeSlot> slots = new ArrayList<>();
        slots.add(new TimeSlot(1, Day.MONDAY, new Node(60, 61)));

        Schedule schedule = new Schedule(slots, new ArrayList<>(), new ArrayList<>());
        assertEquals(schedule, converter.convertToEntityAttribute("1,MONDAY,01:00-01:01//"));
    }

    @Test
    void convertToEntityAttributeNoRemoved() {
        List<TimeSlot> slots = new ArrayList<>();
        slots.add(new TimeSlot(1, Day.MONDAY, new Node(60, 61)));

        Schedule schedule = new Schedule(slots, new ArrayList<>(), slots);
        assertEquals(schedule, converter.convertToEntityAttribute("1,MONDAY,01:00-01:01//1,MONDAY,01:00-01:01"));
    }

    @Test
    void convertToEntityAttributeAll() {
        List<TimeSlot> slots = new ArrayList<>();
        slots.add(new TimeSlot(1, Day.MONDAY, new Node(1, 1)));
        List<TimeSlot> slots2 = new ArrayList<>();
        slots.add(new TimeSlot(1, Day.TUESDAY, new Node(1, 5)));

        Schedule schedule = new Schedule(slots2, slots, slots);
        assertEquals(schedule, converter.convertToEntityAttribute(
                "/1,MONDAY,00:01-00:01;1,TUESDAY,00:01-00:05/1,MONDAY,00:01-00:01;1,TUESDAY,00:01-00:05"));
    }
}