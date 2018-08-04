package casier.billsplitter.Model;

public class User {

    private String userEmail;
    private String userName;
    private String userPhotoUrl;

    public User(){

    }

    public User(String userEmail, String userName, String userPhotoUrl){
        this.userEmail = userEmail;
        this.userName = userName;
        this.userPhotoUrl = userPhotoUrl;
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
}
