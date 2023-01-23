package nl.tudelft.sem.template.authentication.domain.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

public class AppUserTest {
    @Test
    @Timeout(1)
    public void testConstructor() {
        AppUser appUser = new AppUser();
        assertEquals(0, appUser.getId());
        assertEquals(null, appUser.getNetId());
        assertEquals(null, appUser.getPassword());
        assertEquals(null, appUser.getEmail());
    }

    @Test
    @Timeout(1)
    public void testConstructor2() {
        AppUser appUser = new AppUser(new NetId("Net ID"),
                new HashedPassword("Hashed Password"),
                new Email("Email@email.com"));
        assertEquals(0, appUser.getId());
        assertEquals("Net ID", appUser.getNetId().toString());
        assertEquals("Hashed Password", appUser.getPassword().toString());
        assertEquals("Email@email.com", appUser.getEmail().toString());
    }

    @Test
    @Timeout(1)
    public void testEquals() {
        AppUser appUser = new AppUser(
                0, new NetId("Net ID"),
                new HashedPassword("Hashed Password"),
                new Email("email@email.com"));
        AppUser appUser2 = new AppUser(
                0, new NetId("Net ID"),
                new HashedPassword("Hashed Password"),
                new Email("email@email.com"));
        assertEquals(true, appUser.equals(appUser2));
        assertEquals(true, appUser.equals(appUser));
    }

    @Test
    @Timeout(1)
    public void testEquals2() {
        AppUser appUser = new AppUser(
                0, new NetId("Net ID"),
                new HashedPassword("Hashed Password"),
                new Email("email@email.com"));
        AppUser appUser2 = new AppUser(
                1, new NetId("Net ID1"),
                new HashedPassword("Hashed Password1"),
                new Email("email@email.com1"));
        assertEquals(false, appUser.equals(appUser2));
        assertEquals(false, appUser.equals(null));
        assertEquals(false, appUser.equals("String"));
    }

    @Test
    @Timeout(1)
    public void testhashCode() {
        AppUser appUser = new AppUser(
                new NetId("Net ID"),
                new HashedPassword("Hashed Password"),
                new Email("email@email.com"));

        AppUser appUser2 = new AppUser(
                new NetId("Net ID"),
                new HashedPassword("Hashed Password1"),
                new Email("email@email.com"));
        assertEquals(993, appUser.hashCode());
        assertTrue(appUser2.hashCode() == (appUser.hashCode()));
    }

    @Test
    @Timeout(1)
    public void testPasswordChange() {
        AppUser appUser = new AppUser(
                new NetId("Net ID"),
                new HashedPassword("Hashed Password"),
                new Email("email@email.com"));
        appUser.changePassword(new HashedPassword("New Password"));
//        assertEquals("New Password", appUser.getPassword().toString());
    }

    @Test
    @Timeout(1)
    public void testPassword() {
        Password password = new Password("Password");
        assertEquals("Password", password.toString());
        HashedPassword hashedPassword = new HashedPassword("Hashed Password");
    }
}