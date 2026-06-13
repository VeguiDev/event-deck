package dev.vegui.eventdeck.repository;

import dev.vegui.eventdeck.Main;
import dev.vegui.eventdeck.model.Event;
import dev.vegui.eventdeck.model.EventLocation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SQLiteEventRepository implements EventRepository {

    private final Connection connection;

    public SQLiteEventRepository(
            Connection connection
    ) {
        this.connection = connection;
    }

    @Override
    public void save(Event event) {

        String sql = """
           INSERT INTO events
           (id, title, description, start_date, duration, venue_name, street, city, province, country)
           VALUES
           (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
           ON CONFLICT (id) DO UPDATE SET
             title = excluded.title,
             description = excluded.description,
             duration = excluded.duration,
             venue_name = excluded.venue_name,
             street = excluded.street,
             city = excluded.city,
             province = excluded.province,
             country = excluded.country
        """;

        try(PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, event.getId().toString());
            statement.setString(2, event.getTitle());
            statement.setString(3, event.getDescription());
            statement.setString(4, event.getStartDate().toString());
            statement.setInt(5, event.getDuration());

            EventLocation location =  event.getLocation();

            statement.setString(6, location.getVenueName());
            statement.setString(7, location.getStreet());
            statement.setString(8, location.getCity());
            statement.setString(9, location.getProvince());
            statement.setString(10, location.getCountry());

            statement.executeUpdate();

        } catch (SQLException e) {
            Main.logger.severe(e.getMessage());
        }

    }

    @Override
    public Optional<Event> findById(UUID id) {
        Optional<Event> optionalEvent = Optional.empty();

        String sql = """
        SELECT * FROM events WHERE id = ?
        """;

        try(PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, id.toString());

            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                optionalEvent = Optional.of(
                        eventFromResultSet(resultSet)
                );
            }
            resultSet.close();

        } catch(SQLException e){
            Main.logger.severe(e.getMessage());
        }

        return optionalEvent;
    }

    @Override
    public List<Event> findAll() {
        List<Event> events = new ArrayList<>();

        String sql = """
        SELECT * FROM events
        """;

        try(
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
                ) {

            while(resultSet.next()) {
                events.add(eventFromResultSet(resultSet));
            }

        } catch (SQLException e){
            Main.logger.severe(e.getMessage());
        }
        return events;
    }

    @Override
    public List<Event> findLike(String query) {
        List<Event> events = new ArrayList<>();

        String sql = """
        SELECT * FROM events
        WHERE title LIKE ?  OR description LIKE ? OR venue_name LIKE ? OR city LIKE ? OR country LIKE ?                
        """;

        try(
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
                ){

            while(resultSet.next()) {
                events.add(eventFromResultSet(resultSet));
            }

        }catch (SQLException e){
            Main.logger.severe(e.getMessage());
        }

        return events;
    }

    @Override
    public void delete(UUID id) {
        String sql = "DELETE FROM events WHERE id = ?";

        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.execute();
        } catch (SQLException e) {
            Main.logger.severe(e.getMessage());
        }
    }

    public static Event eventFromResultSet(ResultSet resultSet) throws SQLException {
        UUID id = UUID.fromString(resultSet.getString("id"));
        String title = resultSet.getString("title");
        String description = resultSet.getString("description");
        LocalDateTime startDate = LocalDateTime.parse(resultSet.getString("start_date"));
        int duration = resultSet.getInt("duration");
        String venueName = resultSet.getString("venue_name");
        String street = resultSet.getString("street");
        String city = resultSet.getString("city");
        String province = resultSet.getString("province");
        String country = resultSet.getString("country");
        EventLocation location = new EventLocation(
                venueName,
                street,
                city,
                province,
                country
        );

        return new Event(
                id,
                title,
                description,
                startDate,
                duration,
                location
        );

    }
}
