package nl.tudelft.sem.template.converters;

import static org.junit.jupiter.api.Assertions.*;

import nl.tudelft.sem.template.shared.converters.TimeSlotConverter;
import nl.tudelft.sem.template.shared.domain.Node;
import nl.tudelft.sem.template.shared.domain.TimeSlot;
import nl.tudelft.sem.template.shared.enums.Day;
import org.junit.jupiter.api.Test;

class TimeSlotConverterTest {

    private final TimeSlotConverter converter = new TimeSlotConverter();

    // tests for TimeSlotConverter
    @Test
    void nullTest() {
        assertEquals("", converter.convertToDatabaseColumn(null));
    }

    @Test
    void emptyTest() {
        assertEquals(null, converter.convertToEntityAttribute(""));
    }

    @Test
    void convertToTimeslotCorrectly() {
        assertEquals(new TimeSlot(1, Day.MONDAY, new Node(0, 0)),
                converter.convertToEntityAttribute("1,MONDAY,00:00-00:00"));
        assertEquals(new TimeSlot(2, Day.MONDAY, new Node(1, 1)),
                converter.convertToEntityAttribute("2,MONDAY,00:01-00:01"));
        assertEquals(new TimeSlot(3, Day.MONDAY, new Node(59, 59)),
                converter.convertToEntityAttribute("3,MONDAY,00:59-00:59"));
        assertEquals(new TimeSlot(4, Day.MONDAY, new Node(60, 60)),
                converter.convertToEntityAttribute("4,MONDAY,01:00-01:00"));
    }

    @Test
    void convertToStringCorrectly() {
        assertEquals("1,MONDAY,00:00-00:00",
                converter.convertToDatabaseColumn(new TimeSlot(1, Day.MONDAY, new Node(0, 0))));
        assertEquals("2,MONDAY,00:01-00:01",
                converter.convertToDatabaseColumn(new TimeSlot(2, Day.MONDAY, new Node(1, 1))));
        assertEquals("3,MONDAY,00:59-00:59",
                converter.convertToDatabaseColumn(new TimeSlot(3, Day.MONDAY, new Node(59, 59))));
    }

}