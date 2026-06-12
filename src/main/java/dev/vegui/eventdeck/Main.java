package dev.vegui.eventdeck;

import dev.vegui.eventdeck.model.EventLocation;
import dev.vegui.eventdeck.repository.EventRepository;
import dev.vegui.eventdeck.repository.InMemoryEventRepository;
import dev.vegui.eventdeck.services.EventService;
import dev.vegui.eventdeck.views.EventListView;

import javax.swing.*;

public class Main {

    private static MainFrame mainFrame;
    private static Router router;
    private static EventRepository eventRepository;
    private static EventService service;

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
                "Maria Becerra",
                "Maria Becerra en el Movisar Arena",
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
            router = new Router();
            mainFrame = new MainFrame(router);
            router.register(Routes.EVENTS_LIST, new EventListView());

            router.navigate(Routes.EVENTS_LIST);
            mainFrame.setVisible(true);
        });
    }

    public static Router getRouter() {
        return router;
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
