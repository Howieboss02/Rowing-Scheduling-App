package nl.tudelft.sem.template.database;

import nl.tudelft.cse.sem.template.shared.enities.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {}
