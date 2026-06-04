package ru.kpr.kyrs.Dao.Classes;

import ru.kpr.kyrs.Dao.Interfaces.*;
import ru.kpr.kyrs.Pogo.*;
import ru.kpr.kyrs.Pogo.Driver;
import ru.kpr.kyrs.Utilites.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class TripDao implements TripDaoInt {

    private final DriverDaoInt driverDao;
    private final CarDaoInt carDao;
    private final AddressDaoInt addressDao;

    public TripDao(DriverDaoInt driverDao, CarDaoInt carDao, AddressDaoInt addressDao) {
        this.driverDao = driverDao;
        this.carDao = carDao;
        this.addressDao = addressDao;

    }

    @Override
    public void add(Trip trip) {
        String sql = """
        INSERT INTO scheme1.trips ("driverId", "carId", "startPoint", "endPoint", 
                                   "fuelBefore", "fuelAfter", "startDatetime", "endDatetime",
                                   "mileageBefore", "mileageAfter")
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, trip.getDriverId().getId());
            ps.setLong(2, trip.getCarId().getId());
            ps.setObject(3, trip.getStartPoint() != null ? trip.getStartPoint().getId() : null);
            ps.setObject(4, trip.getEndPoint() != null ? trip.getEndPoint().getId() : null);
            ps.setObject(5, trip.getFuelBefore());
            ps.setObject(6, trip.getFuelAfter());
            ps.setObject(7, trip.getStartDatetime());
            ps.setObject(8, trip.getEndDatetime());
            ps.setObject(9, trip.getMileageBefore());
            ps.setObject(10, trip.getMileageAfter());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    trip.setId(rs.getLong(1));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при добавлении поездки", e);
        }
    }

    @Override
    public Optional<Trip> getById(Long id) {
        String sql = "SELECT * FROM scheme1.trips WHERE id = ? AND is_active = true";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return Optional.of(mapRow(rs));
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении поездки по ID", e);
        }
    }

    @Override
    public List<Trip> getAll() {
        String sql = "SELECT * FROM scheme1.trips WHERE is_active = true ORDER BY \"startDatetime\" DESC";

        List<Trip> trips = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                trips.add(mapRow(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении списка поездок", e);
        }

        return trips;
    }

    @Override
    public void update(Trip trip) {
        String sql = """
        UPDATE scheme1.trips 
        SET "driverId" = ?, "carId" = ?, "startPoint" = ?, "endPoint" = ?,
            "fuelBefore" = ?, "fuelAfter" = ?, "startDatetime" = ?, "endDatetime" = ?,
            "mileageBefore" = ?, "mileageAfter" = ?, updated_at = CURRENT_TIMESTAMP
        WHERE id = ? AND is_active = true
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, trip.getDriverId().getId());
            ps.setLong(2, trip.getCarId().getId());
            ps.setObject(3, trip.getStartPoint() != null ? trip.getStartPoint().getId() : null);
            ps.setObject(4, trip.getEndPoint() != null ? trip.getEndPoint().getId() : null);
            ps.setObject(5, trip.getFuelBefore());
            ps.setObject(6, trip.getFuelAfter());
            ps.setObject(7, trip.getStartDatetime());
            ps.setObject(8, trip.getEndDatetime());
            ps.setObject(9, trip.getMileageBefore());
            ps.setObject(10, trip.getMileageAfter());
            ps.setLong(11, trip.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при обновлении поездки", e);
        }
    }

    @Override
    public void delete(Long id) {
        String sql = """
        UPDATE scheme1.trips 
        SET is_active = false, updated_at = CURRENT_TIMESTAMP 
        WHERE id = ?
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при удалении поездки", e);
        }
    }

    @Override
    public List<Trip> getByDriverId(Long driverId) {
        List<Trip> result = new ArrayList<>();
        String sql = "SELECT * FROM scheme1.trips WHERE \"driverId\" = ? AND is_active = true";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, driverId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                result.add(mapRow(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении поездок по водителю", e);
        }

        return result;
    }

    @Override
    public List<Trip> getByCarId(Long carId) {
        List<Trip> result = new ArrayList<>();
        String sql = "SELECT * FROM scheme1.trips WHERE \"carId\" = ? AND is_active = true";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, carId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                result.add(mapRow(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении поездок по машине", e);
        }

        return result;
    }

    @Override
    public Optional<Trip> getLastTripByCarId(Long carId) {
        String sql = """
        SELECT * FROM scheme1.trips 
        WHERE "carId" = ? AND is_active = true
        ORDER BY "startDatetime" DESC
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
            throw new RuntimeException("Ошибка при получении последней поездки", e);
        }
    }

    // =========================
    // MAPPER (главное изменение)
    // =========================
    private Trip mapRow(ResultSet rs) throws SQLException {

        Long driverId = rs.getLong("driverId");
        Long carId = rs.getLong("carId");

        Long startId = rs.getObject("startPoint") != null ? rs.getLong("startPoint") : null;
        Long endId = rs.getObject("endPoint") != null ? rs.getLong("endPoint") : null;

        Driver driver = driverDao.getById(driverId).orElse(null);
        Car car = carDao.getById(carId).orElse(null);

        Address start = startId != null ? addressDao.getById(startId).orElse(null) : null;
        Address end = endId != null ? addressDao.getById(endId).orElse(null) : null;

        return new Trip(
                rs.getLong("id"),
                driver,
                car,
                start,
                end,
                rs.getObject("fuelBefore", Integer.class),
                rs.getObject("fuelAfter", Integer.class),
                rs.getObject("startDatetime", LocalDateTime.class),
                rs.getObject("endDatetime", LocalDateTime.class),
                rs.getObject("mileageBefore", Integer.class),
                rs.getObject("mileageAfter", Integer.class)
        );
    }
}