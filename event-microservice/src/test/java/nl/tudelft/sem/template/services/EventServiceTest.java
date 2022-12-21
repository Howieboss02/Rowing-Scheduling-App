package nl.tudelft.sem.template.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.template.database.EventRepository;
import nl.tudelft.sem.template.database.TestEventRepository;
import nl.tudelft.sem.template.shared.domain.Request;
import nl.tudelft.sem.template.shared.domain.TimeSlot;
import nl.tudelft.sem.template.shared.entities.Event;
import nl.tudelft.sem.template.shared.entities.User;
import nl.tudelft.sem.template.shared.enums.Certificate;
import nl.tudelft.sem.template.shared.enums.Day;
import nl.tudelft.sem.template.shared.enums.EventType;
import nl.tudelft.sem.template.shared.enums.PositionName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.util.Pair;

class EventServiceTest {

    public EventRepository mockedRepo;
    private EventService mockedService;

    /**
     * Method to create a list of positions for testing.
     *
     * @return the list for positions needed for an event
     */
    private static List<PositionName> createPositions() {
        List<PositionName> list = new ArrayList<>();
        list.add(PositionName.Cox);
        list.add(PositionName.PortSideRower);
        list.add(PositionName.PortSideRower);
        return list;
    }

    /**
     * Method to create an event for testing.
     *
     * @param s a string to be used
     * @param l a long to be used
     * @param c a certificate needed
     * @param t the type of event
     * @return a new event
     */
    private static Event getEvent(String s, Long l, Certificate c, EventType t) {
        TimeSlot ts = new TimeSlot(1, Day.FRIDAY, Pair.of(2, 3));
        return new Event(l, s, createPositions(), ts, c, t, true, s);
    }

    @BeforeEach
    public void setup() {
        mockedRepo = mock(TestEventRepository.class);
        mockedService = new EventService(mockedRepo);
    }

    @Test
    void testGetAllEvents() {
        Event event = getEvent("A", 1L, Certificate.B2, EventType.COMPETITION);
        when(mockedRepo.findAll()).thenReturn(List.of(event));
        assertEquals(List.of(event), mockedService.getAllEvents());
    }

    @Test
    void testGetAllEventsByUser() {
        Event event = getEvent("A", 1L, Certificate.B2, EventType.COMPETITION);
        when(mockedRepo.findByOwningUser(1L)).thenReturn(List.of(event));

        assertEquals(List.of(event), mockedService.getAllEventsByUser(1L));
    }

    @Test
    void testInsertFail() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            mockedService.insert(null);
        });

        assertEquals("Event cannot be null", exception.getMessage());
    }

    @Test
    void testDeleteById() {
        try {
            when(mockedRepo.existsById(1L)).thenReturn(true);
            mockedService.deleteById(1L);
            verify(mockedRepo, times(1)).deleteById(1L);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    void testDeleteByIdFail() {
        when(mockedRepo.existsById(1L)).thenReturn(false);
        Exception exception = assertThrows(Exception.class, () -> {
            mockedService.deleteById(1L);
        });

        assertEquals("ID does not exist", exception.getMessage());
    }

    @Test
    void testGetById() {
        Event event = getEvent("A", 4L, Certificate.B2, EventType.COMPETITION);

        when(mockedRepo.existsById(1L)).thenReturn(true);
        when(mockedRepo.findById(1L)).thenReturn(Optional.of(event));
        assertEquals(Optional.of(event), mockedService.getById(1L));
    }

    @Test
    void testGetByIdFail() {
        when(mockedRepo.existsById(1L)).thenReturn(false);
        assertEquals(Optional.empty(), mockedService.getById(1L));
    }

    @Test
    void testUpdateById() {
        Event event = getEvent("A", 1L, Certificate.B2, EventType.COMPETITION);

        TimeSlot ts = new TimeSlot(1, Day.FRIDAY, Pair.of(2, 3));

        when(mockedRepo.existsById(1L)).thenReturn(true);
        when(mockedRepo.findById(1L)).thenReturn(Optional.of(event));

        assertEquals(Optional.of(event), mockedService.updateById(1L, 1L, "B", createPositions(), ts,
                Certificate.B5, EventType.COMPETITION, true, "B"));

        Event updated = getEvent("B", 1L, Certificate.B5, EventType.COMPETITION);
        assertEquals(updated, event);

    }

    @Test
    void testUpdateByIdNoChanges() {
        Event event = getEvent("A", 1L, Certificate.B2, EventType.COMPETITION);

        when(mockedRepo.existsById(1L)).thenReturn(true);
        when(mockedRepo.findById(1L)).thenReturn(Optional.of(event));

        assertEquals(Optional.of(event), mockedService.updateById(1L, 1L, null, null, null, null, null, false, null));
    }

    @Test
    void updateByIdNoEvent() {
        assertEquals(Optional.empty(), mockedService.updateById(null, null, null, null, null, null, null,  false, null));
    }

    @Test
    void testGetRequests() {
        Request r = new Request("Bob", PositionName.Cox);
        Event event = getEvent("A", 4L, Certificate.B2, EventType.COMPETITION);
        event.getQueue().add(r);

        when(mockedRepo.existsById(1L)).thenReturn(true);
        when(mockedRepo.findById(1L)).thenReturn(Optional.of(event));

        assertEquals(List.of(r), mockedService.getRequests(1L));
    }

    @Test
    void testGetRequestsFail() {
        when(mockedRepo.existsById(1L)).thenReturn(false);
        assertEquals(new ArrayList<Request>(), mockedService.getRequests(1L));
    }

    @Test
    void testEnqueueById() {
        Request r = new Request("Bob", PositionName.Cox);

        Event correctEvent = getEvent("A", 4L, Certificate.B2, EventType.COMPETITION);
        correctEvent.getQueue().add(r);

        User user = new User();
        user.setNetId("Bob");

        Event event = getEvent("A", 4L, Certificate.B2, EventType.COMPETITION);

        when(mockedRepo.existsById(1L)).thenReturn(true);
        when(mockedRepo.findById(1L)).thenReturn(Optional.of(event));

        mockedService.enqueueById(1L, user, PositionName.Cox);

        verify(mockedRepo, times(1)).save(event);
        assertEquals(List.of(r), event.getQueue());
    }

    @Test
    void testEnqueueByIdNoEvent() {
        User user = new User();
        user.setNetId("Bob");

        when(mockedRepo.existsById(1L)).thenReturn(false);

        mockedService.enqueueById(1L, user, PositionName.Cox);

        assertEquals(new ArrayList<>(), mockedService.getAllEvents());

    }

    @Test
    void testDequeueById() {
        Request r = new Request("Bob", PositionName.Cox);
        Event event = getEvent("A", 4L, Certificate.B2, EventType.COMPETITION);
        event.getQueue().add(r);
        User user = new User();
        user.setNetId("Bob");

        when(mockedRepo.existsById(1L)).thenReturn(true);
        when(mockedRepo.findById(1L)).thenReturn(Optional.of(event));

        assertTrue(mockedService.dequeueById(1L, new Request("Bob", PositionName.Cox)));
        verify(mockedRepo, times(1)).save(event);
        assertEquals(new ArrayList<>(), event.getQueue());
    }

    @Test
    void testDequeueByIdNoEvent() {
        when(mockedRepo.existsById(1L)).thenReturn(false);
        Request r = new Request("Bob", PositionName.Cox);
        assertFalse(mockedService.dequeueById(1L, r));
    }

    @Test
    void removePositionById() {
        Event event = getEvent("A", 4L, Certificate.B2, EventType.COMPETITION);

        List<PositionName> positions = createPositions();
        positions.remove(PositionName.Cox);

        when(mockedRepo.existsById(1L)).thenReturn(true);
        when(mockedRepo.findById(1L)).thenReturn(Optional.of(event));

        assertTrue(mockedService.removePositionById(1L, PositionName.Cox));
        verify(mockedRepo, times(1)).save(event);
        assertEquals(positions, event.getPositions());
    }

    @Test
    void removePositionByIdFail() {
        when(mockedRepo.existsById(1L)).thenReturn(false);

        assertFalse(mockedService.removePositionById(1L, PositionName.Cox));
    }

    @Test
    void getMatchedEvents() {
    }
}