package casier.billsplitter.AccountSettings;

import java.util.ArrayList;
import java.util.List;

import casier.billsplitter.Model.User;
import casier.billsplitter.Utils;

public class AccountSettingsPresenter {

    private AccountSettingsActivity accountSettingsActivity;
    private Utils mUtils;

    public AccountSettingsPresenter(AccountSettingsActivity activity){
        this.accountSettingsActivity = activity;
        mUtils = Utils.getInstance();
    }

    public void setInitialData(){
        List<User> accountUserList = new ArrayList<>();
        for(String s : mUtils.getSelectedAccount().getAccountUsers())
            accountUserList.add(mUtils.getUserById(s));

        accountSettingsActivity.setAccountName(mUtils.getSelectedAccount().getAccountName());
        accountSettingsActivity.setAccountUsers(accountUserList);
    }

    public void deleteAccount(){
        mUtils.deleteAccount(mUtils.getSelectedAccount());
    }
}
