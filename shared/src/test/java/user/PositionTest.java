package user;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import shared.domain.Position;
import shared.enums.PositionName;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class PositionTest {

  @Test
  public void testConstructor(){
    Position p = new Position(PositionName.Cox, false);
    assertNotNull(p);
    assertEquals(p.getName().toString(), "Cox");
    assertEquals(p.isCompetitive(), false);
  }

  @Test
  public void testChangeCompetitiveness(){
    Position p = new Position(PositionName.Cox, false);
    assertNotNull(p);
    p.setCompetitive(true);
    assertEquals(p.isCompetitive(), true);
  }
}
