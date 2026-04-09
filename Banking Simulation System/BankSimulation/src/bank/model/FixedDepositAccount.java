package bank.model;

import java.util.Date;
import java.util.Calendar;

public class FixedDepositAccount extends Account {
    private double interestRate;
    private Date maturityDate;
    private double principalAmount;

    public FixedDepositAccount(String accountNumber, String username, double amount, double interestRate, int months) {
        super(accountNumber, username, amount);
        this.principalAmount = amount;
        this.interestRate = interestRate;
        
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, months);
        this.maturityDate = cal.getTime();
    }

    @Override
    public void withdraw(double amount) throws Exception {
        if (new Date().before(maturityDate)) {
            throw new Exception("Premature Withdrawal Error: FD matures on " + maturityDate + ". Early withdrawal is not permitted in this simulation.");
        }
        if (amount > balance) throw new Exception("Insufficient funds in FD.");
        this.balance -= amount;
        addTransaction("FD_WITHDRAWAL", amount);
        notifyObservers("FD_WITHDRAWAL", amount);
    }

    @Override
    public void deposit(double amount) throws Exception {
        throw new Exception("Fixed Deposits do not accept additional deposits. Please open a new FD.");
    }

    public double getInterestRate() { return interestRate; }
    public Date getMaturityDate() { return maturityDate; }
    public double getPrincipalAmount() { return principalAmount; }

    @Override
    public String getAccountType() { return "Fixed Deposit"; }
}
