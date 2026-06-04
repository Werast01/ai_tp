package ru.kpr.kyrs.Pogo;

public class WorkerType {
    private Long id;
    private String position;

    public WorkerType(Long id, String position) {
        this.id = id;
        this.position = position;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    @Override
    public String toString() {
        return position != null ? position : "";
    }
}
