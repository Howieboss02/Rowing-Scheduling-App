import nl.tudelft.sem.template.shared.enities.Event;
import nl.tudelft.sem.template.shared.enities.User;
import nl.tudelft.sem.template.shared.enums.Outcome;

import static nl.tudelft.sem.template.shared.enums.Outcome.*;

public class PlatformStrategy implements Strategy{

  public PlatformStrategy(){
  }

  public String sendNotification(User user, Event event, Outcome outcome){
    String message = "";
    if(outcome == ACCEPTED)
      message =  user.getName() + ", you have been accepted to " + event.messageConverter();
    else if (outcome == REJECTED)
      message =  user.getName() + ", you have been rejected from " + event.messageConverter();
    user.addNotification(message);
    return message;
  }
}