package casier.billsplitter.Account;

import casier.billsplitter.DAO;
import casier.billsplitter.Utils;

public class AccountPresenter {

    private AccountActivity accountActivity;
    private Utils mUtils;
    private DAO data;

    public AccountPresenter(AccountActivity accountActivity){
        this.accountActivity = accountActivity;
        mUtils = Utils.getInstance();
        data = DAO.getInstance();
    }

    public void setSelectedAccount(int position){
        mUtils.setSelectedAccount(data.accountList.get(position));
    }
}
