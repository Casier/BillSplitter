package casier.billsplitter.Model;

import android.net.Uri;

public class UserInfo {

    private String user_email;
    private String user_name;
    private String user_id;
    private Uri user_photo_url;

    private static UserInfo mInstance = null;

    public static UserInfo getInstance(){
        if(mInstance == null)
        {
            mInstance = new UserInfo();
        }
        return mInstance;
    }

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

    public String getUserId() {
        return user_id;
    }

    public void setUserId(String user_id) {
        this.user_id = user_id;
    }

    public Uri getUserPhotoUrl() {
        return user_photo_url;
    }

    public void setUserPhotoUrl(Uri user_photo_url) {
        this.user_photo_url = user_photo_url;
    }
}
