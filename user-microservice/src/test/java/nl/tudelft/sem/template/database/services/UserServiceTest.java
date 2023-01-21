package nl.tudelft.sem.template.database.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import nl.tudelft.sem.template.database.UserRepository;
import nl.tudelft.sem.template.services.UserService;
import nl.tudelft.sem.template.shared.domain.Node;
import nl.tudelft.sem.template.shared.domain.Position;
import nl.tudelft.sem.template.shared.domain.TimeSlot;
import nl.tudelft.sem.template.shared.entities.User;
import nl.tudelft.sem.template.shared.enums.Certificate;
import nl.tudelft.sem.template.shared.enums.Day;
import nl.tudelft.sem.template.shared.enums.PositionName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        when(mockedRepo.findById(1L)).thenReturn(java.util.Optional.of(getUser()));
        assertEquals(getUser(), mockedService.getById(1L).get());
    }

    @Test
    void testGetByNonexistentId() {
        when(mockedRepo.existsById(1L)).thenReturn(false);
        assertEquals(Optional.empty(), mockedService.getById(1L));
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
    void testInsertUser(){
        User user = getUser();
        when(mockedRepo.existsById(1L)).thenReturn(false);
        assertEquals(user, mockedService.insert(user));
        verify(mockedRepo, times(1)).save(user);
    }
}
