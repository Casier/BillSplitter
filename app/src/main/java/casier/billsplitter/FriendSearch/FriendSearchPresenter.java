package casier.billsplitter.FriendSearch;

import java.util.ArrayList;
import java.util.List;

import casier.billsplitter.DAO;
import casier.billsplitter.FriendDataObserver;
import casier.billsplitter.Model.LocalUser;
import casier.billsplitter.Model.User;
import casier.billsplitter.Utils;

public class FriendSearchPresenter implements FriendDataObserver{

    private FriendSearchActivity activity;
    private Utils mUtils;
    private DAO data;

    public FriendSearchPresenter(FriendSearchActivity activity){
        this.activity = activity;
        mUtils = Utils.getInstance();
        data = DAO.getInstance();
        data.registerFriendObserver(this);
    }

    public List<User> getUserList(){
        LocalUser localUser = LocalUser.getInstance();
        List<User> userList = new ArrayList<>();
        for(User u : data.userList){
            if(!localUser.getFriendList().contains(u.getUserId())
                    && !localUser.getUserId().equals(u.getUserId())
                    && !userList.contains(u)){
                userList.add(u);
            }
        }
        return userList;
    }

    public void addFriend(String friendID){
        LocalUser.getInstance().addFriend(friendID);
        mUtils.addFriend(friendID);
    }

    public void sendInviteToFriend(){
        mUtils.sendInviteToFriend(activity);
    }

    public List<User> getFilteredUserList(String filter){
        LocalUser localUser = LocalUser.getInstance();
        List<User> filteredUserList = new ArrayList<>();
        for(User u : data.userList){
            if(!localUser.getFriendList().contains(u.getUserId())
                    && !localUser.getUserId().equals(u.getUserId())
                    && u.getUserName().toLowerCase().contains(filter.toLowerCase())
                    && !filteredUserList.contains(u))
                filteredUserList.add(u);
        }
        return filteredUserList;
    }

    @Override
    public void onFriendDataChange(List<String> friendIDList) {
        activity.updateAdapter(getUserList());
    }

    public void clear(){
        activity = null;
        data.removeFriendObserver(this);
    }
}
