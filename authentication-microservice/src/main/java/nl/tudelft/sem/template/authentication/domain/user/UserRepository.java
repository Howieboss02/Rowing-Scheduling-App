package nl.tudelft.sem.template.authentication.domain.user;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * A DDD repository for querying and persisting user aggregate roots.
 */
@Repository
public interface UserRepository extends JpaRepository<AppUser, String> {
    /**
     * Find user by NetID.
     */
    Optional<AppUser> findByNetId(NetId netId);

    /**
     * Check if an existing user already uses a NetID.
     */
    boolean existsByNetId(NetId netId);

    /**
     * Find user by Email.
     */
    Optional<AppUser> findByEmail(Email email);

    /**
     * Check if an existing user already uses an email.
     */
    boolean existsByEmail(Email email);
}
