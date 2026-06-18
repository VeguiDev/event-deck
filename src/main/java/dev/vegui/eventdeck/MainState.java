package dev.vegui.eventdeck;

import dev.vegui.eventdeck.model.Event;
import dev.vegui.eventdeck.model.Ticket;

public class MainState {

    private Event currentEvent;
    private Ticket currentTicket;

    private final Router router;

    public MainState(Router router) {
        this.router = router;
    }

    public Event getCurrentEvent() {
        return currentEvent;
    }
    public void setCurrentEvent(Event currentEvent) {
        this.currentEvent = currentEvent;
    }

    public Ticket getCurrentTicket() {
        return currentTicket;
    }

    public void setCurrentTicket(Ticket currentTicket) {
        this.currentTicket = currentTicket;
    }

    public Router getRouter() {
        return router;
    }


}
