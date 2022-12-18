package nl.tudelft.sem.template.authentication.domain.user;

/**
 * A DDD domain event that indicated a user was created.
 */
public class UserWasCreatedEvent {
    private final NetId netId;
    private final Email email;

    public UserWasCreatedEvent(NetId netId, Email email) {
        this.netId = netId;
        this.email = email;
    }

    public NetId getNetId() {
        return this.netId;
    }

    public Email getEmail() {
        return this.email;
    }
}
