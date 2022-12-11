package nl.tudelft.sem.template.authentication.domain.user;

import java.util.regex.Pattern;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
@Getter
public class Email {
    private final transient String emailValue;

    /**
     * Constructor for Email.
     *
     * @param email the email
     * @throws Exception if the email is invalid
     */
    public Email(String email) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        if (!Pattern.matches("^[A-Za-z0-9+_.-]+@(.+)$", email)) {
            throw new IllegalArgumentException("Email is invalid");
        }
        this.emailValue = email;
    }

    @Override
    public String toString() {
        return emailValue;
    }
}
