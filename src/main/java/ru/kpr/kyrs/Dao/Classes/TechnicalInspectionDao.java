package ru.kpr.kyrs.Dao.Classes;

import ru.kpr.kyrs.Dao.Interfaces.TechnicalInspectionDaoInt;
import ru.kpr.kyrs.Dao.Interfaces.CarDaoInt;
import ru.kpr.kyrs.Dao.Interfaces.WorkerDaoInt;
import ru.kpr.kyrs.Pogo.Car;
import ru.kpr.kyrs.Pogo.TechnicalInspection;
import ru.kpr.kyrs.Pogo.Worker;
import ru.kpr.kyrs.Utilites.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class TechnicalInspectionDao implements TechnicalInspectionDaoInt {

    private final CarDaoInt carDao;
    private final WorkerDaoInt workerDao;

    public TechnicalInspectionDao(CarDaoInt carDao, WorkerDaoInt workerDao) {
        this.carDao = carDao;
        this.workerDao = workerDao;
    }

    @Override
    public void add(TechnicalInspection inspection) {
        String sql = """
            INSERT INTO scheme1.technical_inspections
            ("carId", "inspectionDate", result, "mechanicId")
            VALUES (?, ?, ?, ?)
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, inspection.getCarId().getId());
            ps.setObject(2, inspection.getInspectionDate());
            ps.setString(3, inspection.getResult());
            ps.setLong(4, inspection.getMechanicId().getId());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    inspection.setId(rs.getLong(1));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при добавлении техосмотра", e);
        }
    }

    @Override
    public Optional<TechnicalInspection> getById(Long id) {

        String sql = """
            SELECT *
            FROM scheme1.technical_inspections
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
            throw new RuntimeException("Ошибка при получении техосмотра", e);
        }
    }

    @Override
    public List<TechnicalInspection> getAll() {

        String sql = """
            SELECT *
            FROM scheme1.technical_inspections
            WHERE is_active = true
            ORDER BY "inspectionDate" DESC
        """;

        List<TechnicalInspection> list = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении техосмотров", e);
        }

        return list;
    }

    @Override
    public void update(TechnicalInspection inspection) {

        String sql = """
            UPDATE scheme1.technical_inspections
            SET "carId" = ?, "inspectionDate" = ?, result = ?, "mechanicId" = ?, updated_at = CURRENT_TIMESTAMP
            WHERE id = ? AND is_active = true
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, inspection.getCarId().getId());
            ps.setObject(2, inspection.getInspectionDate());
            ps.setString(3, inspection.getResult());
            ps.setLong(4, inspection.getMechanicId().getId());
            ps.setLong(5, inspection.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при обновлении техосмотра", e);
        }
    }

    @Override
    public void delete(Long id) {

        String sql = """
            UPDATE scheme1.technical_inspections
            SET is_active = false, updated_at = CURRENT_TIMESTAMP
            WHERE id = ?
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при удалении техосмотра", e);
        }
    }

    // ===================== FILTERS =====================

    @Override
    public List<TechnicalInspection> getByCarId(Long carId) {

        String sql = """
            SELECT *
            FROM scheme1.technical_inspections
            WHERE "carId" = ? AND is_active = true
        """;

        List<TechnicalInspection> list = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, carId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении техосмотров по авто", e);
        }

        return list;
    }

    @Override
    public List<TechnicalInspection> getByMechanicId(Long mechanicId) {

        String sql = """
            SELECT *
            FROM scheme1.technical_inspections
            WHERE "mechanicId" = ? AND is_active = true
        """;

        List<TechnicalInspection> list = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, mechanicId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении техосмотров по механику", e);
        }

        return list;
    }

    @Override
    public List<TechnicalInspection> getByDateRange(LocalDate start, LocalDate end) {

        String sql = """
            SELECT *
            FROM scheme1.technical_inspections
            WHERE "inspectionDate" BETWEEN ? AND ?
            AND is_active = true
        """;

        List<TechnicalInspection> list = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, start);
            ps.setObject(2, end);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении техосмотров по датам", e);
        }

        return list;
    }

    @Override
    public Optional<TechnicalInspection> getLatestByCarId(Long carId) {

        String sql = """
            SELECT *
            FROM scheme1.technical_inspections
            WHERE "carId" = ? AND is_active = true
            ORDER BY "inspectionDate" DESC
            LIMIT 1
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, carId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return Optional.of(mapRow(rs));
            }

            return Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении последнего техосмотра", e);
        }
    }

    // ===================== MAP =====================

    private TechnicalInspection mapRow(ResultSet rs) throws SQLException {

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

        return new TechnicalInspection(
                rs.getLong("id"),
                car,
                rs.getObject("inspectionDate", LocalDate.class),
                rs.getString("result"),
                mechanic
        );
    }
}