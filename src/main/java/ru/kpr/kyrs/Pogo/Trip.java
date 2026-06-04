package ru.kpr.kyrs.Pogo;

import java.time.LocalDateTime;

public class Trip {
    private Long id;
    private Driver driverId;
    private Car carId;
    private Address startPoint;
    private Address endPoint;
    private Integer fuelBefore;
    private Integer fuelAfter;
    private LocalDateTime startDatetime;
    private LocalDateTime endDatetime;
    private Integer mileageBefore;
    private Integer mileageAfter;

    public Trip(Long id, Driver driverId, Car carId, Address startPoint,
                Address endPoint, Integer fuelBefore, Integer fuelAfter,
                LocalDateTime startDatetime, LocalDateTime endDatetime,
                Integer mileageBefore, Integer mileageAfter) {
        this.id = id;
        this.driverId = driverId;
        this.carId = carId;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.fuelBefore = fuelBefore;
        this.fuelAfter = fuelAfter;
        this.startDatetime = startDatetime;
        this.endDatetime = endDatetime;
        this.mileageBefore = mileageBefore;
        this.mileageAfter = mileageAfter;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Driver getDriverId() {
        return driverId;
    }

    public void setDriverId(Driver driverId) {
        this.driverId = driverId;
    }

    public Car getCarId() {
        return carId;
    }

    public void setCarId(Car carId) {
        this.carId = carId;
    }

    public Address getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(Address startPoint) {
        this.startPoint = startPoint;
    }

    public Address getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(Address endPoint) {
        this.endPoint = endPoint;
    }

    public Integer getFuelBefore() { return fuelBefore; }
    public void setFuelBefore(Integer fuelBefore) { this.fuelBefore = fuelBefore; }

    public Integer getFuelAfter() { return fuelAfter; }
    public void setFuelAfter(Integer fuelAfter) { this.fuelAfter = fuelAfter; }

    public LocalDateTime getStartDatetime() { return startDatetime; }
    public void setStartDatetime(LocalDateTime startDatetime) { this.startDatetime = startDatetime; }

    public LocalDateTime getEndDatetime() { return endDatetime; }
    public void setEndDatetime(LocalDateTime endDatetime) { this.endDatetime = endDatetime; }

    public Integer getMileageBefore() { return mileageBefore; }
    public void setMileageBefore(Integer mileageBefore) { this.mileageBefore = mileageBefore; }

    public Integer getMileageAfter() { return mileageAfter; }
    public void setMileageAfter(Integer mileageAfter) { this.mileageAfter = mileageAfter; }
}