package dev.vegui.eventdeck;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

    private Database() {
    }

    public static Connection connect() throws SQLException {
        String url = "jdbc:sqlite:" + Main.DB_PATH;
        Connection connection = DriverManager.getConnection(url);

        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");
        }

        return connection;
    }


}
