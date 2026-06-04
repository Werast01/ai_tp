package ru.kpr.kyrs.Dao.Interfaces;
import ru.kpr.kyrs.Pogo.Client;

import java.util.List;
import java.util.Optional;

public interface ClientDaoInt {
    void add(Client client);
    Optional<Client> getById(Long id);
    List<Client> getAll();
    void update(Client client);
    void delete(Long id);
}
