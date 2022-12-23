package nl.tudelft.sem.template.controllers;

import java.util.List;
import nl.tudelft.sem.template.services.GatewayService;
import nl.tudelft.sem.template.shared.converters.TextTimeToMinutesConverter;
import nl.tudelft.sem.template.shared.domain.Node;
import nl.tudelft.sem.template.shared.domain.Position;
import nl.tudelft.sem.template.shared.domain.TimeSlot;
import nl.tudelft.sem.template.shared.entities.User;
import nl.tudelft.sem.template.shared.entities.UserModel;
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

    /**
     * Update a user.
     */
    @PutMapping(path = "/updateUser/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable(uid) Long userId,
                                        @RequestBody UserModel userModel) {
        try {
            return ResponseEntity.ok(gatewayService.updateUser(userId, userModel));
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
                                                 @RequestBody TimeSlot timeSlot) {
        try {
            return ResponseEntity.ok(gatewayService.addRecurring(userId, timeSlot));
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * Remove a recurring time slot.
     */
    @PostMapping(path = "/schedule/removeRecurring/{userId}")
    public ResponseEntity<TimeSlot> removeRecurring(@PathVariable(uid) Long userId,
                                                    @RequestBody TimeSlot timeSlot) {
        try {
            return ResponseEntity.ok(gatewayService.removeRecurring(userId, timeSlot));
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * Add a one time slot.
     */
    @PostMapping(path = "/schedule/include/{userId}")
    public ResponseEntity<TimeSlot> includeTimeSlot(@PathVariable(uid) Long userId,
                                                    @RequestBody TimeSlot timeSlot) {
        try {
            return ResponseEntity.ok(gatewayService.addOneTimeTimeSlot(userId, timeSlot));
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * Remove a one time time slot.
     */
    @PostMapping(path = "/schedule/exclude/{userId}")
    public ResponseEntity<TimeSlot> excludeTimeslot(@PathVariable(uid) Long userId, @RequestBody TimeSlot timeslot) {
        try {
            return ResponseEntity.ok(gatewayService.removeOneTimeTimeSlot(userId, timeslot));
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
