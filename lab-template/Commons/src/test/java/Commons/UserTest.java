package Commons;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
    List<String> pos = new ArrayList<String>();
    pos.add("Cox");
    pos.add("Coach");
    User u = new User(1L, "Bob", "Bob's Organization", "Bob@b.ob", "Male", "B1", pos);
    assertEquals(u.getOrganization(), "Bob's Organization");
    assertEquals(u.getCertificate(), "B1");
    assertEquals(u.getPositions(), pos);
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
    assertEquals(u.getNotifications().toString(), "You have been REJECTED.");
  }

}
