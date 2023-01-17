package nl.tudelft.sem.template.shared.entities;


import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;
import nl.tudelft.sem.template.shared.converters.PositionsToFillListConverter;
import nl.tudelft.sem.template.shared.converters.ScheduleConverter;
import nl.tudelft.sem.template.shared.converters.UserInfoConverter;
import nl.tudelft.sem.template.shared.domain.*;
import nl.tudelft.sem.template.shared.enums.Certificate;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Getter
@Entity
@Table(name = "User")
public class User {

    @Id
    @Setter
    @Column(name = "id", nullable = false)
    private Long id;

    @Column
    @Convert(converter = UserInfoConverter.class)
    UserInfo userInfo;

    @Setter
    @Column
    @Convert(converter = PositionsToFillListConverter.class)
    private List<Position> positions;

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
        this.userInfo = new UserInfo(netId, name, organization, email, gender, certificate);
        this.positions = positions;
        this.schedule = new Schedule();
        this.notifications = new ArrayList<>();
    }

    /**
    * Constructor for the class used when creating account.
    *
    * @param netId the netId of the user
    * @param name the user's name
    * @param email the user's email
    */
    public User(String netId, String name, String email) {
        this.userInfo = new UserInfo(netId, name, null, email, null, null);
    }

    /**
     * Constructor for the class used when editing account using API call.
     */
    public User(String name, String organization, String gender, Certificate certificate, List<Position> positions) {
        this.userInfo = new UserInfo(null, name, organization, null, gender, certificate);
        this.positions = positions;
    }

    /**
    * Add a recurring slot.
    */
    public void addRecurringSlot(TimeSlot timeSlot) {
        schedule.addRecurringSlot(timeSlot);
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
