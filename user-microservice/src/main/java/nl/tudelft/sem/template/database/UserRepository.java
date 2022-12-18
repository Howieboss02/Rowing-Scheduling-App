package nl.tudelft.sem.template.database;

import nl.tudelft.sem.template.shared.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long>{
    <S extends User> List<S> saveAllAndFlush( Iterable<S> entities );

    void deleteAllInBatch( Iterable<User> entities );

    void deleteAllByIdInBatch( Iterable<Long> longs );

    User getById( Long aLong );
}
