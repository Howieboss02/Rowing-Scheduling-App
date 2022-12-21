package nl.tudelft.sem.template.controllers;

import java.util.List;
import nl.tudelft.sem.template.services.GatewayService;
import nl.tudelft.sem.template.shared.converters.TextTimeToMinutesConverter;
import nl.tudelft.sem.template.shared.domain.Node;
import nl.tudelft.sem.template.shared.domain.Position;
import nl.tudelft.sem.template.shared.domain.TimeSlot;
import nl.tudelft.sem.template.shared.entities.User;
import nl.tudelft.sem.template.shared.enums.Certificate;
import nl.tudelft.sem.template.shared.enums.Day;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@RestController
@RequestMapping(path = "/api/user")
public class GatewayUserController {

    @Autowired
    private transient GatewayService gatewayService;
    private static final String uid = "userId";

    /**
     * Get all users.
     */
    @GetMapping(path = "/all")
    public ResponseEntity<List<User>> getAllUsers() {
        try {
            return ResponseEntity.ok(gatewayService.getAllUsers());
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * Get all notifications.
     */
    @GetMapping(path = "/getNotifications/{userId}")
    public ResponseEntity<List<String>> getAllNotifications(@PathVariable(uid) Long userId) {
        try {
            return ResponseEntity.ok(gatewayService.getAllNotifications(userId));
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * Delete a user.
     */
    @DeleteMapping(path = "/deleteUser/{userId}")
    public ResponseEntity<Boolean> deleteUser(@PathVariable(uid) Long userId) {
        try {
            gatewayService.deleteUser(userId);
            return ResponseEntity.ok(true);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    // TODO MAKE SURE IT WORKS

    /**
     * Update a user.
     */
    @PatchMapping(path = "/updateUser/{userId}")
    public ResponseEntity<User> updateUser(@PathVariable(uid) Long userId,
                                           @RequestParam(required = false) String name,
                                           @RequestParam(required = false) String organization,
                                           @RequestParam(required = false) String gender,
                                           @RequestParam(required = false) Certificate certificate,
                                           @RequestParam(required = false) List<Position> positions
    ) {
        try {
            User user = new User(name, organization, gender, certificate, positions);
            return ResponseEntity.ok(gatewayService.updateUser(userId, user));
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * Add a recurring time slot.
     */
    @PostMapping(path = "/schedule/addRecurring/{userId}")
    public ResponseEntity<TimeSlot> addRecurring(@PathVariable(uid) Long userId,
                                                 @RequestBody Day day,
                                                 @RequestBody Node time) {
        try {
            TimeSlot timeSlot = new TimeSlot(-1, day, time);
            return ResponseEntity.ok(gatewayService.addRecurring(userId, timeSlot));
        } catch (Exception e) {
            System.out.println("Unable to add the recurring time slot");
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Remove a recurring time slot.
     */
    @PostMapping(path = "/schedule/removeRecurring/{userId}")
    public ResponseEntity<TimeSlot> removeRecurring(@PathVariable(uid) Long userId,
                                                    @RequestBody Day day,
                                                    @RequestBody Node time) {
        try {
            TextTimeToMinutesConverter conv = new TextTimeToMinutesConverter();
            TimeSlot timeSlot = new TimeSlot(-1, day, time);
            return ResponseEntity.ok(gatewayService.removeRecurring(userId, timeSlot));
        } catch (Exception e) {
            System.out.println("Unable to remove the recurring time slot");
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Add a one time time slot.
     */
    @PostMapping(path = "/schedule/include/{userId}")
    public ResponseEntity<TimeSlot> includeTimeSlot(@PathVariable(uid) Long userId, @RequestBody TimeSlot timeSlot) {
        try {
            return ResponseEntity.ok(gatewayService.addOneTimeTimeSlot(userId, timeSlot));
        } catch (Exception e) {
            System.out.println("Unable to include the time slot");
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Remove a one time time slot.
     */
    @PostMapping(path = "/schedule/exclude/{userId}")
    public ResponseEntity<TimeSlot> excludeTimeslot(@PathVariable(uid) Long userId, @RequestBody TimeSlot timeslot) {
        try {
            return ResponseEntity.ok(gatewayService.removeOneTimeTimeSlot(userId, timeslot));
        } catch (Exception e) {
            System.out.println("Unable to exclude the time slot");
            return ResponseEntity.badRequest().build();
        }
    }
}
