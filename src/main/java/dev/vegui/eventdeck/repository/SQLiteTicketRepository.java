package dev.vegui.eventdeck.repository;

import dev.vegui.eventdeck.Main;
import dev.vegui.eventdeck.model.Ticket;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SQLiteTicketRepository implements TicketRepository {

    private final Connection connection;

    public SQLiteTicketRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void save(Ticket ticket) {
        String sql = """
                INSERT INTO tickets (
                id,
                code,
                attendeeName,
                attendeeEmail,
                event_id,
                created_at,
                attended_at,
                deleted_at
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                ON CONFLICT (id) DO UPDATE SET
                code = excluded.code,
                attendeeName = excluded.attendeeName,
                attendeeEmail = excluded.attendeeEmail,
                event_id = excluded.event_id,
                created_at = excluded.created_at,
                attended_at = excluded.attended_at,
                deleted_at = excluded.deleted_at
                """;

        try (PreparedStatement stat = connection.prepareStatement(sql)) {

            stat.setString(1, ticket.getId().toString());
            stat.setString(2, ticket.getCode());
            stat.setString(3, ticket.getAttendeeName());
            stat.setString(4, ticket.getAttendeeEmail());

            if (ticket.getEvent() != null) {
                stat.setString(5, ticket.getEvent().getId().toString());
            } else if (ticket.getEventId() != null) {
                stat.setString(5, ticket.getEventId().toString());
            } else {
                stat.setString(5, null);
            }

            stat.setString(6, ticket.getCreatedAt().toString());

            if (ticket.getAttendedAt() == null) {
                stat.setString(7, null);
            } else {
                stat.setString(7, ticket.getAttendedAt().toString());
            }

            if (ticket.getDeletedAt() == null) {
                stat.setString(8, null);
            } else {
                stat.setString(8, ticket.getDeletedAt().toString());
            }

            stat.execute();

        } catch (SQLException ex) {
            Main.logger.severe("Error saving ticket: " + ex.getMessage());
        }
    }

    @Override
    public Optional<Ticket> findById(UUID id) {
        String sql = """
                SELECT
                    * FROM tickets WHERE id = ?;
                """;

        try (PreparedStatement stat = connection.prepareStatement(sql)) {
            stat.setString(1, id.toString());

            try (ResultSet rs = stat.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(
                            this.parseTicket(rs)
                    );
                }
            }

        } catch (SQLException ex) {
            Main.logger.severe("Error finding ticket: " + ex.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Optional<Ticket> findByCode(String code) {
        String sql = "SELECT * FROM tickets WHERE code = ? AND deleted_at IS NULL AND attended_at IS NULL;";

        try (PreparedStatement stat = connection.prepareStatement(sql)) {
            stat.setString(1, code);

            try (ResultSet rs = stat.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(
                            this.parseTicket(rs)
                    );
                }
            }

        } catch (SQLException ex) {
            Main.logger.severe("Error find ticket by code: " + ex.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public List<Ticket> findAll() {
        String sql = "SELECT * FROM tickets;";
        List<Ticket> tickets = new ArrayList<>();

        try (
                PreparedStatement stat = connection.prepareStatement(sql);
                ResultSet rs = stat.executeQuery()
        ) {
            while (rs.next()) {
                Ticket ticket = this.parseTicket(rs);
                tickets.add(ticket);
            }
        } catch (SQLException ex) {
            Main.logger.severe("Error finding tickets: " + ex.getMessage());
        }

        return tickets;
    }

    @Override
    public List<Ticket> findByEvent(UUID event_id) {
        String sql = "SELECT * FROM tickets WHERE event_id = ?;";
        List<Ticket> tickets = new ArrayList<>();

        try (
                PreparedStatement stat = connection.prepareStatement(sql)
        ) {
            stat.setString(1, event_id.toString());

            try (ResultSet rs = stat.executeQuery()) {
                while (rs.next()) {
                    Ticket ticket = this.parseTicket(rs);
                    tickets.add(ticket);
                }
            }
        } catch (SQLException ex) {
            Main.logger.severe("Error find ticket by event: " + ex.getMessage());
        }

        return tickets;
    }

    @Override
    public void delete(UUID id) {
        String sql = "DELETE FROM tickets WHERE id = ?;";

        try (PreparedStatement stat = connection.prepareStatement(sql)) {

            stat.setString(1, id.toString());
            stat.execute();

        } catch (SQLException ex) {
            Main.logger.severe("Error deleting ticket: " + ex.getMessage());
        }
    }

    private Ticket parseTicket(ResultSet rs) throws SQLException {
        Ticket ticket = new Ticket();

        ticket.setId(UUID.fromString(rs.getString("id")));
        ticket.setCode(rs.getString("code"));
        ticket.setAttendeeName(rs.getString("attendeeName"));
        ticket.setAttendeeEmail(rs.getString("attendeeEmail"));

        if (rs.getString("event_id") != null) {
            ticket.setEventId(UUID.fromString(rs.getString("event_id")));
        }

        ticket.setCreatedAt(LocalDateTime.parse(rs.getString("created_at")));

        if (rs.getString("attended_at") != null) {
            ticket.setAttendedAt(LocalDateTime.parse(rs.getString("attended_at")));
        }

        if (rs.getString("deleted_at") != null) {
            ticket.setDeletedAt(LocalDateTime.parse(rs.getString("deleted_at")));
        }

        return ticket;
    }
}
