package myApp.models;

import java.time.LocalDate;

public class Transaction {
    private int transactionID;
    private String name;
    private double amount;
    private String description;
    private String category;
    private String bankName;
    private LocalDate date;
    private String recurrencePeriod;

    public Transaction(int transactionID, String name, double amount, String description, String category, String bankName, LocalDate date,String recurrencePeriod) {
        this.transactionID = transactionID;
        this.name = name;
        this.amount = amount;
        this.description = description;
        this.category = category;
        this.bankName = bankName;
        this.date = date;
        this.recurrencePeriod = recurrencePeriod;
    }



    public int getTransactionID() {
        return transactionID;
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

    public String getBankName() {
        return bankName;
    }

    public LocalDate getDate() {
        return date;
    }
    public String getRecurrencePeriod() {
        return recurrencePeriod;
    }

    public void setRecurrencePeriod(String recurrencePeriod) {
        this.recurrencePeriod = recurrencePeriod;
    }

    public String getDateString() {
        return date.toString();
    }
    @Override
    public String toString() {
        return "Transaction{" +
                "name='" + name + '\'' +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                ", category='" + category + '\'' +
                ", bankId='" + bankName + '\'' +
                ", date=" + date +
                '}';
    }
}
