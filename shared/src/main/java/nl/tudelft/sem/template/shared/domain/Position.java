package nl.tudelft.sem.template.shared.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import nl.tudelft.sem.template.shared.enums.PositionName;

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

  @Override
  public int compareTo(Position o) {
      return this.name.compareTo(o.getName());
  }

}
