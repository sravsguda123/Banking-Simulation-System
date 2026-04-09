package bank.model;

public class CurrentAccount extends Account {
    private static final double OVERDRAFT_LIMIT = 10000.0;  // ₹10,000 overdraft
    private static final double TRANSACTION_FEE = 15.0;     // ₹15 fee
    
    public CurrentAccount(String accountNumber, String username, double initialBalance) {
        super(accountNumber, username, initialBalance);
    }

    public double getWithdrawalFee() { return TRANSACTION_FEE; }
    
    @Override
    public void withdraw(double amount) throws Exception {
        if (amount <= 0) throw new Exception("Withdrawal amount must be positive.");
        double totalDeduction = amount + TRANSACTION_FEE;
        if (this.balance - totalDeduction < -OVERDRAFT_LIMIT) {
            throw new Exception("Overdraft limit of ₹" + (int)OVERDRAFT_LIMIT + " exceeded (including ₹15 fee).");
        }
        this.balance -= totalDeduction;
        addTransaction("WITHDRAW", amount);
        if (TRANSACTION_FEE > 0) addTransaction("FEE", TRANSACTION_FEE);
        notifyObservers("WITHDRAW", amount);
    }

    @Override
    public String getAccountType() { return "Current"; }
}
