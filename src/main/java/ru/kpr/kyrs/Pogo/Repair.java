package ru.kpr.kyrs.Pogo;

import java.time.LocalDate;

public class Repair {
    private Long id;
    private Car carId;
    private Worker mechanicId;
    private LocalDate repairDate;
    private String result;


    public Repair(Long id, Car carId, Worker mechanicId, LocalDate repairDate, String result) {
        this.id = id;
        this.carId = carId;
        this.mechanicId = mechanicId;
        this.repairDate = repairDate;
        this.result = result;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Car getCarId() {
        return carId;
    }
    public void setCarId(Car carId) {
        this.carId = carId;
    }
    public Worker getMechanicId() {
        return mechanicId;
    }
    public void setMechanicId(Worker mechanicId) {
        this.mechanicId = mechanicId;
    }
    public LocalDate getRepairDate() { return repairDate; }
    public void setRepairDate(LocalDate repairDate) { this.repairDate = repairDate; }
    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }
}
