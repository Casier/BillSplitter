package casier.billsplitter.Model;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {

    private String userEmail;
    private String userName;
    private String userPhotoUrl;
    @Exclude
    private Map<User, Float> usersBalance;
    @Exclude String userId;

    private List<String> friends;

    public User(){

    }

    public User (String userEmail, String userName, String userPhotoUrl){
        this.userEmail = userEmail;
        this.userName = userName;
        this.userPhotoUrl = userPhotoUrl;
    }

    public User (String userEmail, String userName, String userPhotoUrl, List<String> friendList){
        this.userEmail = userEmail;
        this.userName = userName;
        this.userPhotoUrl = userPhotoUrl;
        this.friends = new ArrayList<>(friendList);
    }

    public User(String userEmail, String userName, String userPhotoUrl, Map<User, Float> usersBalance){
        this.userEmail = userEmail;
        this.userName = userName;
        this.userPhotoUrl = userPhotoUrl;
        this.usersBalance = usersBalance;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String user_email) {
        this.userEmail = user_email;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String user_name) {
        this.userName = user_name;
    }

    public String getUserPhotoUrl() {
        return userPhotoUrl;
    }

    public void setUserPhotoUrl(String user_photo_url) {
        this.userPhotoUrl = user_photo_url;
    }

    public Map<User, Float> getUsersBalance() {
        return usersBalance;
    }

    public void setUsersBalance(Map<User, Float> usersBalance) {
        this.usersBalance = usersBalance;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId(){
        return this.userId;
    }

    public List<String> getFriends() {
        return friends;
    }

    public void setFriends(List<String> friends) {
        this.friends = friends;
    }

    public void addToBalance(User paier, Float amount){
        if(usersBalance == null)
            usersBalance = new HashMap<>();

        if(usersBalance.get(paier) == null){
            usersBalance.put(paier, amount);
        } else {
            usersBalance.put(paier, usersBalance.get(paier) + amount);
        }
    }

    public void clearBalance(){
        if(usersBalance != null)
            usersBalance.clear();
    }

    @Override
    public boolean equals(Object obj) {
        if(this.userId.equals(((User)obj).getUserId()))
            return true;

        return false;
    }
}
