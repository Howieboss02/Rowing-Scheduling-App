package nl.tudelft.sem.template;

import nl.tudelft.sem.template.shared.enums.Outcome;

public class Message {
    public Long idUser;
    public Outcome outcome;

    public Message(Long idUser, Outcome outcome){
        this.idUser = idUser;
        this.outcome = outcome;
    }
}
