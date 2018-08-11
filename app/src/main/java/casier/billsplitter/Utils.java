package casier.billsplitter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Utils {

    private static Utils mInstance = null;
    private Map<String, String> usersImageUrl;

    List<String> currency = Arrays.asList("$", "€", "¥", "₡", "£", "₪", "₦", "₱", "zł", "₲", "฿", "₴", "₫");

    public static Utils getInstance(){
        if(mInstance == null)
        {
            mInstance = new Utils();
        }
        return mInstance;
    }

    public List<String> getCurrency(){
        return this.currency;
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
