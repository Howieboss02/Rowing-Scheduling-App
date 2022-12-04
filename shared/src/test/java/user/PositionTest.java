package user;

import org.junit.jupiter.api.Test;
import nl.tudelft.cse.sem.template.shared.domain.Position;
import nl.tudelft.cse.sem.template.shared.enums.PositionName;

import static org.junit.jupiter.api.Assertions.*;


public class PositionTest {

  @Test
  public void testConstructor(){
    Position p = new Position(PositionName.Cox, false);
    assertNotNull(p);
    assertEquals(p.getName(), "Cox");
    assertFalse(p.isCompetitive());
  }

  @Test
  public void testChangeCompetitiveness(){
    Position p = new Position(PositionName.Cox, false);
    assertNotNull(p);
    p.setCompetitive(true);
    assertTrue(p.isCompetitive());
  }
}
