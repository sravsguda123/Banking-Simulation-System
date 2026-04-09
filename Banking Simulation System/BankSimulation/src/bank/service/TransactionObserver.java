package bank.service;

public interface TransactionObserver {
    void onTransaction(String accountNumber, String type, double amount);
}
