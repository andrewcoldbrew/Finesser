package myApp.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Bank {
    private String bankId;
    private double balance;
    private User owner;
    private String name;
    private List<Transaction> transactions;

    public Bank(String bankId, String name, User owner) {
        this.bankId = bankId;
        this.name = name;
        this.owner = owner;
        this.balance = 0.0;
        this.transactions = new ArrayList<>();
    }
    public String getBankId() {
        return bankId;
    }
    public double getBalance() {
        return balance;
    }
    public User getOwner() {
        return owner;
    }
    public List<Transaction> getTransactions() {
        return transactions;
    }
    public String getName() {return name; }
    public void setName(String name) {
        this.name = name;
    }

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
        this.balance += transaction.getAmount();
    }
    public Map<String, List<Transaction>> groupTransactionsByCategory() {
        Map<String, List<Transaction>> transactionsByCategory = new HashMap<>();

        for (Transaction transaction : transactions) {
            transactionsByCategory.computeIfAbsent(transaction.getCategory(), k -> new ArrayList<>()).add(transaction);
        }

        return transactionsByCategory;
    }
}

