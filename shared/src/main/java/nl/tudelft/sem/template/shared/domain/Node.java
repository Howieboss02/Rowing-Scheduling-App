package nl.tudelft.sem.template.shared.domain;

import lombok.EqualsAndHashCode;
import lombok.Data;
import javax.persistence.Convert;

@Data
@Convert()
@EqualsAndHashCode
public class Node {

    private int first;
    private int second;

    public Node(int first, int second) {
        this.first = first;
        this.second = second;
    }
}