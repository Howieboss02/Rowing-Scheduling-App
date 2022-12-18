package nl.tudelft.sem.template.authentication.domain.user;

public class EmailAlreadyInUseException extends Exception {
    static final long serialVersionUID = -3387516993124229949L;

    public EmailAlreadyInUseException(Email email) {
        super(email.toString());
    }
}

