package ru.kpr.kyrs.Pogo;

import java.time.LocalDate;

public class TechnicalInspection {
    private Long id;
    private Car carId;
    private LocalDate inspectionDate;
    private String result;
    private Worker mechanicId;


    public TechnicalInspection(Long id, Car carId, LocalDate inspectionDate, String result, Worker mechanicId) {
        this.id = id;
        this.carId = carId;
        this.inspectionDate = inspectionDate;
        this.result = result;
        this.mechanicId = mechanicId;
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

    public LocalDate getInspectionDate() { return inspectionDate; }
    public void setInspectionDate(LocalDate inspectionDate) { this.inspectionDate = inspectionDate; }

    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }
}
