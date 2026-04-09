package bank.model;

import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private String username;
    private String password;
    private double rewardPoints = 0;
    private boolean isAdmin = false;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
    public User(String username, String password, boolean isAdmin) {
        this.username = username;
        this.password = password;
        this.isAdmin = isAdmin;
    }

    public String getUsername() { return username; }
    public boolean checkPassword(String pwd) { return this.password.equals(pwd); }
    public boolean isAdmin() { return isAdmin; }
    
    public double getRewardPoints() { return rewardPoints; }
    public void addRewardPoints(double points) { this.rewardPoints += points; }
    public void redeemPoints(double points) throws Exception {
        if (points > rewardPoints) throw new Exception("Insufficient reward points.");
        this.rewardPoints -= points;
    }
}
