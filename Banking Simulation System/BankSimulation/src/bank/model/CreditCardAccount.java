package bank.model;

public class CreditCardAccount extends Account implements Rewardable {
    private static final double CREDIT_LIMIT = 200000.0;  // ₹2,00,000 credit limit
    private static final double CASHBACK_RATE = 0.02;     // 2% cashback
    
    public CreditCardAccount(String accountNumber, String username, double initialDebt) {
        super(accountNumber, username, initialDebt);
    }

    @Override
    public void withdraw(double amount) throws Exception {
        if (amount <= 0) throw new Exception("Purchase amount must be positive.");
        if (this.balance + amount > CREDIT_LIMIT) {
            throw new Exception("Credit limit exceeded. Max limit is ₹" + (int)CREDIT_LIMIT);
        }
        this.balance += amount;
        addTransaction("PURCHASE", amount);
        notifyObservers("PURCHASE", amount);
    }

    @Override
    public void deposit(double amount) throws Exception {
        if (amount <= 0) throw new Exception("Payment amount must be positive.");
        this.balance -= amount;
        addTransaction("PAYMENT", amount);
    }

    @Override
    public double calculateRewards(double transactionAmount) {
        return transactionAmount * CASHBACK_RATE;
    }

    @Override
    public String getAccountType() { return "Credit Card"; }
}
