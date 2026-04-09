package bank.service;

import java.util.ArrayList;
import java.util.List;

public class FraudDetectionSystem implements TransactionObserver {
    private List<String> fraudAlerts = new ArrayList<>();

    private static final double WITHDRAWAL_LIMIT = 100000.0;   // ₹1,00,000
    private static final double DEPOSIT_LIMIT = 5000000.0;     // ₹50,00,000

    @Override
    public void onTransaction(String accountNumber, String type, double amount) {
        String alert = null;
        if ((type.equals("WITHDRAW") || type.equals("PURCHASE")) && amount > WITHDRAWAL_LIMIT) {
            alert = "FRAUD ALERT: Suspicious " + type + " of ?" + String.format("%.2f", amount)
                    + " on account " + accountNumber + ". Amount exceeds ?" + WITHDRAWAL_LIMIT + " limit.";
        }
        if (type.equals("DEPOSIT") && amount > DEPOSIT_LIMIT) {
            alert = "FRAUD ALERT: Suspicious DEPOSIT of ?" + String.format("%.2f", amount)
                    + " on account " + accountNumber + ". Amount exceeds ?" + DEPOSIT_LIMIT + " limit.";
        }
        if (alert != null) {
            fraudAlerts.add(alert);
        }
    }

    public List<String> getFraudAlerts() { return fraudAlerts; }
    public int getAlertCount() { return fraudAlerts.size(); }

    public void clearAlerts() { fraudAlerts.clear(); }
}
