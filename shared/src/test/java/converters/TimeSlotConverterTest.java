package converters;

import static org.junit.jupiter.api.Assertions.*;

import nl.tudelft.sem.template.shared.converters.TimeSlotConverter;
import nl.tudelft.sem.template.shared.domain.Node;
import nl.tudelft.sem.template.shared.domain.TimeSlot;
import nl.tudelft.sem.template.shared.enums.Day;
import org.junit.jupiter.api.Test;
import org.springframework.data.util.Pair;

class TimeSlotConverterTest {

    private final TimeSlotConverter converter = new TimeSlotConverter();

    @Test
    public void convertToDatabaseColumnNull() {
        assertEquals("", converter.convertToDatabaseColumn(null));
    }

    @Test
    public void convertToDatabaseColumn() {
        assertEquals("1,MONDAY,1,1", converter.convertToDatabaseColumn(new TimeSlot(1, Day.MONDAY, new Node(1, 1))));
    }

    @Test
    public void convertToEntityAttributeEmpty() {
        assertEquals(null, converter.convertToEntityAttribute(""));
    }

    @Test
    public void convertToEntityAttribute() {
        assertEquals(new TimeSlot(1, Day.MONDAY, new Node(1, 1)), converter.convertToEntityAttribute("1,MONDAY,1,1"));
    }
}