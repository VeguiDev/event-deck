package dev.vegui.eventdeck;

import dev.vegui.eventdeck.repository.EventRepository;
import dev.vegui.eventdeck.repository.SQLiteEventRepository;
import dev.vegui.eventdeck.repository.SQLiteTicketRepository;
import dev.vegui.eventdeck.repository.TicketRepository;
import dev.vegui.eventdeck.services.EventService;
import dev.vegui.eventdeck.services.ServiceProvider;
import dev.vegui.eventdeck.services.TicketService;
import dev.vegui.eventdeck.views.EventCreateView;
import dev.vegui.eventdeck.views.EventDetailsView;
import dev.vegui.eventdeck.views.EventEditView;
import dev.vegui.eventdeck.views.EventListView;
import dev.vegui.eventdeck.views.TicketCreateView;
import dev.vegui.eventdeck.views.TicketDetailsView;
import dev.vegui.eventdeck.views.TicketEditView;

import javax.swing.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

public class Main {
    public static Logger logger = Logger.getLogger("EventDeck");

    private static MainFrame mainFrame;
    private static EventRepository eventRepository;
    private static TicketRepository ticketRepository;
    private static ServiceProvider serviceProvider;
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

        serviceProvider = new ServiceProvider();

        initServices();

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

        // Events
        router.register(Routes.EVENTS_LIST, new EventListView());
        router.register(Routes.EVENT_CREATE, new EventCreateView());
        router.register(Routes.EVENT_DETAIL, new EventDetailsView());
        router.register(Routes.EVENT_EDIT, new EventEditView());

        // Tickets
        router.register(Routes.TICKET_CREATE, new TicketCreateView());
        router.register(Routes.TICKET_DETAIL, new TicketDetailsView());
        router.register(Routes.TICKET_EDIT, new TicketEditView());

    }

    private static void initServices() {
        eventRepository = new SQLiteEventRepository(connection);
        ticketRepository = new SQLiteTicketRepository(connection);

        serviceProvider.register(EventService.class, new EventService(eventRepository));
        serviceProvider.register(TicketService.class, new TicketService(ticketRepository));

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

    public static <T> T getService(Class<T> serviceClass) {
        
        return serviceProvider.getService(serviceClass);

    }
}
