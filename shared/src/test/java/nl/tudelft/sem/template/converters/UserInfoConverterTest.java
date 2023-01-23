package nl.tudelft.sem.template.converters;

import static org.junit.jupiter.api.Assertions.*;

import nl.tudelft.sem.template.shared.converters.UserInfoConverter;
import nl.tudelft.sem.template.shared.domain.UserInfo;
import nl.tudelft.sem.template.shared.enums.Certificate;
import org.junit.jupiter.api.Test;

class UserInfoConverterTest {
    private final UserInfoConverter converter = new UserInfoConverter();

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
        assertEquals(new UserInfo("A", "B", "C", "D", "E", Certificate.B1),
                converter.convertToEntityAttribute("A,B,C,D,E,B1"));
    }

    @Test
    void convertToTimeslotEmptyString() {
        assertEquals(new UserInfo("", "", "", "", "", null),
                converter.convertToEntityAttribute(",,,,,"));
    }

    @Test
    void convertToStringCorrectly() {
        assertEquals("A,B,C,D,E,B1",
                converter.convertToDatabaseColumn(new UserInfo("A", "B", "C", "D", "E", Certificate.B1)));
    }

    @Test
    void convertToStringEmpty() {
        assertEquals(",,,,,B1",
                converter.convertToDatabaseColumn(new UserInfo("", "", "", "", "", Certificate.B1)));

    }
}