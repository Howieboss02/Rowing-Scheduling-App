package event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import nl.tudelft.sem.template.shared.entities.Event;
import nl.tudelft.sem.template.shared.enums.PositionName;
import org.junit.jupiter.api.Test;

public class EventTest {

    @Test
    public void testAddPosition() {
        Event e = new Event();
        e.addPosition(PositionName.Coach);
        assertNotNull(e.getPositions());
        PositionName p = e.getPositions().get(0);
        assertEquals(PositionName.Coach, p);
    }

    @Test
    public void testRemovePosition() {
        Event e = new Event();
        e.addPosition(PositionName.Coach);
        e.addPosition(PositionName.Cox);
        e.addPosition(PositionName.PortSideRower);
        e.addPosition(PositionName.Startboard);
        e.addPosition(PositionName.ScullingRower);
        e.removePosition(PositionName.Coach);
        assertEquals(4, e.getPositions().size());
    }

}
