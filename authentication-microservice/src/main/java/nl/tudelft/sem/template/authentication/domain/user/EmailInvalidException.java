package nl.tudelft.sem.template.authentication.domain.user;

public class EmailInvalidException extends Exception {
    static final long serialVersionUID = -3387516993124229948L;

    public EmailInvalidException(String email) {
        super(email);
    }
}

