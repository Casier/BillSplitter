package casier.billsplitter;

import java.util.List;

import casier.billsplitter.Model.Account;

public interface AccountDataObserver {
    void onAccountDataChange(List<Account> accountList);
}
