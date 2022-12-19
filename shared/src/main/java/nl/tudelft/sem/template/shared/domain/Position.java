package nl.tudelft.sem.template.shared.domain;

import lombok.Data;
import nl.tudelft.sem.template.shared.enums.PositionName;

@Data
public class Position {

  private PositionName name;
  private boolean isCompetitive;

  public Position(PositionName name, boolean isCompetitive) {
    this.name = name;
    this.isCompetitive = isCompetitive;
  }

  /**
   * Empty constructor for deserializing and serializing Jsons.
   */
  public Position() {
  }

  public int compareTo(Position o) {
      return this.name.compareTo(o.getName());
  }

}
