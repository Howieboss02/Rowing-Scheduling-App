package nl.tudelft.sem.template.controllers;

import java.sql.Time;
import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.template.services.UserService;
import nl.tudelft.sem.template.shared.domain.Node;
import nl.tudelft.sem.template.shared.domain.Position;
import nl.tudelft.sem.template.shared.domain.TimeSlot;
import nl.tudelft.sem.template.shared.entities.Event;
import nl.tudelft.sem.template.shared.entities.User;
import nl.tudelft.sem.template.shared.entities.UserModel;
import nl.tudelft.sem.template.shared.enums.Certificate;
import nl.tudelft.sem.template.shared.enums.Day;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "api/user")
public class UserController {

    private static final String uid = "userId";
    private final transient UserService userService;


    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Get all users.
     *
     * @return list of all users
     */
    @GetMapping("/all")
    public List<User> getUsers() {
        return userService.getAllUsers();
    }

    /**
     * GET API for getting a specific user.
     *
     * @param id the id of the user we are looking for
     * @return the respective user
     */
    @GetMapping("/{userId}")
    public ResponseEntity<User> getUser(@PathVariable("userId") Long id) {
        Optional<User> user = userService.getById(id);
        if (user.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(user.get());
    }

    /**
     * API GET request to retrieve user ny the unique netId.
     *
     * @param name the netId of the user
     * @return information about the user
     */
    @GetMapping("/name")
    public ResponseEntity<User> getUserByNetId(@RequestBody String name) {
        Optional<User> user = userService.getByNetId(name);
        if (user.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(user.get());
    }

    /**
     * GET Http request to retrieve all notifications a user has received.
     *
     * @param id the id of the user
     * @return a list of notifications
     */
    @GetMapping(path = "getNotifications/{userId}")
    public ResponseEntity<List<String>> getNotifications(@PathVariable(uid) Long id) {
        if (userService.getNotifications(id).isEmpty()) {
            return ResponseEntity.badRequest().build();
        } else {
            return ResponseEntity.ok(userService.getNotifications(id).get());
        }
    }

    /**
     * POST Http request to register a new user to the platform.
     *
     * @param user the profile data
     * @return confirmation of registering the user
     */
    @PostMapping(path = "/register")
    public ResponseEntity<User> registerNewUser(@RequestBody User user) {
        User insertedUser = userService.insert(user);
        if (insertedUser == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(insertedUser);
    }

    /**
     * DELETE Http request to delete a user.
     *
     * @param userId the id of the user we want to delete
     * @return a confirmation of deleting it
     */
    @DeleteMapping(path = "/delete/{userId}")
    public ResponseEntity<Boolean> deleteUser(@PathVariable(uid) Long userId) {
        try {
            userService.deleteById(userId);
            return ResponseEntity.ok(true);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Update everything about a user at once by giving all possible parameters.
     */
    @PutMapping(path = "/update/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable(uid) Long userId,
                                        @RequestParam UserModel userModel) {
        Optional<User> returned = userService.updateById(
                userId,
                userModel.getName(),
                userModel.getOrganization(),
                userModel.getGender(),
                userModel.getCertificate(),
                userModel.getPositions());
        if (returned.isPresent()) {
            return ResponseEntity.ok(returned.get());
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Update the user's name.
     */
    @PutMapping(path = "/name/{userId}")
    public ResponseEntity<?> setName(@PathVariable(uid) Long userId,
                                     @RequestParam(required = false) String name
    ) {
        if (userService.setName(userId, name).isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    /**
     * Update the user's organization.
     */
    @PutMapping(path = "/organization/{userId}")
    public ResponseEntity<?> setOrganization(@PathVariable(uid) Long userId,
                                             @RequestParam(required = false) String organization
    ) {
        if (userService.setOrganization(userId, organization).isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }


    /**
     * Update the user's gender.
     */
    @PutMapping(path = "/gender/{userId}")
    public ResponseEntity<?> setGender(@PathVariable(uid) Long userId,
                                       @RequestParam(required = false) String gender
    ) {
        if (userService.setGender(userId, gender).isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    /**
     * Update the user's certificate.
     */
    @PutMapping(path = "/certificate/{userId}")
    public ResponseEntity<?> setCertificate(@PathVariable(uid) Long userId,
                                            @RequestParam(required = false) Certificate certificate
    ) {
        if (userService.setCertificate(userId, certificate).isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    /**
     * Update the user's positions.
     */
    @PutMapping(path = "/positions/{userId}")
    public ResponseEntity<?> setPositions(@PathVariable(uid) Long userId,
                                          @RequestParam(required = false) List<Position> positions
    ) {
        if (userService.setPositions(userId, positions).isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    /**
     * Add a notification to the user's list of notifications.
     */
    @PutMapping(path = "/notification/{userId}")
    public ResponseEntity<?> addNotification(@PathVariable(uid) Long userId,
                                             @RequestParam(required = false) String notification
    ) {
        if (userService.addNotification(userId, notification).isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    /**
     * Add a recurring timeslot (day of the week + time) to the user's schedule.
     * Add in the API call indicates that the timeslot is recurring.
     * include in the API call indicates that the timeslot is one time.
     */
    @PostMapping(path = "/schedule/add/{userId}")
    public ResponseEntity<TimeSlot> addRecurringTimeSlot(@PathVariable(uid) Long userId,
                                                         @RequestBody TimeSlot timeSlot
    ) {
        if (userService.addRecurringTimeSlot(userId, timeSlot).isEmpty()) {
            System.out.println("Failed to add recurring timeslot");
            return ResponseEntity.badRequest().build();
        }
        System.out.println("Added recurring timeslot");
        return ResponseEntity.ok(timeSlot);
    }

    /**
     * Remove a recurring timeslot (day of the week + time) from the user's schedule.
     */
    @PostMapping(path = "/schedule/remove/{userId}")
    public ResponseEntity<TimeSlot> removeRecurringTimeSlot(@PathVariable(uid) Long userId,
                                                            @RequestBody TimeSlot timeSlot
    ) {
        if (userService.removeRecurringTimeSlot(userId, timeSlot).isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(timeSlot);
    }

    /**
     * Include a one-time-only timeslot in the user schedule.
     */
    @PostMapping(path = "/schedule/include/{userId}")
    public ResponseEntity<TimeSlot> addTimeSlot(@PathVariable(uid) Long userId,
                                                @RequestBody TimeSlot timeslot
    ) {
        if (userService.addTimeSlot(userId, timeslot).isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(timeslot);
    }

    /**
     * Exclude one instance of a timeslot from the user's schedule.
     */
    @PostMapping(path = "/schedule/exclude/{userId}")
    public ResponseEntity<TimeSlot> removeTimeSlot(@PathVariable(uid) Long userId,
                                                   @RequestBody TimeSlot timeslot
    ) {
        if (userService.removeTimeSlot(userId, timeslot).isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(timeslot);
    }


}
