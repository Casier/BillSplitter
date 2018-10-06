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

import casier.billsplitter.Model.Account;
import casier.billsplitter.Model.Bill;
import casier.billsplitter.Model.User;

public class Utils implements UserDataSubject, BillDataSubject, AccountDataSubject {

    private FirebaseDatabase mDatabase;

    private List<User> userList = new ArrayList<>();
    private List<Bill> billList = new ArrayList<>();
    private List<Account> accountList = new ArrayList<>();

    private Account selectedAccount;

    private ArrayList<UserDataObserver> mUserObservers = new ArrayList<>();
    private ArrayList<BillDataObserver> mBillObservers = new ArrayList<>();
    private ArrayList<AccountDataObserver> mDataObservers = new ArrayList<>();

    private static Utils mInstance = null;
    private Map<String, String> usersImageUrl = new HashMap<>();

    private List<String> currency = Arrays.asList("$", "€", "¥", "₡", "£", "₪", "₦", "₱", "zł", "₲", "฿", "₴", "₫");

    public static Utils getInstance(){
        if(mInstance == null) {
            mInstance = new Utils();
            mInstance.loadUsers();
            mInstance.loadBills();
            mInstance.loadAccounts();
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
                    userList.add(u);
                    usersImageUrl.put(snapshot.getKey(), u.getUserPhotoUrl());
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
        Query queryBills = mDatabase.getReference("accounts").child("accountName").child("bills");

        queryBills.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                billList.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Bill b = snapshot.getValue(Bill.class);
                    if(b != null) {
                        billList.add(b);
                    }
                }
                notifyBillObservers();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadAccounts(){
        mDatabase = FirebaseDatabase.getInstance();
        Query queryAccount = mDatabase.getReference("accounts");
        queryAccount.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    List<Bill> accountBillList = new ArrayList<>();
                    for(DataSnapshot billSnapshot : snapshot.getChildren()){
                        if(billSnapshot.getKey().equals("bills")){
                            for(DataSnapshot snapshotBill: billSnapshot.getChildren()){
                                Bill b = snapshotBill.getValue(Bill.class);
                                if(b != null) {
                                    billList.add(b);
                                }
                            }
                        }
                    }
                    Account a = new Account();
                    a.setAccountName(snapshot.getKey());
                    a.setBills(accountBillList);
                    accountList.add(a);
                }
                notifyAccountObservers();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void deleteBill(Bill bill){
        mDatabase = FirebaseDatabase.getInstance();
        mDatabase.getReference("bills").child(bill.getDate()).removeValue();
    }

    public User getUserById(String id){
        for(User u : userList){
            if(u.getUserId().equals(id))
                return u;
        }
        return null;
    }

    public List<User> getUserBalanceSummaryList(){
        List<User> userBalanceSummaryList = new ArrayList<>();
        for(User u : userList){
            if(u.getUsersBalance().size() > 0)
                userBalanceSummaryList.add(u);
        }
        return userBalanceSummaryList;
    }

    public List<User> getUserList(){
        return this.userList;
    }

    public List<Bill> getBillList(){
        return this.billList;
    }

    public List<Account> getAccountList(){
        return this.accountList;
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

    @Override
    public void registerAccountObserver(AccountDataObserver dataObserver) {
        if(!mDataObservers.contains(dataObserver))
            mDataObservers.add(dataObserver);
    }

    @Override
    public void removeAccountObserver(AccountDataObserver dataObserver) {
        if(mDataObservers.contains(dataObserver))
            mDataObservers.remove(dataObserver);
    }

    @Override
    public void notifyAccountObservers() {
        for(AccountDataObserver o : mDataObservers)
            o.onAccountDataChange(accountList);
    }
}
