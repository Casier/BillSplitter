package casier.billsplitter;

import android.content.Context;
import android.content.Intent;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import casier.billsplitter.Model.Account;
import casier.billsplitter.Model.Bill;
import casier.billsplitter.Model.LocalUser;
import casier.billsplitter.Model.User;

public class Utils {

    private FirebaseDatabase mDatabase;

    private Account selectedAccount;

    private ArrayList<BillDataObserver> mBillObservers = new ArrayList<>();
    private ArrayList<FriendDataObserver> mFriendObservers = new ArrayList<>();

    private static Utils mInstance = null;
    private Map<String, String> usersImageUrl = new HashMap<>();
    private static DAO data = DAO.getInstance();

    private List<String> currency = Arrays.asList("$", "€", "¥", "₡", "£", "₪", "₦", "₱", "zł", "₲", "฿", "₴", "₫");

    public static Utils getInstance(){
        if(mInstance == null) {
            mInstance = new Utils();
        }
        return mInstance;
    }

    public void addFriend(String friendID){
        LocalUser currentUser = LocalUser.getInstance();
        currentUser.addFriend(friendID);
        DatabaseReference reference = mDatabase.getReference("users").child(LocalUser.getInstance().getUserId());
        reference.child("friends").setValue(currentUser.getFriendList());
    }

    public void sendInviteToFriend(Context context){
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_SUBJECT, "BillSplitter");
        String strShareMessage = "\nLet me recommend you this application\n\n";
        strShareMessage = strShareMessage + "https://play.google.com/store/apps/details?id=" + context.getPackageName();
        i.setType("image/png");
        i.putExtra(Intent.EXTRA_TEXT, strShareMessage);
        context.startActivity(Intent.createChooser(i, "Share via"));
    }

    public void addUserImageUrl(String id, String url){
        usersImageUrl.put(id, url);
    }

    public String getImageUrlByUserId(String userId){
        return usersImageUrl.get(userId);
    }

    public Account getSelectedAccount(){
        return this.selectedAccount;
    }

    public void setSelectedAccount(Account account){
        this.selectedAccount = account;
    }

    public User getUserById(String id){
        for(User u : data.userList){
            if(u.getUserId().equals(id))
                return u;
        }
        return null;
    }

    public List<Bill> getBillList(){
        return this.selectedAccount.getBills();
    }

    public List<String> getCurrency(){
        return this.currency;
    }
}
