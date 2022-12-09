import nl.tudelft.sem.template.shared.enities.*;
import nl.tudelft.sem.template.shared.enums.*;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
public class EmailStrategyTest {
  public User user = new User(1L, "Bob", "Bob@b.ob");
  public Event event = new Event(1L, 1L, "Training 1", new ArrayList<>(), "12:00", "14:00", Certificate.B1, true, EventType.TRAINING);

  @Test
  public void testEmailAccepted() {
    Notification n = new Notification();
    n.setStrategy(new EmailStrategy());
    String message = n.sendNotification(user, event, Outcome.ACCEPTED);
    assertEquals(0, user.getNotifications().size());
    assertEquals("Email has been sent to Bob@b.ob with the message: \nBob, you have been accepted to the TRAINING from 12:00 until 14:00.\n", message);
  }

  @Test
  public void testEmailRejected() {
    Notification n = new Notification();
    n.setStrategy(new EmailStrategy());
    String message = n.sendNotification(user, event, Outcome.REJECTED);
    assertEquals(0, user.getNotifications().size());
    assertEquals("Email has been sent to Bob@b.ob with the message: \nBob, you have been rejected from the TRAINING from 12:00 until 14:00.\n", message);
  }
}