package casier.billsplitter.CreateAccount;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import casier.billsplitter.Model.LocalUser;
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
        userList.add(mUtils.getUserById(LocalUser.getInstance().getUserId()));
        mUtils.createAccount(accountName, userList);
    }

    public List<User> getUserFriendList(){
        LocalUser localUser = LocalUser.getInstance();
        List<User> friendList = new ArrayList<>();
        for(User u : mUtils.getUserList()){
            if(!u.getUserId().equals(localUser.getUserId())
                    && localUser.getFriendList().contains(u.getUserId()))
                friendList.add(u);
        }

        return friendList;
    }

    public List<User> getFilteredUserFriendList(String filter){
        List<User> filteredFriendList = getUserFriendList();
        Iterator<User> iterator = filteredFriendList.iterator();
        while(iterator.hasNext()){
            User u = iterator.next();
            if(!u.getUserName().toLowerCase().contains(filter.toLowerCase()))
                iterator.remove();
        }
        return filteredFriendList;
    }
}
