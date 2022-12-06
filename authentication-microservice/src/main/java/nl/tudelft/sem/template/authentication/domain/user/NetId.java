package nl.tudelft.sem.template.authentication.domain.user;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * A DDD value object representing a NetID in our domain.
 */
@EqualsAndHashCode
@Getter
public class NetId {
    private final transient String netIdValue;

    public NetId(String netId) {
        // validate NetID
        this.netIdValue = netId;
    }

    @Override
    public String toString() {
        return netIdValue;
    }
}
