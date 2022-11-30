package Commons;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  public Long id;

  public String name;
  public String organization;
  public String email;
  public String gender;
  public String certificate;
  public List<String> positions;
  public List<String> notifications = new ArrayList<>();

  /*TODO: add schedule structure
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
  public User(Long id, String name, String organization, String email, String gender,String certificate, List<String> positions){
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
   * Getter for the id
   * @return a unique long for identifying user
   */
  public Long getId() {
    return id;
  }

  /**
   * Getter for the name
   * @return a string representing the name
   */
  public String getName() {
    return name;
  }

  /**
   * Setter for the name in order to permit editing
   * @param name the new name
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Getter for the organization name
   * @return the organization name
   */
  public String getOrganization() {
    return organization;
  }

  /**
   * Setter to change the organization name
   * @param organization the new organization name
   */
  public void setOrganization(String organization) {
    this.organization = organization;
  }

  /**
   * Getter for the user's email
   * @return the email
   */
  public String getEmail() {
    return email;
  }

  /**
   * Setter for the email to permit editing
   * @param email the new email
   */
  public void setEmail(String email) {
    this.email = email;
  }

  /**
   * Getter for the gender
   * @return the user's gender
   */
  public String getGender() {
    return gender;
  }

  /**
   * Setter for the gender to permit editing
   * @param gender the new gender
   */
  public void setGender(String gender) {
    this.gender = gender;
  }

  /**
   * Getter for the certificate
   * @return the biggest certificate the user has obtained
   */
  public String getCertificate() {
    return certificate;
  }

  /**
   * Setter to edit the certificate
   * @param certificate the new certificate
   */
  public void setCertificate(String certificate) {
    this.certificate = certificate;
  }

  /**
   * Getter for the list of positions
   * @return the list of all possible positions to fill
   */
  public List<String> getPositions() {
    return positions;
  }

  /**
   * Setter for the list of possible positions to fill
   * @param positions the list of positions
   */
  public void setPositions(List<String> positions) {
    this.positions = positions;
  }

  /**
   * Method to add another position to the list (for editing)
   * @param position the new position
   */
  public void addPositions(String position){
    this.positions.add(position);
  }

  /**
   * Getter for the list of notifications
   * @return the received notification
   */
  public List<String> getNotifications() {
    return notifications;
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
    return EqualsBuilder.reflectionEquals(this, obj);
  }

  /**
   * A method that uses an API supportive version of hashing.
   *
   * @return a hash code of the Player object
   */
  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }

  /**
   * A method that uses an API supportive method of transforming data into a string.
   *
   * @return a string containing every detail about the player
   */
  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE);
  }

}
