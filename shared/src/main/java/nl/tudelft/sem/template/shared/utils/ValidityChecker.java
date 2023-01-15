package nl.tudelft.sem.template.shared.utils;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.tudelft.sem.template.shared.domain.Position;
import nl.tudelft.sem.template.shared.entities.Event;
import nl.tudelft.sem.template.shared.entities.User;
import nl.tudelft.sem.template.shared.enums.Day;
import nl.tudelft.sem.template.shared.enums.EventType;
import nl.tudelft.sem.template.shared.enums.PositionName;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public class ValidityChecker {

    private Event event;
    private User user;

    public ValidityChecker(Event event) {
        this.event = event;
    }

    public ValidityChecker(User user) {
        this.user = user;
    }

    public boolean canJoin(PositionName position, long time) {
        Day day = event.getTimeslot().getDay();
        int dayNumber = (day.ordinal() + 1) % 7;
        long eventTime = dayNumber * 1440L + event.getTimeslot().getTime().getFirst();

        boolean checkIfOnTime = event.getType() == EventType.COMPETITION ?
                (time + 1440L) % 10080L > eventTime : (time + 30L) % 10080L > eventTime; //if true then false

        boolean checkIfCreator = user.getId().equals(event.getOwningUser()); //if true then false

        boolean checkIfCompetitive = event.getType() == EventType.COMPETITION
                && event.isCompetitive() && !user.getPositions().contains(new Position(position, true)); //if true then false

        boolean checkGenderAndOrganization = event.getType() == EventType.COMPETITION
                && (!event.getOrganisation().equals(user.getOrganization())
                || !event.getGender().equals(user.getGender())); //if true then false

        boolean checkCertificate = user.getCertificate().compareTo(event.getCertificate()) < 0; //if true then false

        return !checkIfOnTime && !checkIfCreator && !checkIfCompetitive && !checkGenderAndOrganization && !checkCertificate;
    }

    public boolean canBeMatched() {
        return user.getCertificate() != null && user.getPositions() != null && user.getPositions().size() != 0
                && user.getOrganization() != null;
    }

}
