package ru.kpr.kyrs.Dao.Classes;

import ru.kpr.kyrs.Dao.Interfaces.WorkerDaoInt;
import ru.kpr.kyrs.Pogo.Worker;
import ru.kpr.kyrs.Pogo.WorkerType;
import ru.kpr.kyrs.Utilites.DatabaseConnection;

import java.sql.*;
import java.util.*;

public class WorkerDao implements WorkerDaoInt {

    @Override
    public void add(Worker worker) {
        String sql = """
        INSERT INTO scheme1.workers (name, familya, "lastName", "phoneNumber", email, "positionId")
        VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, worker.getName());
            ps.setString(2, worker.getFamilya());
            ps.setString(3, worker.getLastName());
            ps.setString(4, worker.getPhoneNumber());
            ps.setString(5, worker.getEmail());
            ps.setLong(6, worker.getPositionId().getId());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    worker.setId(rs.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при добавлении работника", e);
        }
    }

    @Override
    public Optional<Worker> getById(Long id) {
        String sql = """
            SELECT w.*, wt.id as worker_type_id, wt.position as worker_type_position
            FROM scheme1.workers w
            LEFT JOIN scheme1.worker_types wt ON w."positionId" = wt.id AND wt.is_active = true
            WHERE w.id = ? AND w.is_active = true
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
            throw new RuntimeException("Ошибка при получении работника по ID", e);
        }
    }

    @Override
    public List<Worker> getAll() {
        String sql = """
            SELECT w.*, wt.id as worker_type_id, wt.position as worker_type_position
            FROM scheme1.workers w
            LEFT JOIN scheme1.worker_types wt ON w."positionId" = wt.id AND wt.is_active = true
            WHERE w.is_active = true
            ORDER BY w.familya, w.name
            """;

        List<Worker> workers = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                workers.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении списка работников", e);
        }
        return workers;
    }

    @Override
    public void update(Worker worker) {
        String sql = """
        UPDATE scheme1.workers 
        SET name = ?, familya = ?, "lastName" = ?, "phoneNumber" = ?, 
            email = ?, "positionId" = ?, updated_at = CURRENT_TIMESTAMP
        WHERE id = ? AND is_active = true
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, worker.getName());
            ps.setString(2, worker.getFamilya());
            ps.setString(3, worker.getLastName());
            ps.setString(4, worker.getPhoneNumber());
            ps.setString(5, worker.getEmail());
            ps.setLong(6, worker.getPositionId().getId());
            ps.setLong(7, worker.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при обновлении работника", e);
        }
    }

    @Override
    public void delete(Long id) {
        String sql = """
                UPDATE scheme1.workers SET is_active = false, updated_at = CURRENT_TIMESTAMP WHERE id = ?
                """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при soft delete работника", e);
        }
    }

    @Override
    public List<Worker> getByPositionId(Long positionId) {
        String sql = """
            SELECT w.*, wt.id as worker_type_id, wt.position as worker_type_position
            FROM scheme1.workers w
            LEFT JOIN scheme1.worker_types wt ON w."positionId" = wt.id AND wt.is_active = true
            WHERE w."positionId" = ? AND w.is_active = true
            """;

        List<Worker> workers = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, positionId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                workers.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении работников по ID должности", e);
        }
        return workers;
    }

    @Override
    public Optional<Worker> getByPhoneNumber(String phoneNumber) {
        String sql = """
            SELECT w.*, wt.id as worker_type_id, wt.position as worker_type_position
            FROM scheme1.workers w
            LEFT JOIN scheme1.worker_types wt ON w."positionId" = wt.id AND wt.is_active = true
            WHERE w."phoneNumber" = ? AND w.is_active = true
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, phoneNumber);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return Optional.of(mapRow(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении работника по номеру телефона", e);
        }
    }

    @Override
    public Optional<Worker> getByEmail(String email) {
        String sql = """
            SELECT w.*, wt.id as worker_type_id, wt.position as worker_type_position
            FROM scheme1.workers w
            LEFT JOIN scheme1.worker_types wt ON w."positionId" = wt.id AND wt.is_active = true
            WHERE w.email = ? AND w.is_active = true
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return Optional.of(mapRow(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении работника по email", e);
        }
    }

    private Worker mapRow(ResultSet rs) throws SQLException {
        // Создаем объект WorkerType из данных JOIN
        WorkerType workerType = null;
        long workerTypeId = rs.getLong("worker_type_id");
        if (workerTypeId > 0) {
            workerType = new WorkerType(
                    workerTypeId,
                    rs.getString("worker_type_position")
            );
        }

        return new Worker(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("familya"),
                rs.getString("lastName"),
                rs.getString("phoneNumber"),
                rs.getString("email"),
                workerType
        );
    }
}