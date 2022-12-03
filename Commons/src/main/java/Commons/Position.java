package Commons;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Objects;

public class Position {

  PositionName name;
  boolean isCompetitive;

  /**
   * Constructor for the class.
   *
   * @param name          the name of the position
   * @param isCompetitive whether it's a competitive or amateur position
   */
  public Position(PositionName name, boolean isCompetitive) {
    this.name = name;
    this.isCompetitive = isCompetitive;
  }

  /**
   * Getter for the name
   *
   * @return the name of the position
   */
  public String getName() {
    return name.toString();
  }

  /**
   * Getter for the competitive level
   *
   * @return a boolean that shows if the position is competitive
   */
  public boolean isCompetitive() {
    return isCompetitive;
  }

  /**
   * Setter for the competitive level
   *
   * @param competitive the competitive level
   */
  public void setCompetitive(boolean competitive) {
    isCompetitive = competitive;
  }

  /**
   * Equals method for asserting if 2 positions are the same
   *
   * @param o the other we are comparing
   * @return a boolean showing if they are equal
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Position)) return false;
    Position position = (Position) o;
    return isCompetitive == position.isCompetitive && name == position.name;
  }

  /**
   * A friendly method of hashing for the API
   * @return the hash code of a position
   */
  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }
}
