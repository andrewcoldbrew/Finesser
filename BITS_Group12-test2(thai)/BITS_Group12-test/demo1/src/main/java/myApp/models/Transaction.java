package myApp.models;

public class Transaction {
    private String name;
    private double amount;
    private String description;
    private String category;

    private String bank;
//    private enum BANK {
//        TPBANK,
//        ACB,
//        VIB,
//        MB,
//        TECHCOMBANK,
//        VIETCOMBANK
//    }


    public Transaction(String name, double amount, String category) {
        this.name = name;
        this.amount = amount;
        this.category = category;
    }
    public String getName() {
        return name;
    }
    public double getAmount() {
        return amount;
    }
    public String getCategory() {
        return category;
    }
    @Override
    public String toString() {
        return "Transaction{" +
                "name='" + name + '\'' +
                ", amount=" + amount +
                ", category='" + category + '\'' +
                '}';
    }
}

