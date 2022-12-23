package nl.tudelft.sem.template.shared.entities;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import nl.tudelft.sem.template.shared.domain.Position;
import nl.tudelft.sem.template.shared.enums.Certificate;

@Data
@AllArgsConstructor
public class UserModel {

    private String name;
    private String organization;
    private String gender;
    private Certificate certificate;
    private List<Position> positions;
}
