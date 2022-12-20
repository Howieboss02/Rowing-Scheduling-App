package nl.tudelft.sem.template.controllers;

import nl.tudelft.sem.template.services.GatewayService;
import nl.tudelft.sem.template.shared.domain.Position;
import nl.tudelft.sem.template.shared.domain.TimeSlot;
import nl.tudelft.sem.template.shared.entities.User;
import nl.tudelft.sem.template.shared.enums.Certificate;
import nl.tudelft.sem.template.shared.models.AuthenticationRequestModel;
import nl.tudelft.sem.template.shared.models.AuthenticationResponseModel;
import nl.tudelft.sem.template.shared.models.RegistrationRequestModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/user")
public class GatewayUserController {

    @Autowired
    private transient GatewayService gatewayService;

    /*@GetMapping(path = "/all")
    public ResponseEntity<List<User>> getAllUsers() {
        try {
            return ResponseEntity.ok((List<User>) gatewayService.getAllUsers());
        } catch (Exception e) {
            System.out.println("Unable to get all users");
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping(path = "/getNotifications/{userId}")
    public ResponseEntity<List<String>> getAllNotifications(@PathVariable Long userId) {
        try {
            return ResponseEntity.ok((List<String>) gatewayService.getAllNotifications(userId));
        } catch (Exception e) {
            System.out.println("Unable to get all notifications");
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping(path = "/deleteUser/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        try {
            gatewayService.deleteUser(userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.out.println("Unable to delete user");
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping(path = "/updateUser/{userId}")
    public ResponseEntity<String> updateUser(@PathVariable Long userId,
                                             @RequestParam(required = false) String name,
                                             @RequestParam(required = false) String organization,
                                             @RequestParam(required = false) String gender,
                                             @RequestParam(required = false) Certificate certificate,
                                             @RequestParam(required = false) List<Position> positions
                                             ) {
        try {
            User user = new User(name, organization, gender, certificate, positions);
            gatewayService.updateUser(userId, user);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.out.println("Unable to update user");
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping(path = "/addNotification/{userId}")
    public ResponseEntity<String> addNotification(@PathVariable Long userId, @RequestBody String notification) {
        try {
            gatewayService.addNotification(userId, notification);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.out.println("Unable to add the notification");
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping(path = "/user/schedule/addRecurring/{userId}")
    public ResponseEntity<String> addRecurring(@PathVariable Long userId, @RequestBody TimeSlot timeSlot) {
        try {
            gatewayService.addRecurring(userId, timeSlot);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.out.println("Unable to add the recurring time slot");
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping(path = "/user/schedule/removeRecurring/{userId}")
    public ResponseEntity<String> removeRecurring(@PathVariable Long userId, @RequestBody TimeSlot timeSlot) {
        try {
            gatewayService.removeRecurring(userId, timeSlot);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.out.println("Unable to remove the recurring time slot");
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping(path = "/user/schedule/include/{userId}")
    public ResponseEntity<String> includeTimeSlot(@PathVariable Long userId, @RequestBody TimeSlot timeSlot) {
        try {
            gatewayService.includeTimeSlot(userId, timeSlot);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.out.println("Unable to include the time slot");
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping(path = "/user/schedule/exclude/{userId}")
    public ResponseEntity<String> excludeTimeslot(@PathVariable Long userId, @RequestBody TimeSlot timeslot) {
        try {
            gatewayService.removeTimeslot(userId, timeslot);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.out.println("Unable to exclude the time slot");
            return ResponseEntity.badRequest().build();
        }
    }
*/
}
