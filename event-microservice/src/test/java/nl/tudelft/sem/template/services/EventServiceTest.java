package nl.tudelft.sem.template.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.template.database.EventRepository;
import nl.tudelft.sem.template.shared.domain.*;
import nl.tudelft.sem.template.shared.entities.Event;
import nl.tudelft.sem.template.shared.entities.User;
import nl.tudelft.sem.template.shared.enums.Certificate;
import nl.tudelft.sem.template.shared.enums.Day;
import nl.tudelft.sem.template.shared.enums.EventType;
import nl.tudelft.sem.template.shared.enums.PositionName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class EventServiceTest {
    @Mock
    public EventRepository mockedRepo;
    @Mock
    private EventService mockedService;

    /**
     * Method to create a list of position names for testing.
     *
     * @return the list for positions needed for an event
     */
    private static List<PositionName> createPositionNames() {
        List<PositionName> list = new ArrayList<>();
        list.add(PositionName.Cox);
        list.add(PositionName.PortSideRower);
        list.add(PositionName.PortSideRower);
        return list;
    }

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
        User user = new User();
        user.setId(1L);
        user.setNetId("Bob");
        user.setGender("M");
        user.setPositions(createPositions());
        user.setCertificate(Certificate.B2);
        user.setSchedule(new Schedule());
        TimeSlot ts = new TimeSlot(1, Day.FRIDAY, new Node(1445, 1500));
        user.getSchedule().getRecurringSlots().add(ts);
        return user;
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
        TimeSlot ts = new TimeSlot(1, Day.FRIDAY, new Node(1455, 1500));
        return new Event(l, s, createPositionNames(), ts, c, t, true, s, s);
    }

    @BeforeEach
    public void setup() {
        mockedRepo = mock(EventRepository.class);
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

        TimeSlot ts = new TimeSlot(1, Day.FRIDAY, new Node(1455, 1500));

        when(mockedRepo.existsById(1L)).thenReturn(true);
        when(mockedRepo.findById(1L)).thenReturn(Optional.of(event));

        assertEquals(Optional.of(event), mockedService.updateById(1L, 1L, "B", createPositionNames(), ts,
            Certificate.B5, EventType.COMPETITION, true, "M", "B", false));

        Event updated = getEvent("B", 1L, Certificate.B5, EventType.COMPETITION);
        updated.setGender("M");
        assertEquals(updated, event);

    }

    @Test
    void testUpdateByIdNoChanges() {
        Event event = getEvent("A", 1L, Certificate.B2, EventType.COMPETITION);

        when(mockedRepo.existsById(1L)).thenReturn(true);
        when(mockedRepo.findById(1L)).thenReturn(Optional.of(event));

        assertEquals(Optional.of(event),
            mockedService.updateById(1L, 1L, null, null, null, null, null, false, null, null, false));
    }

    @Test
    void updateByIdNoEvent() {
        assertEquals(Optional.empty(),
            mockedService.updateById(null, null, null, null, null, null, null, false, null, null, false));
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
        List<Request> request = mockedService.getRequests(1L);
        assertEquals(ArrayList.class, request.getClass());
    }

    @Test
    void testEnqueueByIdTraining() {
        Request r = new Request("Bob", PositionName.Cox);

        Event correctEvent = getEvent("A", 4L, Certificate.B2, EventType.TRAINING);
        correctEvent.getQueue().add(r);

        User user = getUser();

        Event event = getEvent("A", 4L, Certificate.B2, EventType.TRAINING);

        when(mockedRepo.existsById(1L)).thenReturn(true);
        when(mockedRepo.findById(1L)).thenReturn(Optional.of(event));

        assertTrue(mockedService.enqueueById(1L, user, PositionName.Cox,
            200 + 1440L * ((Day.FRIDAY.ordinal() + 1) % 7)));
        verify(mockedRepo, times(1)).save(event);
        assertEquals(List.of(r), event.getQueue());

    }

    @Test
    void testEnqueueTrainingBoundary() {
        Event event = getEvent("A", 4L, Certificate.B2, EventType.TRAINING);

        when(mockedRepo.existsById(1L)).thenReturn(true);
        when(mockedRepo.findById(1L)).thenReturn(Optional.of(event));

        User user = getUser();
        assertTrue(mockedService.enqueueById(1L, user, PositionName.Cox,
            1455L - 30L + 1440L * ((Day.FRIDAY.ordinal() + 1) % 7)));
        verify(mockedRepo, times(1)).save(event);
        Request r = new Request("Bob", PositionName.Cox);
        assertEquals(List.of(r), event.getQueue());
    }

    @Test
    void testEnqueueEarlyTraining() {
        Event event = getEvent("A", 4L, Certificate.B2, EventType.TRAINING);

        when(mockedRepo.existsById(1L)).thenReturn(true);
        when(mockedRepo.findById(1L)).thenReturn(Optional.of(event));

        User user = getUser();
        mockedService.enqueueById(1L, user, PositionName.Cox,
            1430L + 1440L * ((Day.FRIDAY.ordinal() + 1) % 7));

        verify(mockedRepo, times(0)).save(event);
        assertEquals(new ArrayList<>(), event.getQueue());
    }

    @Test
    void testEnqueueEarlyCompetition() {
        Event event = getEvent("A", 4L, Certificate.B2, EventType.COMPETITION);
        Request r = new Request("Bob", PositionName.Cox);
        when(mockedRepo.existsById(1L)).thenReturn(true);
        when(mockedRepo.findById(1L)).thenReturn(Optional.of(event));

        User user = new User();
        user.setNetId("Bob");
        mockedService.enqueueById(1L, user, PositionName.Cox,
            100 + 1440L * ((Day.FRIDAY.ordinal() + 1) % 7));

        verify(mockedRepo, times(0)).save(event);
        assertEquals(new ArrayList<>(), event.getQueue());
    }

    @Test
    void testEnqueueByIdNoEvent() {
        User user = getUser();

        when(mockedRepo.existsById(1L)).thenReturn(false);

        mockedService.enqueueById(1L, user, PositionName.Cox, 1430);

        assertFalse(mockedService.enqueueById(1L, user, PositionName.Cox,
            1450 + 1440L * ((Day.FRIDAY.ordinal() + 1) % 7)));
        assertEquals(new ArrayList<>(), mockedService.getAllEvents());
    }

    @Test
    void testEnqueueByIdNotCompetitive() {
        Event event = getEvent("A", 4L, Certificate.B2, EventType.COMPETITION);
        event.setCompetitive(true);

        when(mockedRepo.existsById(1L)).thenReturn(true);
        when(mockedRepo.findById(1L)).thenReturn(Optional.of(event));

        User user = getUser();
        assertFalse(mockedService.enqueueById(1L, user, PositionName.Coach,
            100 + 1440L * ((Day.FRIDAY.ordinal() + 1) % 7)));
    }

    @Test
    void testEnqueueByIdOrganizationNoMatch() {
        User user = getUser();

        Event event = getEvent("B", 4L, Certificate.B2, EventType.COMPETITION);

        when(mockedRepo.existsById(1L)).thenReturn(true);
        when(mockedRepo.findById(1L)).thenReturn(Optional.of(event));

        assertFalse(mockedService.enqueueById(1L, user, PositionName.Cox,
            100 + 1440L * ((Day.FRIDAY.ordinal() + 1) % 7)));
    }

    @Test
    void testEnqueueByIdGenderNoMatch() {
        User user = getUser();
        user.setOrganization("B");

        Event event = getEvent("B", 4L, Certificate.B2, EventType.COMPETITION);

        when(mockedRepo.existsById(1L)).thenReturn(true);
        when(mockedRepo.findById(1L)).thenReturn(Optional.of(event));

        assertFalse(mockedService.enqueueById(1L, user, PositionName.Cox,
            100 + 1440L * ((Day.FRIDAY.ordinal() + 1) % 7)));
    }

    @Test
    void testEnqueueByIdCertificateNoMatch() {
        User user = getUser();
        user.setCertificate(Certificate.B1);

        Event event = getEvent("A", 4L, Certificate.B2, EventType.TRAINING);

        when(mockedRepo.existsById(1L)).thenReturn(true);
        when(mockedRepo.findById(1L)).thenReturn(Optional.of(event));

        assertFalse(mockedService.enqueueById(1L, user, PositionName.Cox,
            100 + 1440L * ((Day.FRIDAY.ordinal() + 1) % 7)));
    }

    @Test
    void testEnqueueByIdCreator() {
        User user = getUser();

        Event event = getEvent("A", 1L, Certificate.B2, EventType.TRAINING);

        when(mockedRepo.existsById(1L)).thenReturn(true);
        when(mockedRepo.findById(1L)).thenReturn(Optional.of(event));

        assertFalse(mockedService.enqueueById(1L, user, PositionName.Cox,
            100 + 1440L * ((Day.FRIDAY.ordinal() + 1) % 7)));
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

        List<PositionName> positions = createPositionNames();
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
    void testGetMatchedEventsTrainings() {
        User user = getUser();
        List<Event> trainings = List.of(getEvent("A", 4L, Certificate.B2, EventType.TRAINING));

        when(mockedRepo.findMatchingCompetitions(
            user.getCertificate(), user.getOrganization(), user.getId(),
            EventType.COMPETITION, user.getGender()))
            .thenReturn(new ArrayList<>());

        when(mockedRepo.findMatchingTrainings(
            user.getCertificate(), user.getId(), EventType.TRAINING))
            .thenReturn(trainings);
        assertEquals(trainings, mockedService.getMatchedEvents(user));
    }

    @Test
    void testGetMatchedEventsCompetitions() {
        User user = getUser();
        List<Event> competitions = List.of(getEvent("A", 4L, Certificate.B2, EventType.COMPETITION));

        when(mockedRepo.findMatchingCompetitions(
            user.getCertificate(), user.getOrganization(), user.getId(),
            EventType.COMPETITION, user.getGender()))
            .thenReturn(competitions);

        when(mockedRepo.findMatchingTrainings(
            user.getCertificate(), user.getId(), EventType.TRAINING))
            .thenReturn(new ArrayList<>());
        assertEquals(competitions, mockedService.getMatchedEvents(user));
    }

    @Test
    void testAddEvent() {
        Event event = getEvent("A", 4L, Certificate.B2, EventType.COMPETITION);
        when(mockedRepo.save(event)).thenReturn(event);
        Event added = mockedService.insert(event);
        assertEquals(event, added);
        assertNotEquals(null, added);

    }

    @Test
    void testUpdateUnauthorizedUser() {
        Event event = getEvent("A", 4L, Certificate.B2, EventType.COMPETITION);
        when(mockedRepo.existsById(1L)).thenReturn(true);
        when(mockedService.getById(1L)).thenReturn(Optional.of(event));
        Optional<Event> updated = mockedService.updateById(1L, 1L, "New event", null, null,
            null, null, false, null, null, false);
        assertTrue(updated.isEmpty());
    }

    @Test
    void testUpdateWithNewTimeSlot() {
        Event event = getEvent("A", 4L, Certificate.B2, EventType.COMPETITION);
        TimeSlot ts = new TimeSlot(12, Day.FRIDAY, new Node(1400, 1600));
        when(mockedRepo.existsById(1L)).thenReturn(true);
        when(mockedService.getById(1L)).thenReturn(Optional.of(event));
        Optional<Event> updated = mockedService.updateById(4L, 1L, "New event", createPositionNames(), ts,
            null, null, true, null, null, false);
        assertTrue(updated.isPresent());
        assertEquals(ts, updated.get().getTimeslot());
    }

    @Test
    void testUpdateCertificate() {
        Event event = getEvent("A", 4L, Certificate.B2, EventType.COMPETITION);
        when(mockedRepo.existsById(1L)).thenReturn(true);
        when(mockedService.getById(1L)).thenReturn(Optional.of(event));
        Optional<Event> updated = mockedService.updateById(4L, 1L, "New event", createPositionNames(),
            null, Certificate.B7, null, true, null, null, false);
        assertTrue(updated.isPresent());
        assertEquals(Certificate.B7, updated.get().getCertificate());
    }

    @Test
    void testUpdateType() {
        Event event = getEvent("A", 4L, Certificate.B2, EventType.COMPETITION);
        when(mockedRepo.existsById(1L)).thenReturn(true);
        when(mockedService.getById(1L)).thenReturn(Optional.of(event));
        Optional<Event> updated = mockedService.updateById(4L, 1L, "New event", null, null,
            null, EventType.TRAINING, true, null, null, false);
        assertTrue(updated.isPresent());
        assertEquals(EventType.TRAINING, updated.get().getType());
    }

    @Test
    void testUpdateCompetitiveness() {
        Event event = getEvent("A", 4L, Certificate.B2, EventType.COMPETITION);
        when(mockedRepo.existsById(1L)).thenReturn(true);
        when(mockedService.getById(1L)).thenReturn(Optional.of(event));
        Optional<Event> updated = mockedService.updateById(4L, 1L, "New event", createPositionNames(),
            null, null, null, false, null, null, true);
        assertTrue(updated.isPresent());
        assertEquals(false, updated.get().isCompetitive());
    }

    @Test
    void testUpdateGender() {
        Event event = getEvent("A", 4L, Certificate.B2, EventType.COMPETITION);
        when(mockedRepo.existsById(1L)).thenReturn(true);
        when(mockedService.getById(1L)).thenReturn(Optional.of(event));
        Optional<Event> updated = mockedService.updateById(4L, 1L, null, createPositionNames(),
            null, null, null, false, "Male", null, false);
        assertTrue(updated.isPresent());
        assertEquals("Male", updated.get().getGender());
    }

    @Test
    void testUpdateOrganisation() {
        Event event = getEvent("A", 4L, Certificate.B2, EventType.COMPETITION);
        when(mockedRepo.existsById(1L)).thenReturn(true);
        when(mockedService.getById(1L)).thenReturn(Optional.of(event));
        Optional<Event> updated = mockedService.updateById(4L, 1L, null, createPositionNames(),
            null, null, null, false, null, "Bob's Land", true);
        assertTrue(updated.isPresent());
        assertEquals("Bob's Land", updated.get().getOrganisation());
    }

    @Test
    void testUpdatePositions() {
        Event event = getEvent("A", 4L, Certificate.B2, EventType.COMPETITION);
        when(mockedRepo.existsById(1L)).thenReturn(true);
        when(mockedService.getById(1L)).thenReturn(Optional.of(event));
        Optional<Event> updated = mockedService.updateById(4L, 1L, null, List.of(PositionName.ScullingRower),
            null, null, null, false, null, null, true);
        assertTrue(updated.isPresent());
        assertEquals(List.of(PositionName.ScullingRower), updated.get().getPositions());
    }

    //TODO: Add test on boundary for line 181

    @Test
    void testEnqueueTwice() {
        User user = getUser();
        Event event = getEvent("A", 4L, Certificate.B2, EventType.TRAINING);

        when(mockedRepo.existsById(1L)).thenReturn(true);
        when(mockedRepo.findById(1L)).thenReturn(Optional.of(event));

        assertTrue(mockedService.enqueueById(1L, user, PositionName.Cox,
            200 + 1440L * ((Day.FRIDAY.ordinal() + 1) % 7)));
        verify(mockedRepo, times(1)).save(event);
        Request r = new Request("Bob", PositionName.Cox);
        assertEquals(List.of(r), event.getQueue());

        assertTrue(mockedService.enqueueById(1L, user, PositionName.Cox,
            200 + 1440L * ((Day.FRIDAY.ordinal() + 1) % 7)));
        assertEquals(2, event.getQueue().size());
    }

    @Test
    void testEnqueueNotSpecifiedPosition() {
        Event event = getEvent("A", 4L, Certificate.B2, EventType.COMPETITION);
        event.setCompetitive(true);

        when(mockedRepo.existsById(1L)).thenReturn(true);
        when(mockedRepo.findById(1L)).thenReturn(Optional.of(event));

        User user = getUser();
        assertFalse(mockedService.enqueueById(1L, user, PositionName.ScullingRower,
            200 + 1440L * ((Day.FRIDAY.ordinal() + 1) % 7)));
        verify(mockedRepo, times(0)).save(event);

    }

    @Test
    void testEnqueueCompetition() {
        Event event = getEvent("A", 4L, Certificate.B2, EventType.COMPETITION);
        event.setCompetitive(true);
        event.setGender("M");
        when(mockedRepo.existsById(1L)).thenReturn(true);
        when(mockedRepo.findById(1L)).thenReturn(Optional.of(event));

        User user = getUser();
        user.setOrganization("A");
        assertTrue(mockedService.enqueueById(1L, user, PositionName.Cox,
            1440L * ((Day.TUESDAY.ordinal() + 1) % 7)));
        Request r = new Request("Bob", PositionName.Cox);

        verify(mockedRepo, times(1)).save(event);
        assertEquals(List.of(r), event.getQueue());
    }

    @Test
    void testDequeueException() {
        User user = getUser();
        Event event = getEvent("A", 4L, Certificate.B2, EventType.COMPETITION);

        when(mockedRepo.existsById(1L)).thenReturn(true);
        when(mockedService.getById(1L)).thenReturn(Optional.of(event));
        boolean status = mockedService.dequeueById(1L, new Request("A", PositionName.ScullingRower));
        assertFalse(status);
    }

    @Test
    void testMatchUnwantedPositions() {
        User user = getUser();
        List<Position> l = new ArrayList<>();
        l.add(new Position(PositionName.Coach, false));
        user.setPositions(l);
        Event event = getEvent("A", 4L, Certificate.B2, EventType.COMPETITION);

        when(mockedRepo.findMatchingTrainings(user.getCertificate(), user.getId(), EventType.TRAINING))
            .thenReturn(List.of(event));
        when(mockedRepo.findMatchingCompetitions(user.getCertificate(), user.getOrganization(), user.getId(),
            EventType.COMPETITION, user.getGender())).thenReturn(List.of(event));

        assertEquals(new ArrayList<>(), mockedService.getMatchedEvents(user));
    }

    @Test
    void testEnqueueCompetitionDiffGenderSameOrg() {
        User user = getUser();
        user.setOrganization("A");
        user.setGender("Female");
        Event event = getEvent("A", 4L, Certificate.B2, EventType.COMPETITION);
        event.setCompetitive(true);

        when(mockedRepo.existsById(1L)).thenReturn(true);
        when(mockedRepo.findById(1L)).thenReturn(Optional.of(event));
        assertFalse(mockedService.enqueueById(1L, user, PositionName.Cox,
            1440L * ((Day.TUESDAY.ordinal() + 1) % 7)));
        assertEquals(0, event.getQueue().size());
    }
}