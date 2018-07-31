package casier.billsplitter.Model;

public class User {

    private String user_email;
    private String user_name;
    private String user_photo_url;

    public String getUserEmail() {
        return user_email;
    }

    public void setUserEmail(String user_email) {
        this.user_email = user_email;
    }

    public String getUserName() {
        return user_name;
    }

    public void setUserName(String user_name) {
        this.user_name = user_name;
    }

    public String getUserPhotoUrl() {
        return user_photo_url;
    }

    public void setUserPhotoUrl(String user_photo_url) {
        this.user_photo_url = user_photo_url;
    }
}
