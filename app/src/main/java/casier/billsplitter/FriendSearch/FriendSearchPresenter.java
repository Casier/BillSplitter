package casier.billsplitter.FriendSearch;

import java.util.ArrayList;
import java.util.List;

import casier.billsplitter.Model.User;
import casier.billsplitter.Utils;

public class FriendSearchPresenter {

    private FriendSearchActivity activity;
    private Utils mUtils;
    private List<User> currentUserList;

    public FriendSearchPresenter(FriendSearchActivity activity){
        this.activity = activity;
        mUtils = Utils.getInstance();
    }

    public List<User> getUserList(){
        return mUtils.getUserList();
    }

    public void addFriend(String friendID){
        mUtils.addFriend(friendID);
    }

    public void sendInviteToFriend(String mail, String messageTitle, String messageBody){
        mUtils.sendInviteToFriend(mail, messageTitle, messageBody, activity);
    }

    public List<User> getFilteredUserList(String filter){
        List<User> filteredUserList = new ArrayList<>();
        for(User u : mUtils.getUserList()){
            if(u.getUserName().toLowerCase().contains(filter.toLowerCase()))
                filteredUserList.add(u);
        }

        this.currentUserList = filteredUserList;
        return filteredUserList;
    }

    public User getClickedUser(int position){
        return currentUserList.get(position);
    }
}
