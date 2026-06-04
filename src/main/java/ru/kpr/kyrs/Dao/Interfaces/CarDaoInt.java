package ru.kpr.kyrs.Dao.Interfaces;

import ru.kpr.kyrs.Pogo.Car;
import java.util.List;
import java.util.Optional;

public interface CarDaoInt {
    void add(Car car);
    Optional<Car> getById(Long id);
    List<Car> getAll();
    void update(Car car);
    void delete(Long id);
}