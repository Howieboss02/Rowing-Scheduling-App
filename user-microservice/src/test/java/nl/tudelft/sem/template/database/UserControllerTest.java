package nl.tudelft.sem.template.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import nl.tudelft.sem.template.controllers.UserController;
import nl.tudelft.sem.template.services.UserService;
import nl.tudelft.sem.template.services.UserTimeSlotService;
import nl.tudelft.sem.template.shared.domain.Node;
import nl.tudelft.sem.template.shared.domain.Position;
import nl.tudelft.sem.template.shared.domain.TimeSlot;
import nl.tudelft.sem.template.shared.entities.User;
import nl.tudelft.sem.template.shared.entities.UserModel;
import nl.tudelft.sem.template.shared.enums.Certificate;
import nl.tudelft.sem.template.shared.enums.Day;
import nl.tudelft.sem.template.shared.enums.PositionName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


public class UserControllerTest {

    public TestUserRepository repo;
    public UserService service;
    private UserController sut;

    /**
     * Helper method to create list of positions.
     *
     * @return a list of hardcoded positions
     */
    private static List<Position> getPositions() {
        List<Position> list = new ArrayList<>();
        list.add(new Position(PositionName.PortSideRower, true));
        list.add(new Position(PositionName.Cox, false));
        list.add(new Position(PositionName.PortSideRower, false));
        return list;
    }

    /**
     * Helper method to create a user.
     *
     * @param s a random string
     * @param c a certificate type
     * @return a hardcoded user
     */
    private static User getUser(String s, Certificate c) {
        return new User(s, s, s, s, "M", c, getPositions());
    }

    /**
     * SetUp method to create teh connection between the database and the queries.
     */
    @BeforeEach
    public void setup() {
        this.repo = new TestUserRepository();
        this.service = new UserService(repo);
        UserTimeSlotService timeService = new UserTimeSlotService(repo, service);
        this.sut = new UserController(service, timeService);
    }

    @Test
    public void testGetAll() {
        sut.registerNewUser(getUser("a", Certificate.B5));
        assertEquals(1, sut.getUsers().size());
        sut.registerNewUser(getUser("B", Certificate.B2));
        assertEquals(2, sut.getUsers().size());
    }

    @Test
    public void testAddUser() {
        User u = getUser("A", Certificate.B1);
        sut.registerNewUser(u);
        assertEquals(1, sut.getUsers().size());
        assertEquals(u, sut.getUsers().get(0));
    }

    @Test
    public void testUpdate() {
        User u = getUser("A", Certificate.B1);
        sut.registerNewUser(u);
        sut.updateUser(1L, new UserModel("bbb", "Bobs", "MALE",  Certificate.B2, new ArrayList<>()));
        assertEquals("bbb", sut.getUsers().get(1).getUserInfo().getName());
        assertEquals(Certificate.B2, sut.getUsers().get(0).getUserInfo().getCertificate());
    }

    @Test
    public void testDelete() {
        User u = getUser("A", Certificate.B1);
        sut.registerNewUser(u);
        User u1 = getUser("Bob", Certificate.B6);
        sut.registerNewUser(u1);
        sut.deleteUser(1L);
        assertEquals(1, sut.getUsers().size());
        assertEquals(u1, sut.getUser(2L).getBody());
    }

    @Test
    public void testUpdateNonExistent() {
        User u = getUser("A", Certificate.B1);
        sut.registerNewUser(u);
        assertEquals(ResponseEntity.badRequest().build(), sut.updateUser(2L,
                new UserModel("bbb", "Bob", "MALE", Certificate.B1, new ArrayList<>())));
        assertEquals(sut.getUsers().get(0).getUserInfo().getName(), "A");
    }

    @Test
    public void testRemoveSchedule() {
        User u = getUser("A", Certificate.B1);
        sut.registerNewUser(u);
        assertEquals(sut.addRecurringTimeSlot(1L, new TimeSlot(1, Day.MONDAY, new Node(10, 14))).getStatusCode(),
            HttpStatus.OK);
        assertEquals(sut.removeRecurringTimeSlot(1L, new TimeSlot(1, Day.MONDAY, new Node(10, 14))).getStatusCode(),
            HttpStatus.OK);
        assertEquals(sut.getUser(1L).getBody().getSchedule().getRecurringSlots().size(), 0);
    }


    @Test
    public void testFailedAddSchedule() {
        User u = getUser("A", Certificate.B1);
        sut.registerNewUser(u);
        assertEquals(sut.addRecurringTimeSlot(2L, new TimeSlot(1, Day.MONDAY, new Node(10, 9))).getStatusCode(),
            HttpStatus.BAD_REQUEST);

    }

    @Test
    public void testAddTimeSlot() {
        TimeSlot time = new TimeSlot(1, Day.FRIDAY, new Node(10, 12));
        User u = getUser("A", Certificate.B1);
        sut.registerNewUser(u);
        assertEquals(HttpStatus.OK, sut.addTimeSlot(1L, time).getStatusCode());
        assertTrue(sut.getUser(1L).getBody().getSchedule().getAddedSlots().contains(time));
    }

    @Test
    public void testRemoveTimeSlot() {
        final TimeSlot removedTime = new TimeSlot(-1, Day.FRIDAY, new Node(11, 13));
        final TimeSlot correctTime = new TimeSlot(-1, Day.FRIDAY, new Node(11, 12));

        User u = getUser("A", Certificate.B1);

        sut.registerNewUser(u);
        sut.addRecurringTimeSlot(1L, correctTime);
        assertEquals(HttpStatus.OK, sut.removeTimeSlot(1L, removedTime).getStatusCode());
        assertTrue(Objects.requireNonNull(sut.getUser(1L).getBody()).getSchedule().getRemovedSlots().contains(correctTime));
    }

    @Test
    public void testAddRecurringTimeSlot() {
        TimeSlot time = new TimeSlot(-1, Day.FRIDAY, new Node(10, 12));

        User u = getUser("A", Certificate.B1);
        sut.registerNewUser(u);
        sut.addRecurringTimeSlot(1L, time);
        assertTrue(Objects.requireNonNull(sut.getUser(1L).getBody()).getSchedule().getRecurringSlots().contains(time));
    }

    @Test
    public void testGetNotifications() {
        User u = getUser("A", Certificate.B1);
        u.addNotification("a");
        u.addNotification("b");
        sut.registerNewUser(u);

        List<String> notifications = new ArrayList<>(Arrays.asList("a", "b"));
        assertEquals(notifications, sut.getNotifications(1L).getBody());
    }

    @Test
    public void testGetNotificationsFail() {
        assertEquals(HttpStatus.BAD_REQUEST, sut.getNotifications(1L).getStatusCode());
    }

    @Test
    public void testAddNotifications() {
        List<String> notifications = new ArrayList<>(List.of("A"));

        User u = getUser("A", Certificate.B1);
        sut.registerNewUser(u);
        assertEquals(HttpStatus.OK, sut.addNotification(1L, "A").getStatusCode());
        assertEquals(notifications, sut.getUser(1L).getBody().getNotifications());
    }

    @Test
    public void testAddNotificationsFail() {
        assertEquals(HttpStatus.BAD_REQUEST, sut.addNotification(2L, "A").getStatusCode());
    }

    @Test
    public void testGetUserByNetId() {
        User u = getUser("A", Certificate.B1);
        sut.registerNewUser(u);
        assertEquals(HttpStatus.OK, sut.getUserByNetId("A").getStatusCode());
        assertEquals(u, sut.getUserByNetId("A").getBody());
    }

    @Test
    public void testGetUserByNetIdNotFound() {
        assertEquals(HttpStatus.NOT_FOUND, sut.getUserByNetId("A").getStatusCode());
    }
}
