package nl.tudelft.sem.template.controllers;


import nl.tudelft.sem.template.services.GatewayService;
import nl.tudelft.sem.template.shared.domain.TimeSlot;
import nl.tudelft.sem.template.shared.entities.User;
import nl.tudelft.sem.template.shared.models.AuthenticationRequestModel;
import nl.tudelft.sem.template.shared.models.AuthenticationResponseModel;
import nl.tudelft.sem.template.shared.models.RegistrationRequestModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.persistence.Convert;
import java.util.List;


@RestController
@RequestMapping(path = "/api")
public class GatewayController {

    @Autowired
    private transient GatewayService gatewayService;

    // AUTHENTICATION MS

    @PostMapping(path = "/auth/register")
    public ResponseEntity register(@RequestBody RegistrationRequestModel request) {
        System.out.println("register");
        return gatewayService.registerUser(request);
    }

    @PostMapping(path = "/auth/login")
    public ResponseEntity<AuthenticationResponseModel> login(@RequestBody AuthenticationRequestModel request) {
        System.out.println("login");
        return ResponseEntity.ok(gatewayService.login(request));
    }



    // USER MS

    @GetMapping(path = "/user/all")
    public ResponseEntity<List<User>> getAllUsers() {
        System.out.println("get all users");
        try {
            return ResponseEntity.ok((List<User>) gatewayService.getAllUsers());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping(path = "/user/getNotifications/{userId}")
    public ResponseEntity<List<String>> getAllNotifications(@PathVariable String userId) {
        System.out.println("get all notifications");
        try {
            return ResponseEntity.ok((List<String>) gatewayService.getAllNotifications(userId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping(path = "/user/setName/{userId}")
    public ResponseEntity setName(@PathVariable String userId, @RequestBody String name) {
        System.out.println("set name");
        try {
            gatewayService.setName(userId, name);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping(path = "/user/addNotification/{userId}")
    public ResponseEntity addNotification(@PathVariable String userId, @RequestBody String notification) {
        System.out.println("add notification");
        try {
            gatewayService.addNotification(userId, notification);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping(path = "/user/schedule/addRecurring/{userId}")
    public ResponseEntity addRecurring(@PathVariable String userId, @RequestBody TimeSlot timeSlot) {
        System.out.println("add recurring");
        try {
            gatewayService.addRecurring(userId, timeSlot);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping(path = "/user/schedule/removeRecurring/{userId}")
    public ResponseEntity removeRecurring(@PathVariable String userId, @RequestBody TimeSlot timeSlot) {
        System.out.println("remove recurring");
        try {
            gatewayService.removeRecurring(userId, timeSlot);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping(path = "/user/schedule/include/{userId}")
    public ResponseEntity includeTimeSlot(@PathVariable String userId, @RequestBody TimeSlot timeSlot) {
        System.out.println("include time slot");
        try {
            gatewayService.includeTimeSlot(userId, timeSlot);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    @PutMapping(path = "/user/schedule/exclude/{userId}")
    public ResponseEntity removeTimeslot(@PathVariable String userId, @RequestBody TimeSlot timeslot) {
        System.out.println("remove timeslot");
        try {
            gatewayService.removeTimeslot(userId, timeslot);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // EVENT MS


}
