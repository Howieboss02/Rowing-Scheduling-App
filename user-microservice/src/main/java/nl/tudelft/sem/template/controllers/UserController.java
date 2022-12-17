package nl.tudelft.sem.template.controllers;

import nl.tudelft.sem.template.services.UserService;
import nl.tudelft.sem.template.shared.domain.TimeSlot;
import nl.tudelft.sem.template.shared.enums.Day;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import nl.tudelft.sem.template.shared.domain.Position;
import nl.tudelft.sem.template.shared.entities.User;
import nl.tudelft.sem.template.shared.enums.Certificate;
import org.springframework.data.util.Pair;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping(path = "api/user")
public class UserController {

    private final UserService userService;


    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/all")
    public List<User> getUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUser(@PathVariable("userId") Long id){
        Optional<User> user = userService.getById(id);
        if(user.isEmpty()) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return ResponseEntity.ok(user.get());
    }

    @GetMapping(path = "/getNotifications/{userId}")
    public ResponseEntity<List<String>> getNotifications(@PathVariable("userId") Long id) {
        if (userService.getNotifications(id).isEmpty()){
            return ResponseEntity.badRequest().build();
        } else {
            return ResponseEntity.ok(userService.getNotifications(id).get());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<User> registerNewUser(@RequestBody User user) {
        User insertedUser = userService.insert(user);
        if (insertedUser == null){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(insertedUser);
    }

    @DeleteMapping(path = "/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable("userId") Long userId) {
        if (!userService.deleteById(userId)){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    /**
     * Update everything about a user at once by giving all possible parameters
     */
    @PutMapping(path = "/user/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable("userId") Long userId ,
                                        @RequestParam(required = false) String name,
                                        @RequestParam(required = false) String organization,
                                        @RequestParam(required = false) String email,
                                        @RequestParam(required = false) String gender,
                                        @RequestParam(required = false) Certificate certificate,
                                        @RequestParam(required = false) List<Position> positions
    ) {
        if (userService.updateById(userId, name, organization, email, gender, certificate, positions).isEmpty()){
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok().build();
    }

    /**
     * Update the user's name
     */
    @PutMapping(path = "/name/{userId}")
    public ResponseEntity<?> setName(@PathVariable("userId") Long userId ,
                                      @RequestParam(required = false) String name
    ) {
        if (userService.setName(userId, name).isEmpty()){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    /**
     * Update the user's organization
     */
    @PutMapping(path = "/organization/{userId}")
    public ResponseEntity<?> setOrganization(@PathVariable("userId") Long userId ,
                                        @RequestParam(required = false) String organization
    ) {
        if (userService.setOrganization(userId, organization).isEmpty()){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }


    /**
     * Update the user's gender
     */
    @PutMapping(path = "/gender/{userId}")
    public ResponseEntity<?> setGender(@PathVariable("userId") Long userId ,
                                      @RequestParam(required = false) String gender
    ) {
        if (userService.setGender(userId, gender).isEmpty()){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    /**
     * Update the user's certificate
     */
    @PutMapping(path = "/certificate/{userId}")
    public ResponseEntity<?> setCertificate(@PathVariable("userId") Long userId ,
                                       @RequestParam(required = false) Certificate certificate
    ) {
        if (userService.setCertificate(userId, certificate).isEmpty()){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    /**
     * Update the user's positions
     */
    @PutMapping(path = "/positions/{userId}")
    public ResponseEntity<?> setPositions(@PathVariable("userId") Long userId ,
                                 @RequestParam(required = false) List<Position> positions
    ) {
        if (userService.setPositions(userId, positions).isEmpty()){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    /**
     * Add a notification to the user's list of notifications
     */
    @PutMapping(path = "/notification/{userId}")
    public ResponseEntity<?> addNotification(@PathVariable("userId") Long userId ,
                                          @RequestParam(required = false) String notification
    ) {
        if (userService.addNotification(userId, notification).isEmpty()){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    /**
     * Add a recurring timeslot (day of the week + time) to the user's schedule
     */
    @PutMapping(path = "/schedule/add/{userId}")
    public ResponseEntity<?> addRecurringTimeSlot(@PathVariable("userId") Long userId ,
                                                  @RequestParam(required = false) Day day,
                                                  @RequestParam(required = false) Pair<Integer, Integer> time
    ) {
        if (userService.addRecurringTimeSlot(userId, day, time).isEmpty()){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    /**
     * Remove a recurring timeslot (day of the week + time) from the user's schedule
     */
    @PutMapping(path = "/schedule/remove/{userId}")
    public ResponseEntity<?> removeRecurringTimeSlot(@PathVariable("userId") Long userId ,
                                                  @RequestParam(required = false) Day day,
                                                  @RequestParam(required = false) Pair<Integer, Integer> time
    ) {
        if (userService.removeRecurringTimeSlot(userId, day, time).isEmpty()){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    /**
     * Include a one-time-only timeslot in the user schedule
     */
    @PutMapping(path = "/schedule/include/{userId}")
    public ResponseEntity<?> addTimeSlot(@PathVariable("userId") Long userId ,
                                                  @RequestParam(required = false) TimeSlot timeslot
    ) {
        if (userService.addTimeSlot(userId, timeslot).isEmpty()){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    /**
     *  Exclude one instance of a timeslot from the user's schedule
     */
    @PutMapping(path = "/schedule/exclude/{userId}")
    public ResponseEntity<?> removeTimeSlot(@PathVariable("userId") Long userId ,
                                         @RequestParam(required = false) TimeSlot timeslot
    ) {
        if (userService.removeTimeSlot(userId, timeslot).isEmpty()){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }


}
