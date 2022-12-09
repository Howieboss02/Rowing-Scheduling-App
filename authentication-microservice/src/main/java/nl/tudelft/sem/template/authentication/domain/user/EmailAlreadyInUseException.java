package nl.tudelft.sem.template.authentication.domain.user;

public class EmailAlreadyInUseException extends Exception {
    static final long serialVersionUID = -3387516993124229948L;

    public EmailAlreadyInUseException(Email email) {
        super("Email " + email + " is already in use");
    }
}

