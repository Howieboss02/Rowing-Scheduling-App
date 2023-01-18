package nl.tudelft.sem.template.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import nl.tudelft.sem.template.database.EventRepository;
import nl.tudelft.sem.template.shared.components.RestTemplateResponseErrorHandler;
import nl.tudelft.sem.template.shared.domain.*;
import nl.tudelft.sem.template.shared.entities.Event;
import nl.tudelft.sem.template.shared.entities.EventModel;
import nl.tudelft.sem.template.shared.entities.User;
import nl.tudelft.sem.template.shared.enums.*;
import nl.tudelft.sem.template.shared.utils.JsonUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

@RestClientTest
class EventServiceTest {

    public EventRepository mockedRepo;
    private EventService mockedService;
    private RestTemplate restTemplate;
    @Autowired
    private RestTemplateBuilder builder;
    @Autowired
    private MockRestServiceServer server;

    private static final String apiPrefix = "http://localhost:";

    private static final String userPath = "/api/user";


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
        User user = new User("Bob", null, null, null, "M", Certificate.B2, createPositions());
        user.setId(1L);
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
        this.restTemplate = this.builder
                .errorHandler(new RestTemplateResponseErrorHandler())
                .build();
        server = MockRestServiceServer.createServer(restTemplate);
        this.mockedService = new EventService(mockedRepo, restTemplate);
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

        EventModel eventModel = new EventModel(
                1L,
                "B",
                createPositionNames(),
                ts, Certificate.B5,
                EventType.COMPETITION,
                true,
                "M",
                "B");

        assertEquals(Optional.of(event), mockedService.updateById(1L, eventModel, true));

        Event updated = getEvent("B", 1L, Certificate.B5, EventType.COMPETITION);
        updated.setGender("M");
        assertEquals(updated, event);

    }

    @Test
    void testUpdateByIdNoChanges() {
        Event event = getEvent("A", 1L, Certificate.B2, EventType.COMPETITION);
        EventModel eventModel = new EventModel();
        eventModel.setOwningUser(1L);

        when(mockedRepo.existsById(1L)).thenReturn(true);
        when(mockedRepo.findById(1L)).thenReturn(Optional.of(event));


        assertEquals(Optional.of(event),
                mockedService.updateById(1L, eventModel, false));
    }

    @Test
    void updateByIdNoEvent() {
        assertEquals(Optional.empty(),
                mockedService.updateById(null, new EventModel(), false));
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
    void testEnqueueByIdTraining() throws JsonProcessingException {
        Request r = new Request("Bob", PositionName.Cox);

        Event correctEvent = getEvent("A", 4L, Certificate.B2, EventType.TRAINING);
        correctEvent.getQueue().add(r);

        User user = getUser();

        Event event = getEvent("A", 4L, Certificate.B2, EventType.TRAINING);

        server.expect(requestTo(apiPrefix + MicroservicePorts.USER.port + userPath + "/1"))
                .andRespond(withSuccess(JsonUtil.serialize(user), MediaType.APPLICATION_JSON));

        when(mockedRepo.existsById(1L)).thenReturn(true);
        when(mockedRepo.findById(1L)).thenReturn(Optional.of(event));

        assertTrue(mockedService.enqueueById(1L, user.getId(), PositionName.Cox,
                200 + 1440L * ((Day.FRIDAY.ordinal() + 1) % 7)));

        verify(mockedRepo, times(1)).save(event);
        assertEquals(List.of(r), event.getQueue());

    }

    @Test
    void testEnqueueEarlyTraining() throws JsonProcessingException {
        Event event = getEvent("A", 4L, Certificate.B2, EventType.TRAINING);

        when(mockedRepo.existsById(1L)).thenReturn(true);
        when(mockedRepo.findById(1L)).thenReturn(Optional.of(event));

        User user = getUser();
        server.expect(requestTo(apiPrefix + MicroservicePorts.USER.port + userPath + "/1"))
                .andRespond(withSuccess(JsonUtil.serialize(user), MediaType.APPLICATION_JSON));
        mockedService.enqueueById(1L, user.getId(), PositionName.Cox,
                1430L + 1440L * ((Day.FRIDAY.ordinal() + 1) % 7));

        verify(mockedRepo, times(0)).save(event);
        assertEquals(new ArrayList<>(), event.getQueue());
    }

    @Test
    void testEnqueueEarlyCompetition() throws JsonProcessingException {
        Event event = getEvent("A", 4L, Certificate.B2, EventType.COMPETITION);
        when(mockedRepo.existsById(1L)).thenReturn(true);
        when(mockedRepo.findById(1L)).thenReturn(Optional.of(event));

        User user = new User();
        user.setId(1L);
        user.getUserInfo().setNetId("Bob");

        server.expect(requestTo(apiPrefix + MicroservicePorts.USER.port + userPath + "/1"))
                .andRespond(withSuccess(JsonUtil.serialize(user), MediaType.APPLICATION_JSON));

        mockedService.enqueueById(1L, user.getId(), PositionName.Cox,
                100 + 1440L * ((Day.FRIDAY.ordinal() + 1) % 7));

        verify(mockedRepo, times(0)).save(event);
        assertEquals(new ArrayList<>(), event.getQueue());
    }

    @Test
    void testEnqueueByIdNoEvent() throws JsonProcessingException {
        User user = getUser();

        server.expect(requestTo(apiPrefix + MicroservicePorts.USER.port + userPath + "/1"))
                .andRespond(withSuccess(JsonUtil.serialize(user), MediaType.APPLICATION_JSON));
        when(mockedRepo.existsById(1L)).thenReturn(false);

        assertFalse(mockedService.enqueueById(1L, user.getId(), PositionName.Cox,
                1450 + 1440L * ((Day.FRIDAY.ordinal() + 1) % 7)));
        assertEquals(new ArrayList<>(), mockedService.getAllEvents());
    }

    @Test
    void testEnqueueByIdNotCompetitive() throws JsonProcessingException {
        Event event = getEvent("A", 4L, Certificate.B2, EventType.COMPETITION);
        event.setCompetitive(true);

        when(mockedRepo.existsById(1L)).thenReturn(true);
        when(mockedRepo.findById(1L)).thenReturn(Optional.of(event));

        User user = getUser();

        server.expect(requestTo(apiPrefix + MicroservicePorts.USER.port + userPath + "/1"))
                .andRespond(withSuccess(JsonUtil.serialize(user), MediaType.APPLICATION_JSON));

        assertFalse(mockedService.enqueueById(1L, user.getId(), PositionName.Coach,
                100 + 1440L * ((Day.FRIDAY.ordinal() + 1) % 7)));
    }

    @Test
    void testEnqueueByIdOrganizationNoMatch() throws JsonProcessingException {
        User user = getUser();

        Event event = getEvent("B", 4L, Certificate.B2, EventType.COMPETITION);

        server.expect(requestTo(apiPrefix + MicroservicePorts.USER.port + userPath + "/1"))
                .andRespond(withSuccess(JsonUtil.serialize(user), MediaType.APPLICATION_JSON));
        when(mockedRepo.existsById(1L)).thenReturn(true);
        when(mockedRepo.findById(1L)).thenReturn(Optional.of(event));


        assertFalse(mockedService.enqueueById(1L, user.getId(), PositionName.Cox,
                100 + 1440L * ((Day.FRIDAY.ordinal() + 1) % 7)));
    }

    @Test
    void testEnqueueByIdGenderNoMatch() throws JsonProcessingException {
        User user = getUser();
        user.getUserInfo().setOrganization("B");

        Event event = getEvent("B", 4L, Certificate.B2, EventType.COMPETITION);

        server.expect(requestTo(apiPrefix + MicroservicePorts.USER.port + userPath + "/1"))
                .andRespond(withSuccess(JsonUtil.serialize(user), MediaType.APPLICATION_JSON));
        when(mockedRepo.existsById(1L)).thenReturn(true);
        when(mockedRepo.findById(1L)).thenReturn(Optional.of(event));

        assertFalse(mockedService.enqueueById(1L, user.getId(), PositionName.Cox,
                100 + 1440L * ((Day.FRIDAY.ordinal() + 1) % 7)));
    }

    @Test
    void testEnqueueByIdCertificateNoMatch() throws JsonProcessingException {
        User user = getUser();
        user.getUserInfo().setCertificate(Certificate.B1);

        Event event = getEvent("A", 4L, Certificate.B2, EventType.TRAINING);
        server.expect(requestTo(apiPrefix + MicroservicePorts.USER.port + userPath + "/1"))
                .andRespond(withSuccess(JsonUtil.serialize(user), MediaType.APPLICATION_JSON));
        when(mockedRepo.existsById(1L)).thenReturn(true);
        when(mockedRepo.findById(1L)).thenReturn(Optional.of(event));

        assertFalse(mockedService.enqueueById(1L, user.getId(), PositionName.Cox,
                100 + 1440L * ((Day.FRIDAY.ordinal() + 1) % 7)));
    }

    @Test
    void testEnqueueByIdCreator() throws JsonProcessingException {
        User user = getUser();

        Event event = getEvent("A", 1L, Certificate.B2, EventType.TRAINING);
        server.expect(requestTo(apiPrefix + MicroservicePorts.USER.port + userPath + "/1"))
                .andRespond(withSuccess(JsonUtil.serialize(user), MediaType.APPLICATION_JSON));
        when(mockedRepo.existsById(1L)).thenReturn(true);
        when(mockedRepo.findById(1L)).thenReturn(Optional.of(event));

        assertFalse(mockedService.enqueueById(1L, user.getId(), PositionName.Cox,
                100 + 1440L * ((Day.FRIDAY.ordinal() + 1) % 7)));
    }

    @Test
    void testDequeueById() {
        Request r = new Request("Bob", PositionName.Cox);
        Event event = getEvent("A", 4L, Certificate.B2, EventType.COMPETITION);
        event.getQueue().add(r);
        User user = new User();
        user.getUserInfo().setNetId("Bob");

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
        assertThrows(NoSuchElementException.class, () -> mockedService.dequeueById(1L, r));
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
    void testGetMatchedEventsTrainings() throws JsonProcessingException {
        User user = getUser();
        user.getUserInfo().setOrganization("A");
        List<Event> trainings = List.of(getEvent("A", 4L, Certificate.B2, EventType.TRAINING));

        when(mockedRepo.findMatchingCompetitions(
                user.getUserInfo().getCertificate(), user.getUserInfo().getOrganization(), user.getId(),
                EventType.COMPETITION, user.getUserInfo().getGender()))
                .thenReturn(new ArrayList<>());

        when(mockedRepo.findMatchingTrainings(
                user.getUserInfo().getCertificate(), user.getId(), EventType.TRAINING))
                .thenReturn(trainings);

        server.expect(requestTo(apiPrefix + MicroservicePorts.USER.port + userPath + "/1"))
                .andRespond(withSuccess(JsonUtil.serialize(user), MediaType.APPLICATION_JSON));

        assertEquals(trainings, mockedService.getMatchedEvents(user.getId()));
    }

    @Test
    void testGetMatchedEventsCompetitions() throws JsonProcessingException {
        User user = getUser();
        user.getUserInfo().setOrganization("A");
        List<Event> competitions = List.of(getEvent("A", 4L, Certificate.B2, EventType.COMPETITION));

        when(mockedRepo.findMatchingCompetitions(
                user.getUserInfo().getCertificate(), user.getUserInfo().getOrganization(), user.getId(),
                EventType.COMPETITION, user.getUserInfo().getGender()))
                .thenReturn(competitions);

        when(mockedRepo.findMatchingTrainings(
                user.getUserInfo().getCertificate(), user.getId(), EventType.TRAINING))
                .thenReturn(new ArrayList<>());

        server.expect(requestTo(apiPrefix + MicroservicePorts.USER.port + userPath + "/1"))
                .andRespond(withSuccess(JsonUtil.serialize(user), MediaType.APPLICATION_JSON));
        assertEquals(competitions, mockedService.getMatchedEvents(user.getId()));
    }
}