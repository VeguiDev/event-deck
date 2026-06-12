package dev.vegui.eventdeck;

import dev.vegui.eventdeck.model.EventLocation;
import dev.vegui.eventdeck.repository.EventRepository;
import dev.vegui.eventdeck.repository.InMemoryEventRepository;
import dev.vegui.eventdeck.services.EventService;
import dev.vegui.eventdeck.views.EventCreateView;
import dev.vegui.eventdeck.views.EventDetailsView;
import dev.vegui.eventdeck.views.EventListView;

import javax.swing.*;

public class Main {

    private static MainFrame mainFrame;
    private static EventRepository eventRepository;
    private static EventService service;
    private static MainState mainState;

    static void main() {
        eventRepository = new InMemoryEventRepository();
        service = new EventService(eventRepository);

        service.create(
                "AGS 2026",
                "VISA Argentina Game Show 2026",
                java.time.LocalDateTime.now(),
                java.time.Duration.ofHours(2),
                new EventLocation( "Mi kasa", "Calle 1234", "Ciudad mistica", "Buenos Aires", "Argentina")
        );

        service.create(
                "Tottapalooza 2026",
                "Tottapalooza 2026 qwhe8iquwheiqwneiuqw neinqwue niqwne uiqnwi nqwpo uneio qnwep 9iqunwie ounqwione ioqwun epoiqwn epoqwn peon qnop",
                java.time.LocalDateTime.now(),
                java.time.Duration.ofHours(2),
                new EventLocation( "Movistar Arena", "Calle 1234", "Ciudad mistica", "Buenos Aires", "Argentina")
        );

        try {
            UIManager.setLookAndFeel(
                    "javax.swing.plaf.nimbus.NimbusLookAndFeel"
            );
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        javax.swing.SwingUtilities.invokeLater(() -> {
            Router router = new Router();
            mainState = new MainState(router);
            mainFrame = new MainFrame(router);
            setupRouter(router);
            router.navigate(Routes.EVENTS_LIST);

            mainFrame.setVisible(true);
        });
    }

    private static void setupRouter(Router router) {
        router.register(Routes.EVENTS_LIST, new EventListView());
        router.register(Routes.EVENT_CREATE, new EventCreateView());
        router.register(Routes.EVENT_DETAIL, new EventDetailsView());
    }

    public static MainState getState() {
        return mainState;
    }

    public static MainFrame getMainFrame() {
        return mainFrame;
    }

    public static EventRepository getEventRepository() {
        return eventRepository;
    }

    public static EventService getService() {
        return service;
    }
}
