package bank.model;

import java.io.Serializable;

public class SavingsGoal implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private double targetAmount;
    private double currentAmount;
    private String username;

    public SavingsGoal(String username, String name, double targetAmount) {
        this.username = username;
        this.name = name;
        this.targetAmount = targetAmount;
        this.currentAmount = 0;
    }

    public String getName() { return name; }
    public double getTargetAmount() { return targetAmount; }
    public double getCurrentAmount() { return currentAmount; }
    public String getUsername() { return username; }

    public void addAmount(double amount) {
        this.currentAmount += amount;
    }

    public double getProgress() {
        if (targetAmount == 0) return 0;
        return Math.min(1.0, currentAmount / targetAmount);
    }
}
