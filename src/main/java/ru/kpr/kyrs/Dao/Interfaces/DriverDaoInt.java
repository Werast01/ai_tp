package ru.kpr.kyrs.Dao.Interfaces;

import ru.kpr.kyrs.Pogo.Driver;
import java.util.List;
import java.util.Optional;

public interface DriverDaoInt {
    void add(Driver driver);
    Optional<Driver> getById(Long id);
    List<Driver> getAll();
    void update(Driver driver);
    void delete(Long id);

    // Дополнительные методы
    Optional<Driver> getByWorkerId(Long workerId);
    Optional<Driver> getByLicense(String license);
}