package ru.kpr.kyrs.Pogo;

public class Car {
    private Long id;
    private String marka;
    private String model;
    private String gosNumber;
    private Integer yearOfManufacture;
    private String vinNumber;
    private String insurance;
    private CarCondition conditionId;
    private Integer capacity;
    private Double fuelNorm; // нормативный расход топлива (л/100 км)

    public Car(Long id, String marka, String model, String gosNumber, Integer yearOfManufacture,
               String vinNumber, String insurance, CarCondition conditionId, Integer capacity) {
        this.id = id;
        this.marka = marka;
        this.model = model;
        this.gosNumber = gosNumber;
        this.yearOfManufacture = yearOfManufacture;
        this.vinNumber = vinNumber;
        this.insurance = insurance;
        this.conditionId = conditionId;
        this.capacity = capacity;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getMarka() { return marka; }
    public void setMarka(String marka) { this.marka = marka; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public String getGosNumber() { return gosNumber; }
    public void setGosNumber(String gosNumber) { this.gosNumber = gosNumber; }
    public Integer getYearOfManufacture() { return yearOfManufacture; }
    public void setYearOfManufacture(Integer yearOfManufacture) { this.yearOfManufacture = yearOfManufacture; }
    public String getVinNumber() { return vinNumber; }
    public void setVinNumber(String vinNumber) { this.vinNumber = vinNumber; }
    public String getInsurance() { return insurance; }
    public void setInsurance(String insurance) { this.insurance = insurance; }
    public CarCondition getConditionId() { return conditionId; }
    public void setConditionId(CarCondition conditionId) { this.conditionId = conditionId; }
    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }
    public Double getFuelNorm() {
        return fuelNorm;
    }
    public void setFuelNorm(Double fuelNorm) {
        this.fuelNorm = fuelNorm;
    }

    @Override
    public String toString() {
        return (marka != null ? marka : "") + " " + (model != null ? model : "")
                + (gosNumber != null ? " (" + gosNumber + ")" : "");
    }
}
