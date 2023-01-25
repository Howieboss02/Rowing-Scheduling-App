package nl.tudelft.sem.template.converters;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import nl.tudelft.sem.template.shared.converters.RequestConverter;
import org.junit.jupiter.api.Test;

public class RequestConverterTest {

    private RequestConverter converter = new RequestConverter();

    @Test
    public void testEmpty() {
        assertEquals(converter.convertToDatabaseColumn(new ArrayList<>()), "");
    }

    @Test
    public void testEmptyString() {
        assertEquals(converter.convertToEntityAttribute(""), new ArrayList<>());
    }
}
