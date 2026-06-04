package ru.kpr.kyrs.Pogo;

public class CarCondition {
    private Long id;
    private String conditionName;

    public CarCondition(Long id, String conditionName) {
        this.id = id;
        this.conditionName = conditionName;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getConditionName() { return conditionName; }
    public void setConditionName(String conditionName) { this.conditionName = conditionName; }

    @Override
    public String toString() {
        return conditionName != null ? conditionName : "";
    }
}
