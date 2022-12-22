package nl.tudelft.sem.template;

import nl.tudelft.sem.template.shared.entities.Event;
import nl.tudelft.sem.template.shared.entities.User;
import nl.tudelft.sem.template.shared.enums.Outcome;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import static nl.tudelft.sem.template.shared.enums.Outcome.*;

public class PlatformStrategy implements Strategy {

  public PlatformStrategy(){
  }

  public String sendNotification(User user, Event event, Outcome outcome){
    String message = "";
    if(outcome == ACCEPTED)
      message =  user.getName() + ", you have been accepted to " + event.messageConverter();
    else if (outcome == REJECTED)
      message =  user.getName() + ", you have been rejected from " + event.messageConverter();
    WebClient client = WebClient.create();
    client.put().uri("http://localhost:8084/api/user/notification/" + user.getId()).body(BodyInserters.fromValue(message));
    user.addNotification(message);
    return message;
  }
}