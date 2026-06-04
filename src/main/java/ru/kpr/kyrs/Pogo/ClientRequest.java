package ru.kpr.kyrs.Pogo;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ClientRequest {
    private Long id;
    private LocalDateTime createdAt;
    private LocalDate executionDate;
    private Client clientId;
    private Integer numberOfPersons;
    private Address startPoint;
    private Address endPoint;


    public ClientRequest(Long id, LocalDateTime createdAt, LocalDate executionDate, Client clientId, Integer numberOfPersons, Address startPoint, Address endPoint) {
        this.id = id;
        this.createdAt = createdAt;
        this.executionDate = executionDate;
        this.clientId = clientId;
        this.numberOfPersons = numberOfPersons;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDate getExecutionDate() { return executionDate; }
    public void setExecutionDate(LocalDate executionDate) { this.executionDate = executionDate; }
    public Integer getNumberOfPersons() { return numberOfPersons; }
    public void setNumberOfPersons(Integer numberOfPersons) { this.numberOfPersons = numberOfPersons; }
    public Client getClientId() {
        return clientId;
    }
    public void setClientId(Client clientId) {
        this.clientId = clientId;
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
}