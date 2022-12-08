package nl.tudelft.sem.template.services;

import nl.tudelft.sem.template.database.EventRepository;
import nl.tudelft.sem.template.shared.domain.Position;
import nl.tudelft.sem.template.shared.enities.Event;
import nl.tudelft.sem.template.shared.enums.Certificate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public boolean deleteById( Long eventId ) {
        if(!eventRepo.existsById(eventId)){
            return false;
        }else{
            eventRepo.deleteById(eventId);
            return true;
        }
    }

    public Optional<Event> getById(Long id){
        if(!eventRepo.existsById(id)){
            return Optional.empty();
        }else{
            return eventRepo.findById(id);
        }
    }

    public Optional<Event> updateById( Long userId, Long eventId, String label, List<Position> positions, String startTime, String endTime, Certificate certificate, boolean isCompetitive, String organisation ) {
        Optional<Event> toUpdate = getById(eventId);
        if(toUpdate.isPresent()){
            if(toUpdate.get().getOwningUser() != userId){
                return Optional.empty();
            }
            toUpdate.get().setLabel(label);
            toUpdate.get().setStartTime(startTime);
            toUpdate.get().setEndTime(endTime);
            toUpdate.get().setCertificate(certificate);
            toUpdate.get().setOrganisation(organisation);
            toUpdate.get().setCompetitive(isCompetitive);
            toUpdate.get().setPositions(positions);
        }
        return toUpdate;
    }
}
