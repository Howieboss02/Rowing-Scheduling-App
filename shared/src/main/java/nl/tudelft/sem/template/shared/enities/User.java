package nl.tudelft.sem.template.shared.enities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import nl.tudelft.sem.template.shared.domain.Position;
import nl.tudelft.sem.template.shared.enums.Certificate;

@Entity
@Data
@AllArgsConstructor
@Table(name = "User")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  private String name;
  private String organization;
  private String email;
  private String gender;
  private Certificate certificate;
  private List<Position> positions;
  private List<String> notifications = new ArrayList<>();

  /*TODO: add schedule structure
   */

  /*
  TODO: add enqueued activities list
   */

  @SuppressWarnings("unused")
  public User(){
  }

  /**
   * Constructor for the User class containing all information
   * @param id the id inside the DB
   * @param name the name of the user
   * @param organization the organization it joined
   * @param email the unique email of the user
   * @param gender the gender of the user
   * @param certificate the biggest certificate it holds
   * @param positions the list of positions it can handle
   */
  public User(Long id, String name, String organization, String email, String gender, Certificate certificate, List<Position> positions){
    this.id = id;
    this.name = name;
    this.organization = organization;
    this.email = email;
    this.certificate = certificate;
    this.gender = gender;
    this.positions = positions;
  }
  /**
   * Constructor for the class used when creating account
   * @param id the id of the user
   * @param name the user's name
   * @param email the user's email
   */
  public User(Long id, String name, String email){
    this.id = id;
    this.name = name;
    this.email = email;
  }

  /**
   * Method to add another position to the list (for editing)
   * @param position the new position
   */
  public void addPositions(Position position){
    this.positions.add(position);
  }

  /**
   * Method to append a notification
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
