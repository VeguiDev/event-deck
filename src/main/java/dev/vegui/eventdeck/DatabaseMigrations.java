package dev.vegui.eventdeck;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseMigrations {

    private static final String MIGRATIONS_PATH = "db/migrations";

    private static final Migration[] MIGRATIONS = {
            new Migration(1, "db/migrations/v000-base-entities.sql"),
            new Migration(2, "db/migrations/v001-ticket-entity.sql"),
    };

    public record Migration(int version, String path) {
    }

    public static void prepareMigrations(
            Connection c
    ) throws SQLException {

        c.prepareStatement(
                """
                        CREATE TABLE IF NOT EXISTS migrations (
                            version INTEGER PRIMARY KEY
                        )
                        """
        ).execute();

    }

    public static List<Integer> getMigratedVersions(Connection connection) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(
                """
                        SELECT version FROM migrations
                        """
        );

        List<Integer> versions = new ArrayList<>();

        try (ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                versions.add(resultSet.getInt("version"));
            }
        }
        return versions;
    }

    public static void markMigrated(Connection connection, Migration migration) throws SQLException {
        String sql = """
                INSERT INTO migrations (version) VALUES (?)
                """;

        try (
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, migration.version);
            statement.execute();
        }
    }

    public static void runMigrations(Connection connection) throws SQLException {

        prepareMigrations(connection);

        List<Integer> migrated = getMigratedVersions(connection);

        for (Migration migration : MIGRATIONS) {

            if (migrated.contains(migration.version)) continue;

            connection.prepareStatement(getMigrationStatement(migration)).execute();

            markMigrated(connection, migration);
        }

    }

    public static String getMigrationStatement(Migration migration) {
        InputStream stream = openResource(migration.path);

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            StringBuilder statement = new StringBuilder();
            String line;

            for (line = reader.readLine(); line != null; line = reader.readLine()) {
                statement.append(line).append("\n");
            }
            return statement.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static InputStream openResource(String path) {
        InputStream stream = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream(path);

        if (stream == null) {
            throw new IllegalArgumentException(
                    "No existe el resource: " + path
            );
        }

        return stream;
    }


}
