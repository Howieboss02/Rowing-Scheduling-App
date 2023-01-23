package nl.tudelft.sem.template.database.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.template.database.UserRepository;
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
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;

@RestClientTest
public class UserServiceTest {

    public UserRepository mockedRepo;
    private UserService mockedService;

    /**
     * Method to create a list of positions for testing.
     *
     * @return the list for positions needed for an event
     */
    private static List<Position> createPositions() {
        List<Position> list = new ArrayList<>();
        list.add(new Position(PositionName.Cox, true));
        list.add(new Position(PositionName.PortSideRower, true));
        list.add(new Position(PositionName.Coach, false));
        return list;
    }

    /**
     * Method to create a user.
     *
     * @return the list for positions needed for an event
     */
    private static User getUser() {
        User user = new User("Bob", "Bob", "Bob's House", "Bob@b.ob", "M", Certificate.B2, createPositions());
        user.setId(1L);
        TimeSlot ts = new TimeSlot(1, Day.FRIDAY, new Node(1445, 1500));
        user.getSchedule().getRecurringSlots().add(ts);
        return user;
    }

    @BeforeEach
    public void setup() {
        mockedRepo = mock(UserRepository.class);
        mockedService = new UserService(mockedRepo);
    }

    @Test
    void testGetById() {
        when(mockedRepo.existsById(1L)).thenReturn(true);
        when(mockedRepo.findById(1L)).thenReturn(Optional.of(getUser()));
        assertEquals(getUser(), mockedService.getById(1L).get());
    }

    @Test
    void testGetByNonexistentId() {
        when(mockedRepo.existsById(1L)).thenReturn(false);
        assertEquals(Optional.empty(), mockedService.getById(1L));
    }

    @Test
    void testGetByIdZero() {
        when(mockedRepo.existsById(0L)).thenReturn(true);
        when(mockedRepo.findById(0L)).thenReturn(Optional.of(getUser()));
        assertEquals(getUser(), mockedService.getById(0L).get());
    }

    @Test
    void testGetByNegativeId() {
        assertEquals(Optional.empty(), mockedService.getById(-1L));
    }

    @Test
    void testGetByNet() {
        when(mockedRepo.findAll()).thenReturn(List.of(getUser()));
        assertEquals(getUser(), mockedService.getByNetId("Bob").get());
    }

    @Test
    void testGetByEmptyNet() {
        assertEquals(Optional.empty(), mockedService.getByNetId(""));
    }

    @Test
    void testGetByNetNotFound() {
        when(mockedRepo.findAll()).thenReturn(List.of(getUser()));
        assertEquals(Optional.empty(), mockedService.getByNetId("Alice"));
    }

    @Test
    void testInsertUser() {
        User user = getUser();
        when(mockedRepo.existsById(1L)).thenReturn(false);
        when(mockedRepo.save(user)).thenReturn(user);
        assertEquals(user, mockedService.insert(user));
        verify(mockedRepo, times(1)).save(user);
    }

    @Test
    void testInsertNull() {
        assertEquals(null, mockedService.insert(null));
    }

    @Test
    void testInsertExistent() {
        when(mockedRepo.existsById(1L)).thenReturn(true);
        assertEquals(null, mockedService.insert(getUser()));
    }

    @Test
    void testDeleteById() {
        User user = getUser();
        mockedRepo.save(user);
        when(mockedRepo.existsById(1L)).thenReturn(true);

        assertTrue(mockedService.deleteById(1L));
        verify(mockedRepo, times(1)).deleteById(1L);
        assertEquals(0, mockedService.getAllUsers().size());
        assertFalse(mockedService.getAllUsers().contains(user));
    }

    @Test
    void testDeleteNonexistentId() {
        when(mockedRepo.existsById(1L)).thenReturn(false);
        assertFalse(mockedService.deleteById(1L));
    }

    @Test
    void testDeleteByNegativeId() {
        assertFalse(mockedService.deleteById(-1L));
    }

    @Test
    void testDeleteByZeroId() {
        assertFalse(mockedService.deleteById(0L));
    }

    @Test
    void testUpdateAllComplete() {
        UserModel toUpdate = new UserModel("Alice", "Alice's team", "F", Certificate.B7,
            List.of(new Position(PositionName.Coach, false)));
        User user = getUser();

        when(mockedRepo.existsById(1L)).thenReturn(true);
        when(mockedService.getById(1L)).thenReturn(Optional.of(user));
        User updated = mockedService.updateById(1L, toUpdate).get();

        assertEquals(1L, updated.getId());
        assertEquals("Alice", updated.getUserInfo().getName());
        assertEquals("Bob", updated.getUserInfo().getNetId());
        assertEquals(List.of(new Position(PositionName.Coach, false)), updated.getPositions());

        verify(mockedRepo, times(1)).save(user);
    }

    @Test
    void testUpdateNonexistentId() {
        when(mockedRepo.existsById(1L)).thenReturn(false);

        UserModel toUpdate = new UserModel("Alice", "Alice's team", "F", Certificate.B7,
            List.of(new Position(PositionName.Coach, false)));
        assertEquals(Optional.empty(), mockedService.updateById(1L, toUpdate));
    }

    @Test
    void testUpdateName() {
        UserModel toUpdate = new UserModel("Alice", null, null, Certificate.B7,
            null);
        User user = getUser();
        when(mockedRepo.existsById(1L)).thenReturn(true);
        when(mockedService.getById(1L)).thenReturn(Optional.of(user));

        User updated = mockedService.updateById(1L, toUpdate).get();
        assertEquals("Alice", updated.getUserInfo().getName());
        assertEquals("Bob's House", updated.getUserInfo().getOrganization());
    }

    @Test
    void testUpdateOrganization() {
        UserModel toUpdate = new UserModel(null, "Alice's team", null, Certificate.B7,
            null);
        User user = getUser();
        when(mockedRepo.existsById(1L)).thenReturn(true);
        when(mockedService.getById(1L)).thenReturn(Optional.of(user));

        User updated = mockedService.updateById(1L, toUpdate).get();
        assertEquals("Bob", updated.getUserInfo().getName());
        assertEquals("Alice's team", updated.getUserInfo().getOrganization());
    }

    @Test
    void testUpdateGender() {
        UserModel toUpdate = new UserModel(null, null, "Female", Certificate.B7,
            null);
        User user = getUser();
        when(mockedRepo.existsById(1L)).thenReturn(true);
        when(mockedService.getById(1L)).thenReturn(Optional.of(user));

        User updated = mockedService.updateById(1L, toUpdate).get();
        assertEquals("Female", updated.getUserInfo().getGender());
        assertEquals("Bob", updated.getUserInfo().getName());
    }

    @Test
    void testAddNotification() {
        User user = getUser();
        when(mockedRepo.existsById(1L)).thenReturn(true);
        when(mockedService.getById(1L)).thenReturn(Optional.of(user));

        user.addNotification("Message");
        assertEquals(user, mockedService.addNotification(1L, "Message"));
        assertNotNull(mockedService.addNotification(1L, "Message"));
    }
}

