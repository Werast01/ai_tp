package ru.kpr.kyrs.Dao.Interfaces;

import ru.kpr.kyrs.Pogo.Address;
import java.util.List;
import java.util.Optional;

public interface AddressDaoInt {
    void add(Address address);
    Optional<Address> getById(Long id);
    List<Address> getAll();
    void update(Address address);
    void delete(Long id);
}