package ru.kpr.kyrs.Dao.Classes;

import ru.kpr.kyrs.Dao.Interfaces.ClientRequestDaoInt;
import ru.kpr.kyrs.Pogo.ClientRequest;
import ru.kpr.kyrs.Pogo.Client;
import ru.kpr.kyrs.Pogo.Address;
import ru.kpr.kyrs.Utilites.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class ClientRequestDao implements ClientRequestDaoInt {

    @Override
    public void add(ClientRequest clientRequest) {
        String sql = """
        INSERT INTO scheme1.client_requests ("createdAt", "executionDate", "clientId", "numberOfPersons", "startPoint", "endPoint")
        VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setObject(1, clientRequest.getCreatedAt());
            ps.setObject(2, clientRequest.getExecutionDate());
            ps.setLong(3, clientRequest.getClientId().getId());
            ps.setInt(4, clientRequest.getNumberOfPersons());
            ps.setLong(5, clientRequest.getStartPoint().getId());
            ps.setLong(6, clientRequest.getEndPoint().getId());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    clientRequest.setId(rs.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при добавлении заявки клиента", e);
        }
    }

    @Override
    public Optional<ClientRequest> getById(Long id) {
        String sql = """
            SELECT * FROM scheme1.client_requests
            WHERE id = ? AND is_active = true
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return Optional.of(mapRow(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении заявки по ID", e);
        }
    }

    @Override
    public List<ClientRequest> getAll() {
        String sql = """
    SELECT
        cr.*,

        c.id AS c_id,
        c.name AS c_name,
        c.familya AS c_familya,
        c."lastName" AS c_lastName,
        c."phoneNumber" AS c_phoneNumber,
        c.email AS c_email,

        sp.id AS sp_id,
        sp.town AS sp_town,
        sp.street AS sp_street,
        sp.house AS sp_house,

        ep.id AS ep_id,
        ep.town AS ep_town,
        ep.street AS ep_street,
        ep.house AS ep_house

    FROM scheme1.client_requests cr

    LEFT JOIN scheme1.clients c
        ON c.id = cr."clientId"

    LEFT JOIN scheme1.addresses sp
        ON sp.id = cr."startPoint"

    LEFT JOIN scheme1.addresses ep
        ON ep.id = cr."endPoint"

    WHERE cr.is_active = true
    ORDER BY cr."createdAt" DESC
    """;

        List<ClientRequest> requests = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                requests.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении списка заявок", e);
        }
        return requests;
    }

    @Override
    public void update(ClientRequest clientRequest) {
        String sql = """
        UPDATE scheme1.client_requests 
        SET "executionDate" = ?, "numberOfPersons" = ?, "startPoint" = ?, "endPoint" = ?, updated_at = CURRENT_TIMESTAMP
        WHERE id = ? AND is_active = true
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, clientRequest.getExecutionDate());
            ps.setInt(2, clientRequest.getNumberOfPersons());
            ps.setLong(3, clientRequest.getStartPoint().getId());
            ps.setLong(4, clientRequest.getEndPoint().getId());
            ps.setLong(5, clientRequest.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при обновлении заявки", e);
        }
    }

    @Override
    public void delete(Long id) {
        String sql = """
                UPDATE scheme1.client_requests SET is_active = false, updated_at = CURRENT_TIMESTAMP WHERE id = ?
                """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при soft delete заявки", e);
        }
    }

    @Override
    public List<ClientRequest> getByClientId(Long clientId) {
        String sql = """
            SELECT * FROM scheme1.client_requests
            WHERE "clientId" = ? AND is_active = true
            ORDER BY "executionDate" ASC
            """;

        List<ClientRequest> requests = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, clientId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                requests.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении заявок по ID клиента", e);
        }
        return requests;
    }

    @Override
    public List<ClientRequest> getByExecutionDate(LocalDate executionDate) {
        String sql = """
            SELECT * FROM scheme1.client_requests
            WHERE "executionDate" = ? AND is_active = true
            ORDER BY "executionDate" ASC
            """;

        List<ClientRequest> requests = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, executionDate);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                requests.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении заявок по дате выполнения", e);
        }
        return requests;
    }

    @Override
    public List<ClientRequest> getByDateRange(LocalDate startDate, LocalDate endDate) {
        String sql = """
            SELECT * FROM scheme1.client_requests
            WHERE "executionDate" BETWEEN ? AND ? AND is_active = true
            ORDER BY "executionDate" ASC
            """;

        List<ClientRequest> requests = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, startDate);
            ps.setObject(2, endDate);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                requests.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении заявок по диапазону дат", e);
        }
        return requests;
    }

    private ClientRequest mapRow(ResultSet rs) throws SQLException {

        Client client = new Client(
                rs.getLong("c_id"),
                rs.getString("c_name"),
                rs.getString("c_familya"),
                rs.getString("c_lastName"),
                rs.getString("c_phoneNumber"),
                rs.getString("c_email")
        );

        Address startPoint = new Address(
                rs.getLong("sp_id"),
                rs.getString("sp_town"),
                rs.getString("sp_street"),
                rs.getString("sp_house")
        );

        Address endPoint = new Address(
                rs.getLong("ep_id"),
                rs.getString("ep_town"),
                rs.getString("ep_street"),
                rs.getString("ep_house")
        );

        return new ClientRequest(
                rs.getLong("id"),
                rs.getTimestamp("createdAt") != null
                        ? rs.getTimestamp("createdAt").toLocalDateTime()
                        : null,
                rs.getDate("executionDate") != null
                        ? rs.getDate("executionDate").toLocalDate()
                        : null,
                client,
                rs.getInt("numberOfPersons"),
                startPoint,
                endPoint
        );
    }
}