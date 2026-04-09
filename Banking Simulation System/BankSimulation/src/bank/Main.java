package bank;

import bank.service.BankService;
import bank.model.*;
import bank.ui.WelcomeFrame;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BankService bank = new BankService("Nexus Bank");
            bank.loadData();
            
            // Seed data if empty (first run)
            if (bank.getUser("testuser") == null) {
                bank.addUser("admin", "admin123", true);
                bank.addUser("testuser", "password123", false);
                try {
                    bank.addAccount(new SavingsAccount("SA-1001", "testuser", 150000.0));
                    bank.addAccount(new CurrentAccount("CA-2001", "testuser", 75000.0));
                    bank.addAccount(new CreditCardAccount("CC-8080", "testuser", 12000.0));
                } catch(Exception e) {}
            }

            new WelcomeFrame(bank).setVisible(true);
        });
    }
}
