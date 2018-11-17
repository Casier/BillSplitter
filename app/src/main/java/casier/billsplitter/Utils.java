package casier.billsplitter;

import android.content.Context;
import android.content.Intent;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import casier.billsplitter.Model.Account;
import casier.billsplitter.Model.Bill;
import casier.billsplitter.Model.LocalUser;
import casier.billsplitter.Model.User;

public class Utils implements UserDataSubject, BillDataSubject, AccountDataSubject, FriendDataSubject {

    private FirebaseDatabase mDatabase;

    private List<User> userList = new ArrayList<>();
    private List<Account> accountList = new ArrayList<>();

    private Account selectedAccount;

    private ArrayList<UserDataObserver> mUserObservers = new ArrayList<>();
    private ArrayList<BillDataObserver> mBillObservers = new ArrayList<>();
    private ArrayList<AccountDataObserver> mAccountObservers = new ArrayList<>();
    private ArrayList<FriendDataObserver> mFriendObservers = new ArrayList<>();

    private static Utils mInstance = null;
    private Map<String, String> usersImageUrl = new HashMap<>();

    private List<String> currency = Arrays.asList("$", "€", "¥", "₡", "£", "₪", "₦", "₱", "zł", "₲", "฿", "₴", "₫");

    public static Utils getInstance(){
        if(mInstance == null) {
            mInstance = new Utils();
            mInstance.loadUsers();
            mInstance.loadAccounts();
        }
        return mInstance;
    }

    public void addUserIfNew(GoogleSignInAccount account){
        boolean accountAlreadyExists = false;
        for(User u : userList){
            if(u.getUserEmail().equals(account.getEmail()))
                accountAlreadyExists = true;
        }

        if(!accountAlreadyExists){
            DAO.getInstance().addUser(account);
        }
    }

    private void loadUsers(){
        mDatabase = FirebaseDatabase.getInstance();
        Query queryUsers = mDatabase.getReference("users");

        queryUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    User u = snapshot.getValue(User.class);
                    u.setUserId(snapshot.getKey());
                    userList.add(u);
                    usersImageUrl.put(snapshot.getKey(), u.getUserPhotoUrl());
                }
                // Add friends that added you
                for(User u : userList) {
                    if (u.getFriends() != null) {
                        for (String s : u.getFriends()) {
                            if (s.equals(LocalUser.getInstance().getUserId())) {
                                addFriend(u.getUserId());
                            }
                        }
                    }
                }
                notifyUserObservers();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getUserFriends(String userID){
        DatabaseReference reference = mDatabase.getReference("users").child(userID).child("friends");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    LocalUser.getInstance().addFriend(snapshot.getValue(String.class));
                }
                notifyFriendsObservers();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void addFriend(String friendID){
        LocalUser currentUser = LocalUser.getInstance();
        currentUser.addFriend(friendID);
        DatabaseReference reference = mDatabase.getReference("users").child(LocalUser.getInstance().getUserId());
        reference.child("friends").setValue(currentUser.getFriendList());
    }

    public void sendInviteToFriend(Context context){
        // TODO use param
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

    private void loadAccounts(){
        mDatabase = FirebaseDatabase.getInstance();
        Query queryAccount = mDatabase.getReference("accounts");
        queryAccount.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                accountList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    List<Bill> accountBillList = new ArrayList<>();
                    List<String> accountUsers = new ArrayList<>();
                    for(DataSnapshot billSnapshot : snapshot.getChildren()){
                        if(billSnapshot.getKey().equals("bills")){
                            for(DataSnapshot snapshotBill: billSnapshot.getChildren()){
                                Bill b = snapshotBill.getValue(Bill.class);
                                if(b != null && !accountBillList.contains(b))
                                    accountBillList.add(b);
                            }
                        }
                        if(billSnapshot.getKey().equals("usersId")){
                            for(DataSnapshot snapshotBill: billSnapshot.getChildren()){
                                String s = snapshotBill.getValue(String.class);
                                if(s != null && !accountUsers.contains(s))
                                    accountUsers.add(s);
                            }
                        }
                    }
                    Account a = new Account();
                    a.setAccountName(snapshot.getKey());
                    a.setBills(accountBillList);
                    a.setAccountUsers(accountUsers);

                    accountList.add(a);
                }
                notifyAccountObservers();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public Account getSelectedAccount(){
        return this.selectedAccount;
    }

    public void setSelectedAccount(Account account){
        this.selectedAccount = account;
        accountList.clear();
        mInstance.loadAccounts();
    }

    public void createAccount(String accountName, List<User> userList){
        // TODO à refaire c'est de la merde

        if(!accountList.contains(accountName)){
            List<String> usersId = new ArrayList<>();
            for(User u : userList)
                    usersId.add(u.getUserId());

            DatabaseReference reference = mDatabase.getReference("accounts");
            reference.child(accountName).child("usersId").setValue(usersId);
        }
    }

    public void updateAccount(String accountName){
        DatabaseReference oldReference = mDatabase.getReference("accounts").child(selectedAccount.getAccountName());
        DatabaseReference newReference = mDatabase.getReference("accounts").child(accountName);
        copyRecord(oldReference, newReference);
    }

    public void addBill(Bill bill){
        if(!selectedAccount.getBills().contains(bill)){
            selectedAccount.getBills().add(bill);
            for(Account a : accountList){
                if(a.equals(selectedAccount))
                    a.addBill(bill);
            }
            notifyAccountObservers();
            notifyBillObservers();
        }
    }

    private void copyRecord(DatabaseReference fromPath, final DatabaseReference toPath) {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                toPath.setValue(dataSnapshot.getValue()).addOnCompleteListener(task ->{
                    if (task.isComplete()) {
                        fromPath.removeValue();
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        fromPath.addListenerForSingleValueEvent(valueEventListener);
    }

    public void deleteBill(Bill bill){
        if(selectedAccount.getBills().contains(bill)){
            selectedAccount.getBills().remove(bill);
            for(Account a : accountList){
                if(a.equals(selectedAccount))
                    a.removeBill(bill);
            }
            mDatabase = FirebaseDatabase.getInstance();
            mDatabase.getReference("accounts").child(selectedAccount.getAccountName()).child("bills").child(bill.getDate()).removeValue();
            notifyBillObservers();
        }
    }

    public void deleteAccount(Account account){
        Iterator<Account> iterator = accountList.iterator();
        while(iterator.hasNext()){
            Account current = iterator.next();
            if(current.getAccountName().equals(account.getAccountName()))
                iterator.remove();
        }
        mDatabase = FirebaseDatabase.getInstance();
        mDatabase.getReference("accounts")
                .child(account.getAccountName())
                .removeValue()
                .addOnCompleteListener(task -> notifyAccountObservers());
    }

    public User getUserById(String id){
        for(User u : userList){
            if(u.getUserId().equals(id))
                return u;
        }
        return null;
    }

    public List<User> getUserList(){
        return this.userList;
    }

    public List<Bill> getBillList(){
        return this.selectedAccount.getBills();
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
            Collections.sort(userList, (u1, u2) -> (u1.getUserName().compareTo(u2.getUserName())));
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
            Collections.sort(selectedAccount.getBills(), (u1, u2) -> (u1.getTitle().compareTo(u2.getTitle()))  );
            o.onBillDataChange(selectedAccount.getBills());
        }
    }

    @Override
    public void registerAccountObserver(AccountDataObserver dataObserver) {
        if(!mAccountObservers.contains(dataObserver))
            mAccountObservers.add(dataObserver);
    }

    @Override
    public void removeAccountObserver(AccountDataObserver dataObserver) {
        if(mAccountObservers.contains(dataObserver))
            mAccountObservers.remove(dataObserver);
    }

    @Override
    public void notifyAccountObservers() {
        for(AccountDataObserver o : mAccountObservers){
            Collections.sort(accountList, (u1, u2) -> (u1.getAccountName().compareTo(u2.getAccountName())));
            o.onAccountDataChange(accountList);
        }
    }

    @Override
    public void registerFriendObserver(FriendDataObserver dataObserver) {
        if(!mFriendObservers.contains(dataObserver))
            mFriendObservers.add(dataObserver);
    }

    @Override
    public void removeFriendObserver(FriendDataObserver dataObserver) {
        if(mFriendObservers.contains(dataObserver))
            mFriendObservers.remove(dataObserver);
    }

    @Override
    public void notifyFriendsObservers() {
        for(FriendDataObserver o : mFriendObservers){
            o.onFriendDataChange(LocalUser.getInstance().getFriendList());
        }
    }
}
