package myApp.models;

public class Transaction {
    private String name;
    private double amount;
    private String description;
    private String category;
    private String bank;

    public Transaction(String name, double amount, String description, String category, String bank) {
        this.name = name;
        this.amount = amount;
        this.description = description;
        this.category = category;
        this.bank = bank;
    }

    public String getName() {
        return name;
    }

    public double getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public String getBank() {
        return bank;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "name='" + name + '\'' +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                ", category='" + category + '\'' +
                ", bank='" + bank + '\'' +
                '}';
    }
}
