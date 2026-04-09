package bank.model;

import java.io.Serializable;

public class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;
    private String accountNumber;
    private String type;
    private double amount;
    private long timestamp;

    public Transaction(String accountNumber, String type, double amount, long timestamp) {
        this.accountNumber = accountNumber;
        this.type = type;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public String getAccountNumber() { return accountNumber; }
    public String getType() { return type; }
    public double getAmount() { return amount; }
    public long getTimestamp() { return timestamp; }
}
