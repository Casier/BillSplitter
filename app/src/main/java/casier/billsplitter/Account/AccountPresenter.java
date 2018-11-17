package casier.billsplitter.Account;

import java.util.List;

import casier.billsplitter.AccountDataObserver;
import casier.billsplitter.DAO;
import casier.billsplitter.Model.Account;
import casier.billsplitter.Utils;

public class AccountPresenter implements AccountDataObserver {

    private AccountActivity activity;
    private Utils mUtils;
    private DAO data;

    public AccountPresenter(AccountActivity accountActivity){
        this.activity = accountActivity;
        mUtils = Utils.getInstance();
        data = DAO.getInstance();
        data.registerAccountObserver(this);
    }

    public void setSelectedAccount(int position){
        mUtils.setSelectedAccount(data.accountList.get(position));
    }

    public void clear(){
        data.removeAccountObserver(this);
    }

    public List<Account> getAccountList(){
        return data.accountList;
    }

    @Override
    public void onAccountDataChange(List<Account> accountList) {
        activity.updateAdapter(accountList);
    }
}
