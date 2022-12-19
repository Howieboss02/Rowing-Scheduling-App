package nl.tudelft.sem.template.shared.domain;


import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.tudelft.sem.template.shared.enums.PositionName;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Request {

    String name;

    @Enumerated(EnumType.STRING)
    PositionName position;
}
