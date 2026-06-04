package ru.kpr.kyrs.Dao.Interfaces;

import ru.kpr.kyrs.Pogo.Trip;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TripDaoInt {
    void add(Trip trip);
    Optional<Trip> getById(Long id);
    List<Trip> getAll();
    void update(Trip trip);
    void delete(Long id);
    
    // Дополнительные методы
    List<Trip> getByDriverId(Long driverId);
    List<Trip> getByCarId(Long carId);
    Optional<Trip> getLastTripByCarId(Long carId);
}