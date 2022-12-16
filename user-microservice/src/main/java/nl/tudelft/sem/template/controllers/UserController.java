package nl.tudelft.sem.template.controllers;

import java.util.List;
import nl.tudelft.sem.template.services.UserService;
import nl.tudelft.sem.template.shared.domain.Position;
import nl.tudelft.sem.template.shared.entities.User;
import nl.tudelft.sem.template.shared.enums.Certificate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "api/user")
public class UserController {

    private final transient UserService userService;


    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getUsers() {
        return userService.getAllUsers();
    }

    /**
     * GET Http request to retrieve all notifications a user has received.
     *
     * @param id the id of the user
     * @return a list of notifications
     */
    @GetMapping(path = "getNotifications/{ userId }")
    public ResponseEntity<List<String>> getNotifications(@PathVariable("userId") Long id) {
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
    @PostMapping
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
    @DeleteMapping(path = "{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable("userId") Long userId) {
        if (!userService.deleteById(userId)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    /**
     * Update everything about a user at once by giving all possible parameters.
     */
    @PutMapping(path = "{userId}")
    public ResponseEntity<?> updateUser(@PathVariable("userId") Long userId,
                                        @RequestParam(required = false) String name,
                                        @RequestParam(required = false) String organization,
                                        @RequestParam(required = false) String email,
                                        @RequestParam(required = false) String gender,
                                        @RequestParam(required = false) Certificate certificate,
                                        @RequestParam(required = false) List<Position> positions
    ) {
        if (userService.updateById(userId, name, organization, email, gender, certificate, positions).isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok().build();
    }
}
