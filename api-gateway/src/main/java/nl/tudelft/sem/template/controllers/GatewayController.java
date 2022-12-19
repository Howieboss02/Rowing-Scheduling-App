package nl.tudelft.sem.template.controllers;


import nl.tudelft.sem.template.services.GatewayService;
import nl.tudelft.sem.template.shared.models.AuthenticationRequestModel;
import nl.tudelft.sem.template.shared.models.AuthenticationResponseModel;
import nl.tudelft.sem.template.shared.models.RegistrationRequestModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;


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
}
