package bank.service;

import bank.model.Account;
import java.util.*;

public class BankNetwork {
    private Map<String, BankService> banks = new LinkedHashMap<>();

    public void addBank(BankService bank) {
        banks.put(bank.getBankName(), bank);
    }

    public List<String> getBankNames() {
        return new ArrayList<>(banks.keySet());
    }

    public BankService getBank(String name) {
        return banks.get(name);
    }

    public void interBankTransfer(String fromBankName, String fromAccountNumber,
                                   String toBankName, String toAccountNumber,
                                   double amount) throws Exception {
        BankService fromBank = banks.get(fromBankName);
        BankService toBank = banks.get(toBankName);

        if (fromBank == null) throw new Exception("Source bank '" + fromBankName + "' not found.");
        if (toBank == null) throw new Exception("Destination bank '" + toBankName + "' not found.");

        Account fromAcc = fromBank.getAccount(fromAccountNumber);
        Account toAcc = toBank.getAccount(toAccountNumber);

        if (fromAcc == null) throw new Exception("Source account not found at " + fromBankName + ".");
        if (toAcc == null) throw new Exception("Destination account '" + toAccountNumber + "' not found at " + toBankName + ".");

        fromAcc.withdraw(amount);
        toAcc.deposit(amount);
    }
}
