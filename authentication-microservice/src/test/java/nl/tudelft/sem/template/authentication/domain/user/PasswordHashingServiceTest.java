package nl.tudelft.sem.template.authentication.domain.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordHashingServiceTest {

    @Test
    public void testHash() {
        // Create a mock PasswordEncoder
        PasswordEncoder mockEncoder = mock(PasswordEncoder.class);
        when(mockEncoder.encode("password")).thenReturn("hashed_password");

        // Create an instance of PasswordHashingService with the mock encoder
        PasswordHashingService service = new PasswordHashingService(mockEncoder);

        // Hash a password
        Password password = new Password("password");
        HashedPassword hashed = service.hash(password);

        // Assert that the hashed password is not the same as the original password
        assertNotEquals("password", hashed.toString());
        // Assert that the hashed password is the same as the mocked hashed password
        assertEquals("hashed_password", hashed.toString());
    }
}