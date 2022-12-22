import nl.tudelft.sem.template.Notification;
import nl.tudelft.sem.template.PlatformStrategy;
import nl.tudelft.sem.template.shared.domain.Node;
import nl.tudelft.sem.template.shared.domain.TimeSlot;
import nl.tudelft.sem.template.shared.entities.*;
import nl.tudelft.sem.template.shared.enums.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlatformStrategyTest {

  public User user = new User("BobID", "Bob", "Bob@b.ob");
  public Event event = new Event(1L, "Training 1", new ArrayList<>(),new TimeSlot(1, Day.MONDAY, new Node(12, 14)), Certificate.B1, EventType.TRAINING, true, "M",  "Bob's camp");

  @Test
  public void testPlatformAccepted(){
    Notification n = new Notification();
    n.setStrategy(new PlatformStrategy());
    String message = n.sendNotification(user, event, Outcome.ACCEPTED);
    assertEquals(1, user.getNotifications().size());
    assertEquals("Bob, you have been accepted to Training 1 - TRAINING from 12 until 14 in week 1, on MONDAY.\n", message);
  }
  @Test
  public void testPlatformRejected(){
    Notification n = new Notification();
    n.setStrategy(new PlatformStrategy());
    String message = n.sendNotification(user, event, Outcome.REJECTED);
    assertEquals(1, user.getNotifications().size());
    assertEquals("Bob, you have been rejected from Training 1 - TRAINING from 12 until 14 in week 1, on MONDAY.\n", message);
  }
}