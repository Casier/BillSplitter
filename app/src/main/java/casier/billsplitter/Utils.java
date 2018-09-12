package casier.billsplitter;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import casier.billsplitter.Model.Bill;
import casier.billsplitter.Model.User;

public class Utils implements UserDataSubject, BillDataSubject {

    private FirebaseDatabase mDatabase;

    private List<User> userList = new ArrayList<>();
    private List<Bill> billList = new ArrayList<>();

    private ArrayList<UserDataObserver> mUserObservers = new ArrayList<>();
    private ArrayList<BillDataObserver> mBillObservers = new ArrayList<>();

    private static Utils mInstance = null;
    private Map<String, String> usersImageUrl = new HashMap<>();

    private List<String> currency = Arrays.asList("$", "€", "¥", "₡", "£", "₪", "₦", "₱", "zł", "₲", "฿", "₴", "₫");

    public static Utils getInstance(){
        if(mInstance == null) {
            mInstance = new Utils();
            mInstance.loadUsers();
            mInstance.loadBills();
        }
        return mInstance;
    }

    private void loadUsers(){
        mDatabase = FirebaseDatabase.getInstance();
        Query queryUsers = mDatabase.getReference("users");

        queryUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    User u = snapshot.getValue(User.class);
                    u.setUserId(snapshot.getKey());
                    if(u != null) {
                        userList.add(u);
                        usersImageUrl.put(snapshot.getKey(), u.getUserPhotoUrl());
                    }
                }
                notifyUserObservers();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public String getImageUrlByUserId(String userId){
        return usersImageUrl.get(userId);
    }

    private void loadBills(){
        mDatabase = FirebaseDatabase.getInstance();
        Query queryBills = mDatabase.getReference("bills");

        queryBills.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Bill b = snapshot.getValue(Bill.class);
                    if(b != null) {
                        billList.add(b);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public List<User> getUserList(){
        return this.userList;
    }

    public List<Bill> getBillList(){
        return this.billList;
    }

    public List<String> getCurrency(){
        return this.currency;
    }

    @Override
    public void registerUserObserver(UserDataObserver dataObserver) {
        if(!mUserObservers.contains(dataObserver)){
            mUserObservers.add(dataObserver);
        }
    }

    @Override
    public void removeUserObserver(UserDataObserver dataObserver) {
        if(mUserObservers.contains(dataObserver)){
            mUserObservers.remove(dataObserver);
        }
    }

    @Override
    public void notifyUserObservers() {
        for(UserDataObserver o : mUserObservers){
            o.onUserDataChange(userList);
        }
    }

    @Override
    public void registerBillObserver(BillDataObserver dataObserver) {
        if(!mBillObservers.contains(dataObserver)){
            mBillObservers.add(dataObserver);
        }
    }

    @Override
    public void removeBillObserver(BillDataObserver dataObserver) {
        if(mBillObservers.contains(dataObserver)){
            mBillObservers.remove(dataObserver);
        }
    }

    @Override
    public void notifyBillObservers() {
        for(BillDataObserver o : mBillObservers){
            o.onBillDataChange(billList);
        }
    }
}
