package ru.kpr.kyrs.Pogo;

public class Driver {
    private Long id;
    private Worker workerId;
    private String license;

    public Driver(Long id, Worker workerId, String license) {
        this.id = id;
        this.workerId = workerId;
        this.license = license;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Worker getWorkerId() { return workerId; }
    public void setWorkerId(Worker workerId) { this.workerId = workerId; }
    public String getLicense() { return license; }
    public void setLicense(String license) { this.license = license; }

    @Override
    public String toString() {
        if (workerId != null) {
            return workerId.getFamilya() + " " + workerId.getName()
                    + (license != null ? " [" + license + "]" : "");
        }
        return license != null ? license : "Водитель #" + id;
    }
}
