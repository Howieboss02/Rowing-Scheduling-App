package nl.tudelft.sem.template.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import nl.tudelft.sem.template.services.GatewayService;
import nl.tudelft.sem.template.shared.authentication.JwtAuthenticationEntryPoint;
import nl.tudelft.sem.template.shared.authentication.JwtRequestFilter;
import nl.tudelft.sem.template.shared.domain.Request;
import nl.tudelft.sem.template.shared.domain.TimeSlot;
import nl.tudelft.sem.template.shared.entities.Event;
import nl.tudelft.sem.template.shared.entities.EventModel;
import nl.tudelft.sem.template.shared.enums.Certificate;
import nl.tudelft.sem.template.shared.enums.EventType;
import nl.tudelft.sem.template.shared.enums.PositionName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GatewayEventController.class)
@AutoConfigureMockMvc(addFilters = false)
public class EventGatewayControllerTest {

    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(), StandardCharsets.UTF_8);

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GatewayService gatewayService;

    @MockBean
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    @MockBean
    private JwtRequestFilter jwtRequestFilter;

    private final Event event1 = new Event(
            1L,
            "Event 1",
            Arrays.asList(PositionName.Startboard),
            new TimeSlot(),
            Certificate.B1,
            EventType.TRAINING,
            false,
            "M",
            "Organisation 1"
    );

    private final Event event2 = new Event(
            2L,
            "Event 2",
            Arrays.asList(PositionName.Coach, PositionName.Cox),
            new TimeSlot(),
            Certificate.B3,
            EventType.COMPETITION,
            true,
            "F",
            "Organisation 2"
    );

    private final EventModel eventModel = new EventModel(
            1L,
            "Test Event",
            Arrays.asList(PositionName.Coach, PositionName.Cox),
            new TimeSlot(),
            Certificate.B1,
            EventType.TRAINING,
            true,
            "M",
            "Test Organisation"
    );

    private final Request request = new Request("testRequest", PositionName.Coach);



    @Test
    public void testGetEvents() throws Exception {
        List<Event> events = Arrays.asList(event1, event2);
        when(gatewayService.getAllEvents()).thenReturn(events);

        mockMvc.perform(get("/api/event/all"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Event 1")))
                .andExpect(content().string(containsString("Event 2")));
    }

    @Test
    public void testGetEventsWithException() throws Exception {
        when(gatewayService.getAllEvents()).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        mockMvc.perform(get("/api/event/all"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetEventsWithOtherException() throws Exception {
        when(gatewayService.getAllEvents()).thenThrow(new IllegalArgumentException());

        mockMvc.perform(get("/api/event/all"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetEventsByUser() throws Exception {
        List<Event> events = Arrays.asList(event1, event2);
        when(gatewayService.getAllEventsForUser(1L)).thenReturn(events);

        mockMvc.perform(get("/api/event/ownedBy/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Event 1")))
                .andExpect(content().string(containsString("Event 2")));
    }

    @Test
    public void testGetEventsByUserWithException() throws Exception {
        when(gatewayService.getAllEventsForUser(1L)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        mockMvc.perform(get("/api/event/ownedBy/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetEventsByUserWithOtherException() throws Exception {
        when(gatewayService.getAllEventsForUser(1L)).thenThrow(new IllegalArgumentException());

        mockMvc.perform(get("/api/event/ownedBy/1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetRequests() throws Exception {
        List<Request> requests = Arrays.asList(new Request("request1", PositionName.Coach), new Request("request2", PositionName.Coach));
        when(gatewayService.getAllRequestsForEvent(1L)).thenReturn(requests);

        mockMvc.perform(get("/api/event/1/queue"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("request1")))
                .andExpect(content().string(containsString("request2")));
    }

    @Test
    public void testGetRequestsWithException() throws Exception {
        when(gatewayService.getAllRequestsForEvent(1L)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        mockMvc.perform(get("/api/event/1/queue"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetRequestsWithOtherException() throws Exception {
        when(gatewayService.getAllRequestsForEvent(1L)).thenThrow(new IllegalArgumentException());

        mockMvc.perform(get("/api/event/1/queue"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testMatchEvents() throws Exception {
        List<Event> events = Arrays.asList(event1, event2);

        when(gatewayService.getMatchedEventsForUser(1L)).thenReturn(ResponseEntity.ok(events));

        mockMvc.perform(get("/api/event/match/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Event 1")))
                .andExpect(content().string(containsString("Event 2")));
    }

    @Test
    public void testMatchEventsWithException() throws Exception {
        when(gatewayService.getMatchedEventsForUser(1L))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        mockMvc.perform(get("/api/event/match/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testMatchEventsWithOtherException() throws Exception {
        when(gatewayService.getMatchedEventsForUser(1L)).thenThrow(new IllegalArgumentException());

        mockMvc.perform(get("/api/event/match/1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testRegisterNewEvent() throws Exception {
        when(gatewayService.addNewEvent(eventModel)).thenReturn(event1);


        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(eventModel);

        mockMvc.perform(post("/api/event/register")
                        .contentType(APPLICATION_JSON_UTF8).content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Event 1")));
    }

    @Test
    public void testRegisterNewEventWithException() throws Exception {
        when(gatewayService.addNewEvent(eventModel)).thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST));

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(eventModel);

        mockMvc.perform(post("/api/event/register")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testRegisterNewEventWithOtherException() throws Exception {
        when(gatewayService.addNewEvent(eventModel)).thenThrow(new IllegalArgumentException());

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(eventModel);

        mockMvc.perform(post("/api/event/register").contentType(APPLICATION_JSON_UTF8)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testDeleteEvent() throws Exception {
        // Set up mock behavior for the gatewayService
        when(gatewayService.deleteEvent(1L)).thenReturn(ResponseEntity.ok().build());

        // Send a DELETE request to the /{id} endpoint
        mockMvc.perform(delete("/api/event/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteEventWithException() throws Exception {
        // Set up mock behavior for the gatewayService
        doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST)).when(gatewayService).deleteEvent(1L);

        // Send a DELETE request to the /{id} endpoint
        mockMvc.perform(delete("/api/event/1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdateEvent() throws Exception {

        when(gatewayService.updateEvent(eventModel, 1L)).thenReturn(event1);

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(eventModel);

        mockMvc.perform(put("/api/event/1")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Event 1")));
    }

    @Test
    public void testUpdateEventWithException() throws Exception {
        when(gatewayService.updateEvent(eventModel, 1L)).thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST));

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(eventModel);

        mockMvc.perform(put("/api/event/1").contentType(APPLICATION_JSON_UTF8)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testEnqueue() throws Exception {
        when(gatewayService.enqueueToEvent(1L, 1L, PositionName.Coach)).thenReturn("ENQUEUED");

        mockMvc.perform(post("/api/event/1/enqueue/1?position=Coach")
                        .contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("ENQUEUED")));
    }

    @Test
    public void testEnqueueWithException() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST)).when(gatewayService).enqueueToEvent(1L, 1L, PositionName.Coach);

        mockMvc.perform(post("/api/event/1/enqueue/1?position=Coach")
                        .contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testAccept() throws Exception {
        when(gatewayService.acceptToEvent(1L, request)).thenReturn("ACCEPTED");

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(request);

        mockMvc.perform(post("/api/event/1/accept").contentType(APPLICATION_JSON_UTF8)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("ACCEPTED")));
    }

    @Test
    public void testAcceptWithException() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST)).when(gatewayService).acceptToEvent(1L, request);

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(request);

        mockMvc.perform(post("/api/event/1/accept")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testReject() throws Exception {
        when(gatewayService.rejectFromEvent(1L, request)).thenReturn("REJECTED");

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(request);

        mockMvc.perform(post("/api/event/1/reject").contentType(APPLICATION_JSON_UTF8)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("REJECTED")));
    }

    @Test
    public void testRejectWithException() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND)).when(gatewayService).rejectFromEvent(1L, request);

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(request);

        mockMvc.perform(post("/api/event/1/reject")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(requestJson))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testRejectWithAnotherException() throws Exception {
        Request request = new Request();
        doThrow(new IllegalArgumentException()).when(gatewayService).rejectFromEvent(1L, request);

        // Set up JSON representation of the Request object
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(request);

        mockMvc.perform(post("/api/event/1/reject").contentType(APPLICATION_JSON_UTF8)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
    }

}
