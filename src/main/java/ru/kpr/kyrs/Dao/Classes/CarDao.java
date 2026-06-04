package ru.kpr.kyrs.Dao.Classes;

import ru.kpr.kyrs.Dao.Interfaces.CarDaoInt;
import ru.kpr.kyrs.Pogo.Car;
import ru.kpr.kyrs.Pogo.CarCondition;
import ru.kpr.kyrs.Utilites.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CarDao implements CarDaoInt {

    @Override
    public void add(Car car) {
        String sql = """
        INSERT INTO scheme1.cars (marka, model, "gosNumber", "yearOfManufacture", "vinNumber", insurance, "conditionId", capacity, "fielNorm")
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, car.getMarka());
            ps.setString(2, car.getModel());
            ps.setString(3, car.getGosNumber());
            ps.setObject(4, car.getYearOfManufacture());
            ps.setString(5, car.getVinNumber());
            ps.setString(6, car.getInsurance());
            ps.setObject(7, car.getConditionId() != null ? car.getConditionId().getId() : null);
            ps.setObject(8, car.getCapacity());
            ps.setObject(9, car.getFuelNorm());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    car.setId(rs.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при добавлении автомобиля", e);
        }
    }

    @Override
    public Optional<Car> getById(Long id) {
        String sql = """
            SELECT c.*, cc.id as "conditionId", cc."conditionName" 
            FROM scheme1.cars c 
            LEFT JOIN scheme1.car_conditions cc ON c."conditionId" = cc.id 
            WHERE c.id = ? AND c.is_active = true
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
            throw new RuntimeException("Ошибка при получении автомобиля по ID", e);
        }
    }

    @Override
    public List<Car> getAll() {
        String sql = """
                SELECT c.*, cc.id as "conditionId", cc."conditionName"\s
                            FROM scheme1.cars c\s
                            LEFT JOIN scheme1.car_conditions cc ON c."conditionId" = cc.id\s
                            WHERE c.is_active = true\s
                            ORDER BY c.id
            """;

        List<Car> cars = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                cars.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении списка автомобилей", e);
        }
        return cars;
    }

    @Override
    public void update(Car car) {
        String sql = """
        UPDATE scheme1.cars 
        SET marka = ?, model = ?, "gosNumber" = ?, "yearOfManufacture" = ?, 
            "vinNumber" = ?, insurance = ?, "conditionId" = ?, 
            capacity = ?,updated_at = CURRENT_TIMESTAMP, "fuelNorm" = ?
        WHERE id = ? AND is_active = true
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, car.getMarka());
            ps.setString(2, car.getModel());
            ps.setString(3, car.getGosNumber());
            ps.setObject(4, car.getYearOfManufacture());
            ps.setString(5, car.getVinNumber());
            ps.setString(6, car.getInsurance());
            ps.setObject(7, car.getConditionId() != null ? car.getConditionId().getId() : null);
            ps.setObject(8, car.getCapacity());
            ps.setObject(9, car.getFuelNorm());
            ps.setLong(10, car.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при обновлении автомобиля", e);
        }
    }

    @Override
    public void delete(Long id) {
        String sql = "UPDATE cars SET is_active = false, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при soft delete автомобиля", e);
        }
    }

    private Car mapRow(ResultSet rs) throws SQLException {
        Integer conditionIdInt = rs.getObject("conditionId", Integer.class);

        Car car = new Car(
                rs.getLong("id"),
                rs.getString("marka"),
                rs.getString("model"),
                rs.getString("gosNumber"),
                rs.getObject("yearOfManufacture", Integer.class),
                rs.getString("vinNumber"),
                rs.getString("insurance"),
                null,  // временно передаём null
                rs.getInt("capacity")
        );
        Object fuelNormVal = rs.getObject("fuelNorm");
        car.setFuelNorm(fuelNormVal != null ? ((Number) fuelNormVal).doubleValue() : null);
        // Привязываем состояние автомобиля (если есть)
        if (conditionIdInt != null) {
            CarCondition condition = new CarCondition(conditionIdInt.longValue(), rs.getString("conditionName"));
            car.setConditionId(condition);
        }

        return car;
    }
}