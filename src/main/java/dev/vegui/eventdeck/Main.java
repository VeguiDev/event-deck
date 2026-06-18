package dev.vegui.eventdeck;

import dev.vegui.eventdeck.repository.EventRepository;
import dev.vegui.eventdeck.repository.SQLiteEventRepository;
import dev.vegui.eventdeck.repository.SQLiteTicketRepository;
import dev.vegui.eventdeck.repository.TicketRepository;
import dev.vegui.eventdeck.services.EventService;
import dev.vegui.eventdeck.services.ServiceProvider;
import dev.vegui.eventdeck.services.TicketService;
import dev.vegui.eventdeck.views.*;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

public class Main {
    public static Logger logger = Logger.getLogger("EventDeck");
    public static final Path APPDATA_PATH = resolveAppDataPath();
    public static final Path DB_PATH = APPDATA_PATH.resolve("eventdeck.db");
    public static final Path CONFIG_PATH = APPDATA_PATH.resolve("config.ser");

    private static MainFrame mainFrame;
    private static EventRepository eventRepository;
    private static TicketRepository ticketRepository;
    private static ServiceProvider serviceProvider;
    private static MainState mainState;
    private static Connection connection;
    private static AppConfig appConfig;

    static void main() {

        try {
            initAppData();
            appConfig = AppConfig.load(CONFIG_PATH);
            appConfig.save(CONFIG_PATH);

            connection = Database.connect();

            DatabaseMigrations.runMigrations(connection);
        } catch (SQLException | IOException | ClassNotFoundException e) {

            logger.severe("Error initializing application storage");
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
        router.register(Routes.TICKET_CREATE_BULK, new TicketBulkCreateView());
        router.register(Routes.TICKET_DETAIL, new TicketDetailsView());
        router.register(Routes.TICKET_EDIT, new TicketEditView());
        router.register(Routes.TICKET_USE, new TicketClaimView());

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

    public static AppConfig getAppConfig() {
        return appConfig;
    }

    public static void saveAppConfig() {
        try {
            appConfig.save(CONFIG_PATH);
        } catch (IOException e) {
            logger.severe("Error saving config: " + e.getMessage());
        }
    }

    public static <T> T getService(Class<T> serviceClass) {

        return serviceProvider.getService(serviceClass);

    }

    private static void initAppData() throws IOException {
        Files.createDirectories(APPDATA_PATH);
        migrateLegacyDatabase();
    }

    private static Path resolveAppDataPath() {
        String appData = System.getenv("APPDATA");

        if (appData != null && !appData.isBlank()) {
            return Path.of(appData, "eventdeck");
        }

        return Path.of(System.getProperty("user.home"), "AppData", "Roaming", "eventdeck");
    }

    private static void migrateLegacyDatabase() throws IOException {
        Path legacyDbPath = Path.of("eventdeck.db");

        if (Files.exists(legacyDbPath) && !Files.exists(DB_PATH)) {
            Files.move(legacyDbPath, DB_PATH);
        }
    }
}
