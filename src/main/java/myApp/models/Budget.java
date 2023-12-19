package myApp.models;

import java.time.LocalDate;

public class Budget {
    private int id;
    private String category;
    private double allocatedAmount;
    private double spentAmount;
    private LocalDate startDate;
    private LocalDate endDate;

    // Constructor
    public Budget(int id, String category, double allocatedAmount, double spentAmount, LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.category = category;
        this.allocatedAmount = allocatedAmount;
        this.spentAmount = spentAmount;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public double calculatePercentage() {
        double value = this.getSpentAmount() / this.getAllocatedAmount();
        if (value >= 1) {
            return 1.0; // Return 1.0 directly if value is greater than or equal to 1
        } else {
            return value;
        }
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getAllocatedAmount() {
        return allocatedAmount;
    }

    public void setAllocatedAmount(double allocatedAmount) {
        this.allocatedAmount = allocatedAmount;
    }

    public double getSpentAmount() {
        return spentAmount;
    }

    public void setSpentAmount(double spentAmount) {
        this.spentAmount = spentAmount;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public double getRemainingAmount() {
        return allocatedAmount - spentAmount;
    }

    // toString method for debugging
    @Override
    public String toString() {
        return "Budget{" +
                "id=" + id +
                ", category='" + category + '\'' +
                ", allocatedAmount=" + allocatedAmount +
                ", spentAmount=" + spentAmount +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}
