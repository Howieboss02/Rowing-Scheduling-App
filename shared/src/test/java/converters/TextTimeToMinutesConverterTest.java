package converters;

import nl.tudelft.sem.template.shared.converters.TextTimeToMinutesConverter;
import org.junit.jupiter.api.Test;
import org.springframework.data.util.Pair;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TextTimeToMinutesConverterTest {
    // tests for TextTimeToMinutesConverter
    private final TextTimeToMinutesConverter converter = new TextTimeToMinutesConverter();

    @Test
    void nullTest() {
        assertEquals("", converter.convertToDatabaseColumn(null));
        assertEquals(null, converter.convertToEntityAttribute(null));
    }

    @Test
    void emptyTest() {
        assertEquals(null, converter.convertToEntityAttribute(""));
    }

    @Test
    void convertToDatabaseColumnTest() {
        assertEquals("00:00-00:00", converter.convertToDatabaseColumn(Pair.of(0, 0)));
        assertEquals("00:01-00:01", converter.convertToDatabaseColumn(Pair.of(1, 1)));
        assertEquals("00:59-00:59", converter.convertToDatabaseColumn(Pair.of(59, 59)));
        assertEquals("01:00-01:00", converter.convertToDatabaseColumn(Pair.of(60, 60)));
        assertEquals("01:01-01:01", converter.convertToDatabaseColumn(Pair.of(61, 61)));
        assertEquals("01:59-01:59", converter.convertToDatabaseColumn(Pair.of(119, 119)));
        assertEquals("02:00-02:00", converter.convertToDatabaseColumn(Pair.of(120, 120)));
        assertEquals("02:01-02:01", converter.convertToDatabaseColumn(Pair.of(121, 121)));
        assertEquals("02:59-02:59", converter.convertToDatabaseColumn(Pair.of(179, 179)));
        assertEquals("03:00-03:00", converter.convertToDatabaseColumn(Pair.of(180, 180)));
        assertEquals("03:01-03:01", converter.convertToDatabaseColumn(Pair.of(181, 181)));
    }

    @Test
    void convertToEntityAttributeTest() {
        assertEquals(Pair.of(0, 0), converter.convertToEntityAttribute("00:00-00:00"));
        assertEquals(Pair.of(1, 1), converter.convertToEntityAttribute("00:01-00:01"));
        assertEquals(Pair.of(59, 59), converter.convertToEntityAttribute("00:59-00:59"));
        assertEquals(Pair.of(60, 60), converter.convertToEntityAttribute("01:00-01:00"));
        assertEquals(Pair.of(61, 61), converter.convertToEntityAttribute("01:01-01:01"));
        assertEquals(Pair.of(119, 119), converter.convertToEntityAttribute("01:59-01:59"));
        assertEquals(Pair.of(120, 120), converter.convertToEntityAttribute("02:00-02:00"));
        assertEquals(Pair.of(121, 121), converter.convertToEntityAttribute("02:01-02:01"));
        assertEquals(Pair.of(179, 179), converter.convertToEntityAttribute("02:59-02:59"));
        assertEquals(Pair.of(180, 180), converter.convertToEntityAttribute("03:00-03:00"));
        assertEquals(Pair.of(181, 181), converter.convertToEntityAttribute("03:01-03:01"));
    }

    @Test
    void invalidTimeFormatTest() {
        assertEquals(null, converter.convertToEntityAttribute("00:00-00:0"));
        assertEquals(null, converter.convertToEntityAttribute("00:00-00:"));
        assertEquals(null, converter.convertToEntityAttribute("00:00-00"));
        assertEquals(null, converter.convertToEntityAttribute("00:00-0"));
        assertEquals(null, converter.convertToEntityAttribute("00:00-"));
        assertEquals(null, converter.convertToEntityAttribute("00:00-"));
        assertEquals(null, converter.convertToEntityAttribute("00:00"));
        assertEquals(null, converter.convertToEntityAttribute("00:0"));
        assertEquals(null, converter.convertToEntityAttribute("00:"));
        assertEquals(null, converter.convertToEntityAttribute("00"));
        assertEquals(null, converter.convertToEntityAttribute("0"));
        assertEquals(null, converter.convertToEntityAttribute("00:00-00:00-00:00"));
        assertEquals(null, converter.convertToEntityAttribute("00:00-00:00-00:00-00:00"));
        assertEquals(null, converter.convertToEntityAttribute("00:00-00:00-00:00-00:00-00:00"));
        assertEquals(null, converter.convertToEntityAttribute("00:00-00:00-00:00-00:00-00:00-00:00"));
        assertEquals(null, converter.convertToEntityAttribute("00:00-00:00-00:00-00:00-00:00-00:00-00:00"));
        assertEquals(null, converter.convertToEntityAttribute("00:00-00:00-00:00-00:00-00:00-00:00-00:00-00:00"));
    }
}
