package bank.model;

import bank.service.TransactionObserver;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class Account implements Serializable {
    private static final long serialVersionUID = 1L;
    private String accountNumber;
    protected String username;
    protected double balance;
    protected boolean hasDebitCard = false;
    private List<Transaction> transactions;
    private transient List<TransactionObserver> observers = new ArrayList<>();

    public Account(String accountNumber, String username, double initialBalance) {
        this.accountNumber = accountNumber;
        this.username = username;
        this.balance = initialBalance;
        this.transactions = new ArrayList<>();
        if (initialBalance > 0) {
            addTransaction("INITIAL_DEPOSIT", initialBalance);
        } else {
            addTransaction("ACCOUNT_OPENED", 0.0);
        }
    }

    public String getAccountNumber() { return accountNumber; }
    public String getUsername() { return username; }
    public double getBalance() { return balance; }
    public boolean hasDebitCard() { return hasDebitCard; }
    public void setHasDebitCard(boolean hasCard) { this.hasDebitCard = hasCard; }

    public void addObserver(TransactionObserver obs) { 
        if (observers == null) observers = new ArrayList<>();
        observers.add(obs); 
    }

    public void deposit(double amount) throws Exception {
        if (amount <= 0) throw new Exception("Deposit amount must be positive.");
        this.balance += amount;
        addTransaction("DEPOSIT", amount);
        notifyObservers("DEPOSIT", amount);
    }
    
    public abstract void withdraw(double amount) throws Exception;

    protected void addTransaction(String type, double amount) {
        Transaction t = new Transaction(this.accountNumber, type, amount, System.currentTimeMillis());
        transactions.add(t);
    }

    protected void notifyObservers(String type, double amount) {
        if (observers == null) return;
        for (TransactionObserver obs : observers) {
            obs.onTransaction(this.accountNumber, type, amount);
        }
    }
    
    public List<Transaction> getTransactions() { return transactions; }
    public abstract String getAccountType();
}
