package casier.billsplitter.CreateAccount;

import java.util.List;

import casier.billsplitter.Model.User;
import casier.billsplitter.Utils;

public class CreateAccountPresenter {

    private CreateAccountActivity createAccountActivity;
    private Utils mUtils;

    public CreateAccountPresenter(final CreateAccountActivity createAccountActivity){
        this.createAccountActivity = createAccountActivity;
        mUtils = Utils.getInstance();
    }

    public void createAccount(String accountName, List<User> userList) {
        mUtils.createAccount(accountName, userList);
    }
}
