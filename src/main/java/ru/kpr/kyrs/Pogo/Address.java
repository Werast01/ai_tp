package ru.kpr.kyrs.Pogo;

public class Address {
    private Long id;
    private String town;
    private String street;
    private String house;

    public Address(Long id, String town, String street, String house) {
        this.id = id;
        this.town = town;
        this.street = street;
        this.house = house;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTown() { return town; }
    public void setTown(String town) { this.town = town; }
    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }
    public String getHouse() { return house; }
    public void setHouse(String house) { this.house = house; }

    @Override
    public String toString() {
        return (town != null ? town : "") + ", " + (street != null ? street : "") + ", " + (house != null ? house : "");
    }
}
