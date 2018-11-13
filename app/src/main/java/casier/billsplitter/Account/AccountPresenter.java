package casier.billsplitter.Account;

import casier.billsplitter.Utils;

public class AccountPresenter {

    private AccountActivity accountActivity;
    private Utils mUtils;

    public AccountPresenter(AccountActivity accountActivity){
        this.accountActivity = accountActivity;
        this.mUtils = Utils.getInstance();
    }

    public void setSelectedAccount(int position){
        mUtils.setSelectedAccount(mUtils.getAccountList().get(position));
    }
}
