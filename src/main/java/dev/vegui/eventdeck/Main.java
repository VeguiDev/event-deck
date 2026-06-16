package dev.vegui.eventdeck;

import dev.vegui.eventdeck.repository.EventRepository;
import dev.vegui.eventdeck.repository.SQLiteEventRepository;
import dev.vegui.eventdeck.services.EventService;
import dev.vegui.eventdeck.views.EventCreateView;
import dev.vegui.eventdeck.views.EventDetailsView;
import dev.vegui.eventdeck.views.EventListView;

import javax.swing.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

public class Main {
    public static Logger logger = Logger.getLogger("EventDeck");

    private static MainFrame mainFrame;
    private static EventRepository eventRepository;
    private static EventService service;
    private static MainState mainState;
    private static Connection connection;

    static void main() {

        try {
            connection = Database.connect();

            DatabaseMigrations.runMigrations(connection);
        } catch (SQLException e) {

            logger.severe("Error connecting to database");
            e.printStackTrace();
            System.exit(-1);
        }

        eventRepository = new SQLiteEventRepository(connection);
        service = new EventService(eventRepository);

//        service.create(
//                "AGS 2026",
//                "VISA Argentina Game Show 2026",
//                java.time.LocalDateTime.now(),
//                2,
//                new EventLocation( "Mi kasa", "Calle 1234", "Ciudad mistica", "Buenos Aires", "Argentina")
//        );
//
//        service.create(
//                "Tottapalooza 2026",
//                "Tottapalooza 2026 qwhe8iquwheiqwneiuqw neinqwue niqwne uiqnwi nqwpo uneio qnwep 9iqunwie ounqwione ioqwun epoiqwn epoqwn peon qnop",
//                java.time.LocalDateTime.now(),
//                2,
//                new EventLocation( "Movistar Arena", "Calle 1234", "Ciudad mistica", "Buenos Aires", "Argentina")
//        );

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
