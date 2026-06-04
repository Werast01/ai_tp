package ru.kpr.kyrs.Dao.Interfaces;

import ru.kpr.kyrs.Pogo.ClientRequest;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ClientRequestDaoInt {
    void add(ClientRequest clientRequest);
    Optional<ClientRequest> getById(Long id);
    List<ClientRequest> getAll();
    void update(ClientRequest clientRequest);
    void delete(Long id);

    List<ClientRequest> getByClientId(Long clientId);
    List<ClientRequest> getByExecutionDate(LocalDate executionDate);
    List<ClientRequest> getByDateRange(LocalDate startDate, LocalDate endDate);
}