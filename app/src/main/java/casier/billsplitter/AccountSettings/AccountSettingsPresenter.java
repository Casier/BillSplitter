package casier.billsplitter.AccountSettings;

import java.util.ArrayList;
import java.util.List;

import casier.billsplitter.DAO;
import casier.billsplitter.Model.User;
import casier.billsplitter.Utils;

public class AccountSettingsPresenter {

    private AccountSettingsActivity accountSettingsActivity;
    private Utils mUtils;
    private DAO data;

    public AccountSettingsPresenter(AccountSettingsActivity activity){
        this.accountSettingsActivity = activity;
        mUtils = Utils.getInstance();
        data = DAO.getInstance();
    }

    public void setInitialData(){
        List<User> accountUserList = new ArrayList<>();
        for(String s : mUtils.getSelectedAccount().getAccountUsers())
            accountUserList.add(mUtils.getUserById(s));

        accountSettingsActivity.setAccountName(mUtils.getSelectedAccount().getAccountName());
        accountSettingsActivity.setAccountUsers(accountUserList);
    }

    public void deleteAccount(){
        data.deleteAccount(mUtils.getSelectedAccount());
    }

    public void updateAccount(String accountName, List<User> pickedUsers){
        data.updateAccount(accountName, pickedUsers);
    }
}
