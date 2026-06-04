package ru.kpr.kyrs.Dao.Interfaces;

import ru.kpr.kyrs.Pogo.Worker;
import java.util.List;
import java.util.Optional;

public interface WorkerDaoInt {
    void add(Worker worker);
    Optional<Worker> getById(Long id);
    List<Worker> getAll();
    void update(Worker worker);
    void delete(Long id);
    
    // Дополнительные методы
    List<Worker> getByPositionId(Long positionId);
    Optional<Worker> getByPhoneNumber(String phoneNumber);
    Optional<Worker> getByEmail(String email);
}