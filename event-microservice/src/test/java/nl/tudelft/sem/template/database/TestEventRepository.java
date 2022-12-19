package nl.tudelft.sem.template.database;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.template.shared.entities.Event;
import nl.tudelft.sem.template.shared.enums.Certificate;
import nl.tudelft.sem.template.shared.enums.EventType;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * This class will be used to test the EventRepository.
 * But I don't know if we should test all those methods since they are implemented by JPA.
 * I think we should only test the methods we implemented ourselves.
 */
public class TestEventRepository implements EventRepository {

    public final transient List<Event> events = new ArrayList<>();
    public final transient List<String> used = new ArrayList<>();

    /**
     * Adds a called method to the used list.
     *
     * @param s the name of the method
     */
    public void call(String s) {
        used.add(s);
    }

    @Override
    public List<Event> findAll() {
        call("findAll");
        return events;
    }

    @Override
    public List<Event> findAll(Sort sort) {
        return null;
    }

    @Override
    public Page<Event> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public <S extends Event> List<S> findAll(Example<S> example) {
        return null;
    }

    @Override
    public <S extends Event> List<S> findAll(Example<S> example, Sort sort) {
        return null;
    }

    @Override
    public <S extends Event> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public List<Event> findAllById(Iterable<Long> longs) {
        return null;
    }

    @Override
    public long count() {
        call("count");
        return events.size();
    }

    @Override
    public <S extends Event> long count(Example<S> example) {
        return 0;
    }

    @Override
    public void deleteById(Long id) {
        call("deleteById");
        events.remove(id);
    }

    @Override
    public void delete(Event entity) {
        call("delete");
        events.remove(entity);
    }

    @Override
    public void deleteAll(Iterable<? extends Event> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public <S extends Event> S save(S entity) {
        call("save");
        entity.setId((long) events.size());
        events.add(entity);
        return entity;
    }

    @Override
    public <S extends Event> List<S> saveAll(Iterable<S> entities) {
        return null;
    }

    @Override
    public Optional<Event> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(Long id) {
        return false;
    }

    @Override
    public void flush() {

    }

    @Override
    public <S extends Event> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public void deleteInBatch(Iterable<Event> entities) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public Event getOne(Long id) {
        return null;
    }

    @Override
    public <S extends Event> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends Event> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public List<Event> findMatchingTrainings(Certificate certificate, Long id, EventType et) {
        return null;
    }

    @Override
    public List<Event> findMatchingCompetitions(Certificate certificate, String organization,
                                                Long id, EventType competition) {
        return null;
    }
}
