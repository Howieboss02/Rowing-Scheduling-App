package converters;

import nl.tudelft.cse.sem.template.shared.converters.PositionsToFillListConverter;
import nl.tudelft.cse.sem.template.shared.domain.Position;
import nl.tudelft.cse.sem.template.shared.enums.PositionName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class PositionsToFillConverterTest {

    private PositionsToFillListConverter converter = new PositionsToFillListConverter();

    @Test
    public void testConvertToDatabaseColumnNull() {
        assertEquals("", converter.convertToDatabaseColumn(null));
    }

    @Test
    public void testConvertToDatabaseColumnEmpty() {
        assertEquals("", converter.convertToDatabaseColumn(new ArrayList<>()));
    }

    @Test
    public void testConvertToDatabaseColumn() {
        List<Position> list = new ArrayList<>();
        list.add(new Position(PositionName.Coach, false));
        list.add(new Position(PositionName.Cox, false));
        converter.convertToDatabaseColumn(list);
        assertEquals("Coach,false;Cox,false;", converter.convertToDatabaseColumn(list));
    }

    @Test
    public void testConvertToEntityAttributeNull() {
        assertEquals(new ArrayList<>(), converter.convertToEntityAttribute(null));
    }

    @Test
    public void testConvertToEntityAttributeEmpty() {
        assertEquals(new ArrayList<>(), converter.convertToEntityAttribute(""));
    }

    @Test
    public void testConvertToEntityAttribute() {
        List<Position> list = new ArrayList<>();
        list.add(new Position(PositionName.Coach, false));
        list.add(new Position(PositionName.Cox, false));
        assertEquals(list, converter.convertToEntityAttribute("Coach,false;Cox,false;"));
    }


}
