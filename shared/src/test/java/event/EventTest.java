package event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import nl.tudelft.sem.template.shared.domain.Position;
import nl.tudelft.sem.template.shared.entities.Event;
import nl.tudelft.sem.template.shared.enums.PositionName;
import org.junit.jupiter.api.Test;

public class EventTest {

    @Test
    public void testAddPosition() {
        Event e = new Event();
        e.addPosition(new Position(PositionName.Coach, false));
        assertNotNull(e.getPositions());
        Position p = e.getPositions().get(0);
        assertEquals(PositionName.Coach, p.getName());
    }

    @Test
    public void testRemovePosition() {
        Event e = new Event();
        e.addPosition(new Position(PositionName.Coach, false));
        e.addPosition(new Position(PositionName.Cox, false));
        e.addPosition(new Position(PositionName.PortSideRower, false));
        e.addPosition(new Position(PositionName.Startboard, false));
        e.addPosition(new Position(PositionName.ScullingRower, false));
        e.removePosition(new Position(PositionName.Coach, false));
        assertEquals(4, e.getPositions().size());
    }

}
