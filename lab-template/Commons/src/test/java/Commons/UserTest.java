package Commons;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class UserTest {

  @Test
  public void testConstructor(){
    User u = new User(1L, "Bob", "Bob@b.ob");
    assertEquals(u.getNotifications().size(), 0);
    assertEquals(u.getName(), "Bob");
    assertEquals(u.getEmail(), "Bob@b.ob");
  }

  @Test
  public void testConstructorEmpty(){
    User u = new User();
    assertNotNull(u);
  }

  @Test
  public void testConstructorFull(){
    List<Position> pos = new ArrayList<>();
    pos.add(new Position(PositionName.Cox, false));
    pos.add(new Position(PositionName.Coach, true));
    User u = new User(1L, "Bob", "Bob's Organization", "Bob@b.ob", "Male", Certificate.B1, pos);
    assertEquals(u.getOrganization(), "Bob's Organization");
    assertEquals(u.getCertificate(), "B1");
    assertEquals(u.getPositions(), pos);
    assertEquals(u.getPositions().get(0).getName(), "Coach");
    assertEquals(u.getGender(), "Male");
  }

  @Test
  public void tesEqualHashCode(){
    User u = new User(1L, "Bob", "Bob@b.ob");
    User u1 = new User(1L, "Bob", "Bob@b.ob");
    assertEquals(u, u1);
    assertEquals(u.hashCode(), u1.hashCode());
  }

  @Test
  public void testAddNotification(){
    User u = new User();
    u.addNotification("You have been REJECTED.");
    int size = u.getNotifications().size();
    assertEquals(u.getNotifications().get(size - 1).toString(), "You have been REJECTED.");
  }

}
