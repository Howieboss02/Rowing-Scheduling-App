package nl.tudelft.sem.template.shared.entities;


import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import nl.tudelft.sem.template.shared.converters.PositionsToFillListConverter;
import nl.tudelft.sem.template.shared.converters.ScheduleConverter;
import nl.tudelft.sem.template.shared.domain.Node;
import nl.tudelft.sem.template.shared.domain.Position;
import nl.tudelft.sem.template.shared.domain.Schedule;
import nl.tudelft.sem.template.shared.domain.TimeSlot;
import nl.tudelft.sem.template.shared.enums.Certificate;
import nl.tudelft.sem.template.shared.enums.Day;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.data.util.Pair;

@Entity
@Data
@AllArgsConstructor
@Table(name = "User")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Getter private String netId;
    @Getter private String name;
    @Getter private String organization;
    @Getter private String email;
    @Getter private String gender;
    @Getter private Certificate certificate;

    @Column
    @Convert(converter = PositionsToFillListConverter.class)
    @Getter private List<Position> positions;

    @Column
    @ElementCollection(targetClass = String.class)
    private List<String> notifications = new ArrayList<>();

    @Column
    @Convert(converter = ScheduleConverter.class)
    Schedule schedule;

    /*
    TODO: add enqueued activities list
    */

    @SuppressWarnings("unused")
    public User() {}

    /**
    * Constructor for the User class containing all information.
    *
    * @param netId        the netId of a user used to log in
    * @param name         the name and surname of the user
    * @param organization the organization it joined
    * @param email        the unique email of the user
    * @param gender       the gender of the user (indicated by M - male, F - female, O - other)
    * @param certificate  the biggest certificate it holds
    * @param positions    the list of positions it can handle
     */
    public User(String netId, String name, String organization, String email, String gender,
              Certificate certificate, List<Position> positions) {
        this.netId = netId;
        this.name = name;
        this.organization = organization;
        this.email = email;
        this.certificate = certificate;
        this.gender = gender;
        this.positions = positions;
        this.schedule = new Schedule();
    }

    /**
    * Constructor for the class used when creating account.
    *
    * @param netId the netId of the user
    * @param name the user's name
    * @param email the user's email
    */
    public User(String netId, String name, String email) {
        this.netId = netId;
        this.name = name;
        this.email = email;
    }

    /**
    * Method to add another position to the list (for editing).
     *
    * @param position the new position
    */
    public void addPositions(Position position) {
        this.positions.add(position);
    }

    /**
    * Add a recurring slot.
    *
    * @param day  the day of the slot
    * @param time the time interval in seconds of the slot
    */
    public void addRecurringSlot(Day day, Node time) {
        schedule.addRecurringSlot(new TimeSlot(-1, day, time));
    }

    /**
    * Remove a recurring slot.
    *
    * @param day  the day of the slot
    * @param time the time interval in seconds of the slot
    */
    public void removeRecurringSlot(Day day, Node time) {
        schedule.removeRecurringSlot(new TimeSlot(-1, day, time));
    }

    /**
    * Temporarily removes slot.
    *
    * @param slot the time slot that should be temporarily removed
    */
    public void removeSlot(TimeSlot slot) {
        schedule.removeSlot(slot);
    }

    /**
    * Temporarily adds slot.
    *
    * @param slot the time slot that should be temporarily added
    */
    public void addSlot(TimeSlot slot) {
        schedule.addSlot(slot);
    }

    /**
    * Method to append a notification.
    *
    * @param notifications a new notification
    */
    public void addNotification(String notifications) {
        this.notifications.add(notifications);
    }

    /**
    * A method that uses an API supportive version of the "equals" method.
    *
    * @param obj a random type of object to be compared to
    * @return a boolean whether these 2 objects are the same or not
    */
    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj) && obj instanceof User;
    }

    /**
    * A method that uses an API supportive version of hashing.
    *
    * @return a hash code of the User object
    */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    /**
    * A method that uses an API supportive method of transforming data into a string.
    *
    * @return a string containing every detail about the user
    */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

}
