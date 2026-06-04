package ru.kpr.kyrs.Dao.Classes;

import ru.kpr.kyrs.Dao.Interfaces.*;
import ru.kpr.kyrs.Pogo.Driver;
import ru.kpr.kyrs.Pogo.Worker;
import ru.kpr.kyrs.Utilites.DatabaseConnection;

import java.sql.*;
import java.util.*;

public class DriverDao implements DriverDaoInt {
    private final WorkerDaoInt workerDao;


    public DriverDao(WorkerDaoInt workerDao) {
        this.workerDao = workerDao;
    }
    @Override
    public void add(Driver driver) {
        String sql = """
        INSERT INTO scheme1.drivers ("workerId", license)
        VALUES (?, ?)
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, driver.getWorkerId().getId());
            ps.setString(2, driver.getLicense());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    driver.setId(rs.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при добавлении водителя", e);
        }
    }

    @Override
    public Optional<Driver> getById(Long id) {
        String sql = """
        SELECT * 
        FROM scheme1.drivers
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
            throw new RuntimeException("Ошибка при получении водителя", e);
        }
    }

    @Override
    public List<Driver> getAll() {
        String sql = """
        SELECT * 
        FROM scheme1.drivers
        WHERE is_active = true
        ORDER BY id
    """;

        List<Driver> drivers = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                drivers.add(mapRow(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении водителей", e);
        }

        return drivers;
    }

    @Override
    public void update(Driver driver) {
        String sql = """
        UPDATE scheme1.drivers 
        SET "workerId" = ?, license = ?, updated_at = CURRENT_TIMESTAMP
        WHERE id = ? AND is_active = true
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, driver.getWorkerId().getId());
            ps.setString(2, driver.getLicense());
            ps.setLong(3, driver.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при обновлении водителя", e);
        }
    }

    @Override
    public void delete(Long id) {
        String sql = """
                UPDATE scheme1.drivers SET is_active = false, updated_at = CURRENT_TIMESTAMP WHERE id = ?
                """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при soft delete водителя", e);
        }
    }

    @Override
    public Optional<Driver> getByWorkerId(Long workerId) {
        String sql = """
            SELECT * FROM scheme1.drivers
            WHERE "workerId" = ? AND is_active = true
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, workerId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return Optional.of(mapRow(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении водителя по ID работника", e);
        }
    }

    @Override
    public Optional<Driver> getByLicense(String license) {
        String sql = """
            SELECT * FROM scheme1.drivers
            WHERE license = ? AND is_active = true
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, license);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return Optional.of(mapRow(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении водителя по номеру прав", e);
        }
    }

    private Driver mapRow(ResultSet rs) throws SQLException {
        Long workerId = rs.getLong("workerId");

        Worker worker = null;
        if (workerId != null && workerId > 0) {
            worker = workerDao.getById(workerId).orElse(null);
        }

        return new Driver(
                rs.getLong("id"),
                worker,
                rs.getString("license")
        );
    }
}