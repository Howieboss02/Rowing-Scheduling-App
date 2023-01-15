package nl.tudelft.sem.template.shared.utils;

import lombok.AllArgsConstructor;
import nl.tudelft.sem.template.shared.domain.Position;
import nl.tudelft.sem.template.shared.entities.Event;
import nl.tudelft.sem.template.shared.entities.User;
import nl.tudelft.sem.template.shared.enums.Day;
import nl.tudelft.sem.template.shared.enums.EventType;
import nl.tudelft.sem.template.shared.enums.PositionName;


@AllArgsConstructor
public class ValidityChecker {

    private Event event;
    private User user;

    public ValidityChecker(User user) {
        this.user = user;
    }

    /**
     * Check if the user is valid for the event.
     *
     * @return true if the user is valid for the event
     */
    public boolean canJoin(PositionName position, long time) {
        Day day = event.getTimeslot().getDay();
        int dayNumber = (day.ordinal() + 1) % 7;
        long eventTime = dayNumber * 1440L + event.getTimeslot().getTime().getFirst();
        System.out.println("eventTime: " + eventTime);
        System.out.println("time: " + (time + 1440L) % 10080L);

        boolean checkIfOnTime = event.getType() == EventType.COMPETITION
                ? (time + 1440L) % 10080L > eventTime : (time + 30L) % 10080L > eventTime;
        System.out.println("checkIfOnTime: " + checkIfOnTime);

        boolean checkIfCreator = user.getId().equals(event.getOwningUser());
        System.out.println("checkIfCreator: " + checkIfCreator);

        boolean checkIfCompetitive = event.getType() == EventType.COMPETITION
                && event.isCompetitive() && !user.getPositions().contains(new Position(position, true));
        System.out.println("checkIfCompetitive: " + checkIfCompetitive);

        boolean checkGenderAndOrganization = event.getType() == EventType.COMPETITION
                && (!event.getOrganisation().equals(user.getOrganization())
                || !event.getGender().equals(user.getGender()));
        System.out.println("checkGenderAndOrganization: " + checkGenderAndOrganization);

        boolean checkCertificate = user.getCertificate().compareTo(event.getCertificate()) < 0;
        System.out.println("checkCertificate: " + checkCertificate);

        return !checkIfOnTime && !checkIfCreator && !checkIfCompetitive && !checkGenderAndOrganization && !checkCertificate;
    }

    public boolean canBeMatched() {
        return user.getCertificate() != null && user.getPositions() != null && user.getPositions().size() != 0
                && user.getOrganization() != null;
    }

}
