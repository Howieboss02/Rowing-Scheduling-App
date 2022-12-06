package nl.tudelft.sem.template.shared.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import nl.tudelft.sem.template.shared.enums.PositionName;

@Data
@AllArgsConstructor
public class Position {

  private PositionName name;
  private boolean isCompetitive;
}
