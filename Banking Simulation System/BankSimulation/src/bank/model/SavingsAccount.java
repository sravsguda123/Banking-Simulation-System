package bank.model;

public class SavingsAccount extends Account {
    private static final double MIN_BALANCE = 500.0;  // ₹500 minimum balance
    private static final double ANNUAL_INTEREST_RATE = 0.04; // 4% APY
    
    public SavingsAccount(String accountNumber, String username, double initialBalance) {
        super(accountNumber, username, initialBalance);
    }

    public double getInterestRate() { return ANNUAL_INTEREST_RATE; }
    
    @Override
    public void withdraw(double amount) throws Exception {
        if (amount <= 0) throw new Exception("Withdrawal amount must be positive.");
        if (this.balance - amount < MIN_BALANCE) {
            throw new Exception("Insufficient funds. Minimum balance of ₹" + (int)MIN_BALANCE + " required.");
        }
        this.balance -= amount;
        addTransaction("WITHDRAW", amount);
        notifyObservers("WITHDRAW", amount);
    }

    @Override
    public String getAccountType() { return "Savings"; }
}
