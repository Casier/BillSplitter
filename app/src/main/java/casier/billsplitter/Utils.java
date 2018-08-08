package casier.billsplitter;

import java.util.Map;

public class Utils {

    private static Utils mInstance = null;
    private Map<String, String> usersImageUrl;

    public static Utils getInstance(){
        if(mInstance == null)
        {
            mInstance = new Utils();
        }
        return mInstance;
    }


    //region User images
    public String getImageUrlByUserId(String userId){
        return usersImageUrl.get(userId);
    }

    public void setUsersImageUrl(Map<String, String> usersImageUrl){
        this.usersImageUrl = usersImageUrl;
    }

    //endregion
}
