package ru.kpr.kyrs.Dao.Classes;

import ru.kpr.kyrs.Dao.Interfaces.AddressDaoInt;
import ru.kpr.kyrs.Pogo.Address;
import ru.kpr.kyrs.Utilites.DatabaseConnection;

import java.sql.*;
import java.util.*;

public class AddressDao implements AddressDaoInt {

    @Override
    public void add(Address address) {
        String sql = """
        INSERT INTO scheme1.addresses (town, street, house)
        VALUES (?, ?, ?)
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, address.getTown());
            ps.setString(2, address.getStreet());
            ps.setString(3, address.getHouse());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    address.setId(rs.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при добавлении адреса", e);
        }
    }

    @Override
    public Optional<Address> getById(Long id) {
        String sql = """
            SELECT * FROM scheme1.addresses
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
            throw new RuntimeException("Ошибка при получении адреса по ID", e);
        }
    }

    @Override
    public List<Address> getAll() {
        String sql = """
                SELECT * FROM scheme1.addresses
                WHERE is_active = true
            """;

        List<Address> addresses = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                addresses.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении списка адресов", e);
        }
        return addresses;
    }

    @Override
    public void update(Address address) {
        String sql = """
        UPDATE scheme1.addresses 
        SET town = ?, street = ?, house = ?, updated_at = CURRENT_TIMESTAMP
        WHERE id = ? AND is_active = true
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, address.getTown());
            ps.setString(2, address.getStreet());
            ps.setString(3, address.getHouse());
            ps.setLong(4, address.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при обновлении адреса", e);
        }
    }

    @Override
    public void delete(Long id) {
        String sql = """
                UPDATE scheme1.addresses SET is_active = false, updated_at = CURRENT_TIMESTAMP WHERE id = ?
                """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при soft delete адреса", e);
        }
    }

    private Address mapRow(ResultSet rs) throws SQLException {
        return new Address(
                rs.getLong("id"),
                rs.getString("town"),
                rs.getString("street"),
                rs.getString("house")
        );
    }
}