package ru.kpr.kyrs.Dao.Interfaces;

import ru.kpr.kyrs.Pogo.Repair;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RepairDaoInt {
    void add(Repair repair);
    Optional<Repair> getById(Long id);
    List<Repair> getAll();
    void update(Repair repair);
    void delete(Long id);
    

    /*List<Repair> getByCarId(Long carId);
    List<Repair> getByMechanicId(Long mechanicId);
    List<Repair> getByDateRange(LocalDate startDate, LocalDate endDate);
    List<Repair> getByCarIdAndDateRange(Long carId, LocalDate startDate, LocalDate endDate);*/
}