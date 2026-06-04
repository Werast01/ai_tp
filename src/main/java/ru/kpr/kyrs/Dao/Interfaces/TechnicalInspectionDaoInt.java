package ru.kpr.kyrs.Dao.Interfaces;

import ru.kpr.kyrs.Pogo.TechnicalInspection;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TechnicalInspectionDaoInt {
    void add(TechnicalInspection inspection);
    Optional<TechnicalInspection> getById(Long id);
    List<TechnicalInspection> getAll();
    void update(TechnicalInspection inspection);
    void delete(Long id);
    
    // Дополнительные методы
    List<TechnicalInspection> getByCarId(Long carId);
    List<TechnicalInspection> getByMechanicId(Long mechanicId);
    List<TechnicalInspection> getByDateRange(LocalDate startDate, LocalDate endDate);
    Optional<TechnicalInspection> getLatestByCarId(Long carId);
}