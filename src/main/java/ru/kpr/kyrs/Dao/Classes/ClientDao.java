package ru.kpr.kyrs.Dao.Classes;

import ru.kpr.kyrs.Dao.Interfaces.ClientDaoInt;
import ru.kpr.kyrs.Pogo.Client;
import ru.kpr.kyrs.Utilites.DatabaseConnection;

import java.sql.*;
import java.util.*;

public class ClientDao implements ClientDaoInt {
    @Override
    public void add(Client client) {
        String sql = """
        INSERT INTO scheme1.clients (name, familya, "lastName", "phoneNumber", email)
        VALUES (?, ?, ?, ?, ?)
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, client.getName());
            ps.setString(2, client.getFamilya());
            ps.setString(3, client.getLastName());
            ps.setString(4, client.getPhoneNumber());
            ps.setString(5, client.getEmail());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    client.setId(rs.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при добавлении клиента", e);
        }
    }

    @Override
    public Optional<Client> getById(Long id) {
        String sql = """
            SELECT * FROM scheme1.clients
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
            throw new RuntimeException("Ошибка при получении клиента по ID", e);
        }
    }

    @Override
    public List<Client> getAll() {
        String sql = """
                SELECT * FROM scheme1.clients
                WHERE is_active = true
            """;

        List<Client> clients = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                clients.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении списка клиентов", e);
        }
        return clients;
    }

    @Override
    public void update(Client client) {
        String sql = """
        UPDATE scheme1.clients 
        SET name = ?, familya = ?, "lastName" = ?, "phoneNumber" = ?, 
            email = ?, updated_at = CURRENT_TIMESTAMP
        WHERE id = ? AND is_active = true
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, client.getName());
            ps.setString(2, client.getFamilya());
            ps.setString(3, client.getLastName());
            ps.setString(4, client.getPhoneNumber());
            ps.setString(5, client.getEmail());
            ps.setLong(6, client.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при обновлении клиента", e);
        }
    }

    @Override
    public void delete(Long id) {
        String sql = """
                UPDATE scheme1.clients SET is_active = false, updated_at = CURRENT_TIMESTAMP WHERE id = ?
                """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при soft delete клиента", e);
        }
    }

    private Client mapRow(ResultSet rs) throws SQLException {
        return new Client(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("familya"),
                rs.getString("lastName"),
                rs.getString("phoneNumber"),
                rs.getString("email")
        );
    }
}