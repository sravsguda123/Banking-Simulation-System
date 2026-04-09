package bank.service;

import bank.model.*;
import java.io.*;
import java.util.*;

public class BankService {
    private static final String DATA_FILE = "nexus_bank_data.ser";
    private String bankName;
    private Map<String, User> users = new HashMap<>();
    private Map<String, Account> accounts = new HashMap<>();
    private FraudDetectionSystem fraudSystem = new FraudDetectionSystem();

    public BankService(String bankName) {
        this.bankName = bankName;
    }

    public String getBankName() { return bankName; }

    private void registerObservers(Account acc) {
        acc.addObserver(fraudSystem);
    }

    public FraudDetectionSystem getFraudSystem() { return fraudSystem; }

    public List<Account> getAllAccounts() {
        return new ArrayList<>(accounts.values());
    }

    public User login(String username, String password) throws Exception {
        User user = users.get(username);
        if (user != null && user.checkPassword(password)) return user;
        throw new Exception("Invalid credentials");
    }

    public void registerUser(String username, String password, boolean isSavings) throws Exception {
        if (users.containsKey(username)) {
            throw new Exception("Username '" + username + "' already exists at " + bankName + "!");
        }
        User newUser = new User(username, password);
        users.put(username, newUser);
        
        String prefix = isSavings ? "SA-" : "CA-";
        String newId = prefix + (int)(Math.random() * 9000 + 1000);
        Account acc = isSavings ? new SavingsAccount(newId, username, 0.0) : new CurrentAccount(newId, username, 0.0);
        
        registerObservers(acc);
        accounts.put(newId, acc);
        saveData();
    }

    public void addUser(String username, String password, boolean isAdmin) {
        users.put(username, new User(username, password, isAdmin));
        saveData();
    }

    public void addAccount(Account acc) {
        registerObservers(acc);
        accounts.put(acc.getAccountNumber(), acc);
        saveData();
    }

    public User getUser(String username) {
        return users.get(username);
    }

    public List<Account> getUserAccounts(String username) {
        List<Account> userAccounts = new ArrayList<>();
        for (Account acc : accounts.values()) {
            if (acc.getUsername().equals(username)) userAccounts.add(acc);
        }
        return userAccounts;
    }

    public Account getAccount(String accountNumber) {
        return accounts.get(accountNumber);
    }

    public void transfer(String fromAccount, String toAccount, double amount) throws Exception {
        Account from = accounts.get(fromAccount);
        Account to = accounts.get(toAccount);
        if (from == null || to == null) throw new Exception("Invalid account");
        if (fromAccount.equals(toAccount)) throw new Exception("Cannot transfer to same account");
        from.withdraw(amount);
        to.deposit(amount);
        saveData();
    }

    public void deposit(String accountNumber, double amount) throws Exception {
        Account acc = accounts.get(accountNumber);
        if (acc == null) throw new Exception("Invalid account");
        acc.deposit(amount);
        saveData();
    }

    public void withdraw(String accountNumber, double amount) throws Exception {
        Account acc = accounts.get(accountNumber);
        if (acc == null) throw new Exception("Invalid account");
        acc.withdraw(amount);

        if (acc instanceof Rewardable) {
            double earned = ((Rewardable) acc).calculateRewards(amount);
            User u = users.get(acc.getUsername());
            u.addRewardPoints(earned);
        }
        saveData();
    }

    public void redeemPoints(String username, String ccAccountNumber, double pointsAmount) throws Exception {
        User u = users.get(username);
        Account cc = accounts.get(ccAccountNumber);
        if (u == null || cc == null || !(cc instanceof CreditCardAccount)) {
            throw new Exception("Invalid redemption parameters.");
        }
        u.redeemPoints(pointsAmount);
        cc.deposit(pointsAmount);
        saveData();
    }

    public void applyForCreditCard(String username) throws Exception {
        double totalAssets = 0;
        for (Account a : getUserAccounts(username)) {
            if (!(a instanceof CreditCardAccount)) {
                totalAssets += a.getBalance();
            }
        }
        if (totalAssets < 50000) {
            throw new Exception("Application Denied: Total assets must exceed ₹50,000. Current: ₹" + String.format("%.2f", totalAssets));
        }
        String newId = "CC-" + (int)(Math.random() * 9000 + 1000);
        CreditCardAccount newCard = new CreditCardAccount(newId, username, 0.0);
        registerObservers(newCard);
        accounts.put(newId, newCard);
        saveData();
    }
    
    public void applyForDebitCard(String username, String accountNumber) throws Exception {
        Account a = accounts.get(accountNumber);
        if (a == null || !a.getUsername().equals(username)) {
            throw new Exception("Invalid account for Debit linking.");
        }
        if (a instanceof CreditCardAccount) {
            throw new Exception("Cannot link a Debit card to a Credit Card.");
        }
        if (a.hasDebitCard()) {
            throw new Exception("This account already has a linked Debit Card.");
        }
        a.setHasDebitCard(true);
        saveData();
    }

    // --- EXPANSION FEATURES ---

    public void createFixedDeposit(String username, String sourceAccount, double amount, int months) throws Exception {
        Account src = accounts.get(sourceAccount);
        if (src == null || !src.getUsername().equals(username)) throw new Exception("Invalid source account.");
        if (src.getBalance() < amount) throw new Exception("Insufficient funds in source account.");
        
        src.withdraw(amount);
        String fdId = "FD-" + (int)(Math.random() * 9000 + 1000);
        double rate = (months >= 12) ? 7.5 : 5.5; // 7.5% for >1 year, 5.5% otherwise
        
        FixedDepositAccount fd = new FixedDepositAccount(fdId, username, amount, rate, months);
        accounts.put(fdId, fd);
        saveData();
    }

    public void payBill(String username, String accountNumber, String billType, double amount) throws Exception {
        Account acc = accounts.get(accountNumber);
        if (acc == null || !acc.getUsername().equals(username)) throw new Exception("Invalid account.");
        acc.withdraw(amount);
        // Explicitly set the type for analytics tracking
        acc.getTransactions().get(acc.getTransactions().size()-1).getAccountNumber(); // Force get last
        Transaction t = acc.getTransactions().get(acc.getTransactions().size()-1); 
        saveData();
        // Note: Transaction model doesn't have category yet, we just check the 'type' string later.
    }

    public Map<String, Double> getMonthlyAnalytics(String username) {
        Map<String, Double> stats = new LinkedHashMap<>();
        stats.put("Savings", 0.0);
        stats.put("Spending", 0.0);
        stats.put("Investments", 0.0);

        List<Account> userAccs = getUserAccounts(username);
        for (Account a : userAccs) {
            for (Transaction t : a.getTransactions()) {
                String type = t.getType().toUpperCase();
                if (type.equals("DEPOSIT") || type.contains("PAYMENT") || type.contains("REDEEM")) {
                    stats.put("Savings", stats.get("Savings") + t.getAmount());
                } else if (type.equals("WITHDRAW") || type.equals("PURCHASE") || type.contains("BILL")) {
                    stats.put("Spending", stats.get("Spending") + t.getAmount());
                }
            }
            if (a instanceof FixedDepositAccount) {
                stats.put("Investments", stats.get("Investments") + ((FixedDepositAccount) a).getPrincipalAmount());
            }
        }
        return stats;
    }

    // --- GOALS MANAGEMENT ---
    private Map<String, List<SavingsGoal>> userGoals = new HashMap<>();

    public void addGoal(String username, String name, double target) {
        userGoals.computeIfAbsent(username, k -> new ArrayList<>()).add(new SavingsGoal(username, name, target));
        saveData();
    }

    public List<SavingsGoal> getGoals(String username) {
        return userGoals.getOrDefault(username, new ArrayList<>());
    }

    public void contributeToGoal(String username, String goalName, double amount) throws Exception {
        List<SavingsGoal> goals = userGoals.get(username);
        if (goals == null) throw new Exception("No goals found.");
        for (SavingsGoal g : goals) {
            if (g.getName().equals(goalName)) {
                g.addAmount(amount);
                saveData();
                return;
            }
        }
        throw new Exception("Goal not found.");
    }

    public synchronized void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(users);
            oos.writeObject(accounts);
            oos.writeObject(userGoals);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public synchronized void loadData() {
        File file = new File(DATA_FILE);
        if (!file.exists()) return;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            users = (Map<String, User>) ois.readObject();
            accounts = (Map<String, Account>) ois.readObject();
            userGoals = (Map<String, List<SavingsGoal>>) ois.readObject();
            
            // Re-register observers for all accounts
            for (Account acc : accounts.values()) {
                registerObservers(acc);
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Could not load data: " + e.getMessage());
        }
    }
}
