package nl.tudelft.sem.template.authentication.domain.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.when;

import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
// activate profiles to have spring use mocks during auto-injection of certain beans.
@ActiveProfiles({"test", "mockPasswordEncoder"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class RegistrationServiceTests {

    @Autowired
    private transient RegistrationService registrationService;

    @Autowired
    private transient PasswordHashingService mockPasswordEncoder;

    @Autowired
    private transient UserRepository userRepository;

    @Test
    public void createUser_withValidData_worksCorrectly() throws Exception {
        // Arrange
        final NetId testUser = new NetId("SomeUser");
        final Password testPassword = new Password("password123");
        final HashedPassword testHashedPassword = new HashedPassword("hashedTestPassword");
        final Email testEmail = new Email("test@test.com");
        when(mockPasswordEncoder.hash(testPassword)).thenReturn(testHashedPassword);

        // Act
        var u = registrationService.registerUser(testUser, testPassword, testEmail);
        System.out.println(u.getEmail().equals(testEmail));
        // Assert
        AppUser savedUser = userRepository.findByEmail(testEmail).orElseThrow();
        assertThat(registrationService.checkNetIdIsUnique(new NetId("uniquenetid"))).isTrue();
        assertThat(registrationService.checkEmailIsUnique(new Email("unique@email.com"))).isTrue();
        assertThat(savedUser.getNetId()).isEqualTo(testUser);
        assertThat(savedUser.getPassword()).isEqualTo(testHashedPassword);
        assertThat(savedUser.getEmail()).isEqualTo(testEmail);
    }

    @Test
    public void createUser_withExistingUser_throwsException() throws Exception {
        // Arrange
        final NetId testUser = new NetId("SomeUser");
        final HashedPassword existingTestPassword = new HashedPassword("password123");
        final Password newTestPassword = new Password("password456");
        final Email testEmail = new Email("test@test.com");
        AppUser existingAppUser = new AppUser(testUser, existingTestPassword, testEmail);
        userRepository.save(existingAppUser);

        // Act
        ThrowingCallable action = () -> registrationService.registerUser(testUser, newTestPassword, testEmail);
        assertThat(registrationService.checkNetIdIsUnique(testUser)).isFalse();
        // Assert
        assertThatExceptionOfType(Exception.class)
                .isThrownBy(action);

        AppUser savedUser = userRepository.findByNetId(testUser).orElseThrow();

        assertThat(savedUser.getNetId()).isEqualTo(testUser);
        assertThat(savedUser.getPassword()).isEqualTo(existingTestPassword);
    }

    @Test
    public void createUser_withExistEmail_throwsException() throws Exception {
        // Arrange
        final NetId testUser = new NetId("SomeUser");
        final NetId newUser = new NetId("NewUser");
        final HashedPassword existingTestPassword = new HashedPassword("password123");
        final Password newTestPassword = new Password("password456");
        final Email testEmail = new Email("test@test.com");
        AppUser existingAppUser = new AppUser(testUser, existingTestPassword, testEmail);
        userRepository.save(existingAppUser);

        // Act
        ThrowingCallable action = () -> registrationService.registerUser(newUser, newTestPassword, testEmail);
        assertThat(registrationService.checkEmailIsUnique(testEmail)).isFalse();
        // Assert
        assertThatExceptionOfType(Exception.class)
                .isThrownBy(action);


        AppUser savedUser = userRepository.findByNetId(testUser).orElseThrow();

        assertThat(savedUser.getNetId()).isEqualTo(testUser);
        assertThat(savedUser.getPassword()).isEqualTo(existingTestPassword);
        assertThat(savedUser.getEmail()).isEqualTo(testEmail);
    }
}
