import nl.tudelft.sem.template.shared.enities.*;
import nl.tudelft.sem.template.shared.enums.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlatformStrategyTest {

  public User user = new User(1L, "Bob", "Bob@b.ob");
  public Event event = new Event(1L, 1L, "Training 1", new ArrayList<>(),"12:00", "14:00", Certificate.B1, true, EventType.TRAINING);

  @Test
  public void testPlatformAccepted(){
    Notification n = new Notification();
    n.setStrategy(new PlatformStrategy());
    String message = n.sendNotification(user, event, Outcome.ACCEPTED);
    assertEquals(1, user.getNotifications().size());
    assertEquals("Bob, you have been accepted to the TRAINING from 12:00 until 14:00.\n", message);
  }
  @Test
  public void testPlatformRejected(){
    Notification n = new Notification();
    n.setStrategy(new PlatformStrategy());
    String message = n.sendNotification(user, event, Outcome.REJECTED);
    assertEquals(1, user.getNotifications().size());
    assertEquals("Bob, you have been rejected from the TRAINING from 12:00 until 14:00.\n", message);
  }
}