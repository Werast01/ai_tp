package ru.kpr.kyrs.Pogo;

public class Client {
    private Long id;
    private String name;
    private String familya;
    private String lastName;
    private String phoneNumber;
    private String email;

    public Client(Long id, String name, String familya, String lastName, String phoneNumber, String email) {
        this.id = id;
        this.name = name;
        this.familya = familya;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getFamilya() { return familya; }
    public void setFamilya(String familya) { this.familya = familya; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public String toString() {
        String fio = (familya != null ? familya : "") + " " + (name != null ? name : "");
        if (lastName != null && !lastName.isBlank()) fio += " " + lastName;
        return fio.trim();
    }
}
