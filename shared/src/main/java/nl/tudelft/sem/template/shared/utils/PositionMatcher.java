package nl.tudelft.sem.template.shared.utils;

import java.util.ArrayList;
import java.util.List;
import nl.tudelft.sem.template.shared.domain.Position;
import nl.tudelft.sem.template.shared.entities.Event;
import nl.tudelft.sem.template.shared.entities.User;

public class PositionMatcher {

    /**
     * Match all events accessible to a user.
     *
     * @param events list of all available events
     * @param user the user
     * @return List of positions
     */
    public static List<Event> matchPositions(List<Position> positions, List<Event> events, User user) {
        List<Event> matchedEvents = new ArrayList<>();
        for (Event e : events) {
            for (Position p : positions) {
                if (e.getPositions().contains(p.getName()) && (!e.isCompetitive() || p.isCompetitive())
                        && e.getTimeslot().matchSchedule(user.getSchedule())) {
                    matchedEvents.add(e);
                    break;
                }
            }
        }
        return matchedEvents;
    }
}
