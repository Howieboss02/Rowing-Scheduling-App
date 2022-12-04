package event;


import org.junit.jupiter.api.Test;

import shared.enities.Event;

import static org.junit.jupiter.api.Assertions.assertNotNull;


public class EventTest {

    @Test
    public void testConstructor(){
        Event e = new Event();
        assertNotNull(e);
    }

}
