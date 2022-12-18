package nl.tudelft.sem.template.database;

import nl.tudelft.sem.template.shared.enities.Event;
import nl.tudelft.sem.template.shared.enums.Certificate;
import nl.tudelft.sem.template.shared.enums.EventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("select e from Event as e where e.certificate <= ?1 and e.type = ?3 and e.owningUser <> ?2")
    List<Event> findMatchingTrainings( Certificate certificate, Long id , EventType et);

    @Query("select e from Event as e where e.certificate <= ?1 and e.type = ?4 and e.organisation = ?2 and e.owningUser <> ?3")
    List<Event> findMatchingCompetitions( Certificate certificate, String organization, Long id, EventType competition );
}
