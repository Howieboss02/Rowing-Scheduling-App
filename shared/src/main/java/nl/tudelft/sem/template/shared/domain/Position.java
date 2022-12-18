package nl.tudelft.sem.template.shared.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import nl.tudelft.sem.template.shared.enums.PositionName;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Data
@AllArgsConstructor
public class Position implements Comparable<Position> {

  private PositionName name;
  private boolean isCompetitive;

  @Override
  public int compareTo( Position o ) {
    return this.name.compareTo(o.getName());
  }
}
