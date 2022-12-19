package nl.tudelft.sem.template.shared.domain;


import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.tudelft.sem.template.shared.enums.PositionName;

import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Request {

    String name;

    @Enumerated(EnumType.STRING)
    PositionName position;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Request request = (Request) o;
        return Objects.equals(name, request.name) && position == request.position;
    }
}
