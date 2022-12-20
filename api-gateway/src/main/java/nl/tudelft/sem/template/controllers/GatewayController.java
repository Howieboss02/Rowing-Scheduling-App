package nl.tudelft.sem.template.controllers;


import nl.tudelft.sem.template.services.GatewayService;
import nl.tudelft.sem.template.shared.domain.TimeSlot;
import nl.tudelft.sem.template.shared.models.AuthenticationRequestModel;
import nl.tudelft.sem.template.shared.models.AuthenticationResponseModel;
import nl.tudelft.sem.template.shared.models.RegistrationRequestModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.persistence.Convert;


@RestController
@RequestMapping(path = "/api")
public class GatewayController {

    @Autowired
    private transient GatewayService gatewayService;

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

    @GetMapping(path = "/user/all")
    public ResponseEntity getAllUsers() {
        System.out.println("get all users");
        return gatewayService.getAllUsers();
    }

    @GetMapping(path = "/user/getNotifications/{userId}")
    public ResponseEntity getAllNotifications(@PathVariable String userId) {
        System.out.println("get all notifications");
        return gatewayService.getAllNotifications(userId);
    }

    @PostMapping(path = "/user/addNotification/{userId}")
    public ResponseEntity addNotification(@PathVariable String userId, @RequestBody String notification) {
        System.out.println("add notification");
        return gatewayService.addNotification(userId, notification);
    }

    @PutMapping(path = "/user/schedule/addRecurring/{userId}")
    public ResponseEntity addRecurring(@PathVariable String userId, @RequestBody TimeSlot timeSlot) {
        System.out.println("add recurring");
        return gatewayService.addRecurring(userId, timeSlot);
    }

    @PutMapping(path = "/user/schedule/removeRecurring/{userId}")
    public ResponseEntity removeRecurring(@PathVariable String userId, @RequestBody TimeSlot timeSlot) {
        System.out.println("remove recurring");
        return gatewayService.removeRecurring(userId, timeSlot);
    }

    @PutMapping(path = "/user/schedule/include/{userId}")
    public ResponseEntity includeTimeSlot(@PathVariable String userId, @RequestBody TimeSlot timeSlot) {
        System.out.println("include time slot");
        return gatewayService.includeTimeSlot(userId, timeSlot);
    }
    @PutMapping(path = "/user/schedule/exclude/{userId}")
    public ResponseEntity removeTimeslot(@PathVariable String userId, @RequestBody TimeSlot timeslot) {
        System.out.println("remove timeslot");
        return gatewayService.removeTimeslot(userId, timeslot);
    }


}
