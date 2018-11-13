package casier.billsplitter.AccountSettings;

import android.util.SparseBooleanArray;

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

    public void updateAccount(String accountName, SparseBooleanArray pickedUsers){
        List<User> billUsersList = new ArrayList<>();
        List<User> usersList = mUtils.getUserList();

        for(int i = 0 ; i < pickedUsers.size() ; i ++){
            if(pickedUsers.get(i)){
                billUsersList.add(usersList.get(i));
            }
        }
        mUtils.updateAccount(accountName, usersList);
    }
}
