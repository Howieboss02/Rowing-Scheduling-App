package nl.tudelft.sem.template.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import nl.tudelft.sem.template.controllers.UserController;
import nl.tudelft.sem.template.services.UserService;
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
import org.springframework.data.util.Pair;
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
        this.sut = new UserController(service);
    }

    @Test
    public void testGetAll() {
        sut.registerNewUser(getUser("a", Certificate.B5));
        assertEquals(sut.getUsers().size(), 1);
        sut.registerNewUser(getUser("B", Certificate.B2));
        assertEquals(sut.getUsers().size(), 2);
    }

    @Test
    public void testAddUser() {
        User u = getUser("A", Certificate.B1);
        sut.registerNewUser(u);
        assertEquals(sut.getUsers().size(), 1);
        assertEquals(sut.getUsers().get(0), u);
    }

    @Test
    public void testUpdate() {
        User u = getUser("A", Certificate.B1);
        sut.registerNewUser(u);
        sut.updateUser(1L, new UserModel("bbb", "Bobs", "MALE",  Certificate.B2, new ArrayList<>()));
        assertEquals(sut.getUsers().get(1).getName(), "bbb");
        assertEquals(sut.getUsers().get(0).getCertificate(), Certificate.B2);
    }

    @Test
    public void testDelete() {
        User u = getUser("A", Certificate.B1);
        sut.registerNewUser(u);
        User u1 = getUser("Bob", Certificate.B6);
        sut.registerNewUser(u1);
        sut.deleteUser(1L);
        assertEquals(sut.getUsers().size(), 1);
        assertEquals(sut.getUser(2L).getBody(), u1);
    }

    @Test
    public void testUpdateNonExistent() {
        User u = getUser("A", Certificate.B1);
        sut.registerNewUser(u);
        assertEquals(sut.updateUser(2L, new UserModel("bbb", "Bob", "MALE",
                Certificate.B1, new ArrayList<>())), ResponseEntity.badRequest().build());
        assertEquals(sut.getUsers().get(0).getName(), "A");
    }

    //TODO: fix the tests

    /*@Test
    public void testAddSchedule() {
        User u = getUser("A", Certificate.B1);
        sut.registerNewUser(u);
        //assertEquals(sut.addRecurringTimeSlot().getStatusCode(), HttpStatus.OK);
        assertEquals(u.getId(), 1L);
        assertEquals(sut.getUser(1L).getBody(), u);
        assertEquals(sut.getUser(1L).getBody().getSchedule().getRecurringSlots().get(0),
                new TimeSlot(-1, Day.MONDAY, new Node(10, 14)));
        assertEquals(sut.getUser(1L).getBody().getSchedule().getAddedSlots().size(), 0);
        assertEquals(sut.getUser(1L).getBody().getSchedule().getRemovedSlots().size(), 0);
    }*/

    @Test
    public void testRemoveSchedule() {
        User u = getUser("A", Certificate.B1);
        sut.registerNewUser(u);
        //assertEquals(sut.addRecurringTimeSlot(1L, Day.MONDAY, new Node(10, 14)).getStatusCode(), HttpStatus.OK);
        //assertEquals(sut.removeRecurringTimeSlot(1L, Day.MONDAY, new Node(10, 14)).getStatusCode(), HttpStatus.OK);
        assertEquals(sut.getUser(1L).getBody().getSchedule().getRecurringSlots().size(), 0);
    }

    @Test
    public void testFailedAddSchedule() {
        User u = getUser("A", Certificate.B1);
        sut.registerNewUser(u);
        //assertEquals(sut.addRecurringTimeSlot(2L, Day.MONDAY, new Node(10, 14)).getStatusCode(), HttpStatus.BAD_REQUEST);

    }

    @Test
    public void testAddTimeSlot() {
        TimeSlot time = new TimeSlot(1, Day.FRIDAY, new Node(10, 12));
        User u = getUser("A", Certificate.B1);
        sut.registerNewUser(u);
        assertEquals(sut.addTimeSlot(1L, time).getStatusCode(), HttpStatus.OK);
        assertTrue(sut.getUser(1L).getBody().getSchedule().getAddedSlots().contains(time));
    }

    /*
    @Test
    public void testRemoveTimeSlot() {
        TimeSlot time = new TimeSlot(1, Day.FRIDAY, Pair.of(10, 12));
        User u = getUser("A", Certificate.B1);
        sut.registerNewUser(u);
        sut.addTimeSlot(1L, time);
        assertTrue(sut.removeTimeSlot(1L, time).getStatusCode().equals(HttpStatus.OK));
        assertTrue(sut.getUser(1L).getBody().getSchedule().getRemovedSlots().contains(time));
    }
     */
}
