package nl.tudelft.sem.template.shared.domain;

import javax.persistence.Convert;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Convert()
@EqualsAndHashCode
@NoArgsConstructor
public class Node {

    private int first;
    private int second;

    public Node(int first, int second) {
        this.first = first;
        this.second = second;
    }
}