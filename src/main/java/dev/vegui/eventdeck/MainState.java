package dev.vegui.eventdeck;

import dev.vegui.eventdeck.model.Event;

public class MainState {

    private Event currentEvent;

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

    public Router getRouter() {
        return router;
    }


}
