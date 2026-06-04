package ru.kpr.kyrs.Dao.Classes;

import ru.kpr.kyrs.Dao.Interfaces.RepairDaoInt;
import ru.kpr.kyrs.Dao.Interfaces.CarDaoInt;
import ru.kpr.kyrs.Dao.Interfaces.WorkerDaoInt;
import ru.kpr.kyrs.Pogo.Car;
import ru.kpr.kyrs.Pogo.Repair;
import ru.kpr.kyrs.Pogo.Worker;
import ru.kpr.kyrs.Utilites.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class RepairDao implements RepairDaoInt {

    private final CarDaoInt carDao;
    private final WorkerDaoInt workerDao;

    public RepairDao(CarDaoInt carDao, WorkerDaoInt workerDao) {
        this.carDao = carDao;
        this.workerDao = workerDao;
    }

    @Override
    public void add(Repair repair) {
        String sql = """
            INSERT INTO scheme1.repairs ("carId", "mechanicId", "repairDate", result)
            VALUES (?, ?, ?, ?)
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, repair.getCarId().getId());
            ps.setLong(2, repair.getMechanicId().getId());
            ps.setObject(3, repair.getRepairDate());
            ps.setString(4, repair.getResult());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    repair.setId(rs.getLong(1));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при добавлении ремонта", e);
        }
    }

    @Override
    public Optional<Repair> getById(Long id) {
        String sql = """
            SELECT *
            FROM scheme1.repairs
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
            throw new RuntimeException("Ошибка при получении ремонта", e);
        }
    }


    @Override
    public List<Repair> getAll() {
        String sql = """
            SELECT *
            FROM scheme1.repairs
            WHERE is_active = true
            ORDER BY "repairDate" DESC
        """;

        List<Repair> list = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении ремонтов", e);
        }

        return list;
    }

    @Override
    public void update(Repair repair) {
        String sql = """
            UPDATE scheme1.repairs
            SET "carId" = ?, "mechanicId" = ?, "repairDate" = ?, result = ?, updated_at = CURRENT_TIMESTAMP
            WHERE id = ? AND is_active = true
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, repair.getCarId().getId());
            ps.setLong(2, repair.getMechanicId().getId());
            ps.setObject(3, repair.getRepairDate());
            ps.setString(4, repair.getResult());
            ps.setLong(5, repair.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при обновлении ремонта", e);
        }
    }

    @Override
    public void delete(Long id) {
        String sql = """
            UPDATE scheme1.repairs
            SET is_active = false, updated_at = CURRENT_TIMESTAMP
            WHERE id = ?
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при удалении ремонта", e);
        }
    }

    private Repair mapRow(ResultSet rs) throws SQLException {

        Long carId = rs.getLong("carId");
        Long mechanicId = rs.getLong("mechanicId");

        Car car = null;
        if (carId != null && carId > 0) {
            car = carDao.getById(carId).orElse(null);
        }

        Worker mechanic = null;
        if (mechanicId != null && mechanicId > 0) {
            mechanic = workerDao.getById(mechanicId).orElse(null);
        }

        return new Repair(
                rs.getLong("id"),
                car,
                mechanic,
                rs.getObject("repairDate", LocalDate.class),
                rs.getString("result")
        );
    }
}