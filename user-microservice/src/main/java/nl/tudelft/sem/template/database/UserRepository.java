package nl.tudelft.sem.template.database;

import nl.tudelft.sem.template.shared.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
