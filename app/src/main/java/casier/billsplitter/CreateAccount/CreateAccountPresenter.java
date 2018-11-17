package casier.billsplitter.CreateAccount;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import casier.billsplitter.DAO;
import casier.billsplitter.FriendDataObserver;
import casier.billsplitter.Model.LocalUser;
import casier.billsplitter.Model.User;
import casier.billsplitter.Utils;

public class CreateAccountPresenter implements FriendDataObserver {

    private CreateAccountActivity createAccountActivity;
    private Utils mUtils;
    private DAO data;

    public CreateAccountPresenter(final CreateAccountActivity createAccountActivity){
        this.createAccountActivity = createAccountActivity;
        mUtils = Utils.getInstance();
        data = DAO.getInstance();
        data.registerFriendObserver(this);
    }

    public void createAccount(String accountName, List<User> userList) {
        userList.add(mUtils.getUserById(LocalUser.getInstance().getUserId()));
        List<String> userIdList = new ArrayList<>();
        for(User u : userList)
            userIdList.add(u.getUserId());
        data.createAccount(accountName, userIdList);
    }

    public List<User> getUserFriendList(){
        LocalUser localUser = LocalUser.getInstance();
        List<User> friendList = new ArrayList<>();
        for(User u : data.userList){
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

    @Override
    public void onFriendDataChange(List<String> friendList) {
        List<User> newFriendList = new ArrayList<>();
        for(String s : friendList)
            newFriendList.add(mUtils.getUserById(s));

        createAccountActivity.updateAdapter(newFriendList);
    }
}
