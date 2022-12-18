package nl.tudelft.sem.template.services;

import javassist.NotFoundException;
import nl.tudelft.sem.template.database.EventRepository;
import nl.tudelft.sem.template.shared.domain.Position;
import nl.tudelft.sem.template.shared.enities.Event;
import nl.tudelft.sem.template.shared.enities.User;
import nl.tudelft.sem.template.shared.enums.Certificate;
import nl.tudelft.sem.template.shared.enums.EventType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EventService {
    private final EventRepository eventRepo;
    
    @Autowired
    public EventService(EventRepository eventRepo){
        this.eventRepo = eventRepo;
    }

    public List<Event> getAllEvents() {
        return eventRepo.findAll();
    }

    public Event insert( Event event ) {
        if(event == null){
            return null;
        }else {
            return eventRepo.save(event);
        }
    }

    public void deleteById( Long eventId ) throws Exception {
        if(!eventRepo.existsById(eventId)){
            throw new Exception("ID does not exist");
        }
        eventRepo.deleteById(eventId);
    }

    public Optional<Event> getById(Long id){
        if(!eventRepo.existsById(id)){
            return Optional.empty();
        }else{
            return eventRepo.findById(id);
        }
    }

    public Optional<Event> updateById( Long userId, Long eventId, String label, List<Position> positions, String startTime, String endTime, Certificate certificate, EventType type, String organisation) {
        Optional<Event> toUpdate = getById(eventId);
        if(toUpdate.isPresent()){
            if(toUpdate.get().getOwningUser() != userId){
                return Optional.empty();
            }
            if(label != null)
                toUpdate.get().setLabel(label);
            if(startTime != null)
                toUpdate.get().setStartTime(startTime);
            if(endTime != null)
                toUpdate.get().setEndTime(endTime);
            if(certificate != null)
                toUpdate.get().setCertificate(certificate);
            if(type != null)
                toUpdate.get().setType(type);
            if(organisation != null)
                toUpdate.get().setOrganisation(organisation);
            if(positions != null)
                toUpdate.get().setPositions(positions);
            eventRepo.save(toUpdate.get());
        }
        return toUpdate;
    }


    public List<Event> getMatchedEvents( User user ) {
        List<Event> e1 = eventRepo.findMatchingTrainings(user.getCertificate(), user.getId(), EventType.TRAINING );
        List<Event> e2 = eventRepo.findMatchingCompetitions(user.getCertificate(), user.getOrganization(), user.getId(), EventType.COMPETITION);
        List<Event> matchedEvents = new ArrayList<>();
        List<Position> positions = user.getPositions();
        for(Event e: e1){
            for(Position p: positions){
                if(e.getPositions().contains(p)){
                    matchedEvents.add(e);
                    break;
                }
            }
        }
        for(Event e: e2){
            for(Position p: positions){
                if(e.getPositions().contains(p)){
                    matchedEvents.add(e);
                    break;
                }
            }
        }
        return matchedEvents;
    }
}
