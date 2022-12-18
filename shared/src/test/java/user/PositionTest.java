package user;

import static org.junit.jupiter.api.Assertions.*;

import nl.tudelft.sem.template.shared.domain.Position;
import nl.tudelft.sem.template.shared.enums.PositionName;
import org.junit.jupiter.api.Test;

public class PositionTest {

    @Test
    public void testConstructor() {
        Position p = new Position(PositionName.Cox, false);
        assertNotNull(p);
        assertEquals(PositionName.Cox, p.getName());
        assertFalse(p.isCompetitive());
    }

    @Test
    public void testChangeCompetitiveness() {
        Position p = new Position(PositionName.Cox, false);
        assertNotNull(p);
        p.setCompetitive(true);
        assertTrue(p.isCompetitive());
    }
}
