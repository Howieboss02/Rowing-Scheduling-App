package nl.tudelft.sem.template.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.List;
import nl.tudelft.sem.template.shared.domain.Position;
import nl.tudelft.sem.template.shared.entities.User;
import nl.tudelft.sem.template.shared.enums.Certificate;
import nl.tudelft.sem.template.shared.enums.PositionName;
import org.junit.jupiter.api.Test;

public class UserTest {

    @Test
    public void testConstructorEmpty() {
        User u = new User();
        assertNotNull(u);
    }

    @Test
    public void testConstructorFull() {
        List<Position> pos = new ArrayList<>();
        pos.add(new Position(PositionName.Cox, false));
        pos.add(new Position(PositionName.Coach, true));
        User u = new User("BobID", "Bob", "Bob's Organization", "Bob@b.ob", "Male", Certificate.B1, pos);
        assertEquals(u.getUserInfo().getOrganization(), "Bob's Organization");
        assertEquals(u.getUserInfo().getCertificate().toString(), "B1");
        assertEquals(u.getPositions(), pos);
        assertEquals(u.getPositions().get(0).getName(), PositionName.Cox);
        assertEquals(u.getUserInfo().getGender(), "Male");
    }

    @Test
    public void tesEqualHashCode() {
        User u = new User("BobID", "Bob", null, "Bob@b.ob", null, null, null);
        User u1 = new User("BobID", "Bob", null, "Bob@b.ob", null, null, null);
        assertEquals(u, u1);
        assertEquals(u.hashCode(), u1.hashCode());
    }

    @Test
    public void testAddNotification() {
        User u = new User();
        u.addNotification("You have been REJECTED.");
        int size = u.getNotifications().size();
        assertEquals(u.getNotifications().get(size - 1), "You have been REJECTED.");
    }

}
