package converters;

import nl.tudelft.sem.template.shared.converters.StringTimeToMinutesConverter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class StringTimeToMinutesConverterTest {

    private StringTimeToMinutesConverter converter = new StringTimeToMinutesConverter();

    @Test
    public void testConvertToDatabaseColumn_validInput() {
        int result = converter.convertToDatabaseColumn("12:34");
        assertEquals(754, result);
    }

    @Test
    public void testConvertToDatabaseColumn_nullInput() {
        Integer result = converter.convertToDatabaseColumn(null);
        assertNull(result);
    }

    @Test
    public void testConvertToDatabaseColumn_emptyInput() {
        Integer result = converter.convertToDatabaseColumn("");
        assertNull(result);
    }

    @Test
    public void testConvertToDatabaseColumn_invalidFormat() {
        Integer result = converter.convertToDatabaseColumn("1234");
        assertNull(result);
    }

    @Test
    public void testConvertToEntityAttribute_validInput() {
        String result = converter.convertToEntityAttribute(754);
        assertEquals("12:34", result);
    }

    @Test
    public void testConvertToEntityAttribute_nullInput() {
        String result = converter.convertToEntityAttribute(null);
        assertEquals("", result);
    }

    @Test
    public void testConvertToEntityAttribute_zeroInput() {
        String result = converter.convertToEntityAttribute(0);
        assertEquals("00:00", result);
    }

    @Test
    public void testConvertToEntityAttribute_maximumInput() {
        String result = converter.convertToEntityAttribute(1440);
        assertEquals("24:00", result);
    }

    @Test
    public void testConvertToEntityAttribute_minimumInput() {
        String result = converter.convertToEntityAttribute(1);
        assertEquals("00:01", result);
    }
}


