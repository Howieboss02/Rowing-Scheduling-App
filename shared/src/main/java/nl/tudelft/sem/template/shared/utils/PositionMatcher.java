package nl.tudelft.sem.template.shared.utils;

import nl.tudelft.sem.template.shared.domain.Position;
import nl.tudelft.sem.template.shared.entities.Event;
import nl.tudelft.sem.template.shared.entities.User;

import java.util.ArrayList;
import java.util.List;

public class PositionMatcher {

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
