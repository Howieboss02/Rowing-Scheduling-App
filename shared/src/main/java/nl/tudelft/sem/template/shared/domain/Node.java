package nl.tudelft.sem.template.shared.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class Node {
    int first;
    int second;

    public Node(int first, int second) {
        this.first = first;
        this.second = second;
    }
}