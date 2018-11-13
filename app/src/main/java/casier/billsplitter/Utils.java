package casier.billsplitter;

import android.support.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import casier.billsplitter.Model.User;

public class Utils implements UserDataSubject, BillDataSubject, AccountDataSubject {

    private FirebaseDatabase mDatabase;

    private List<User> userList = new ArrayList<>();
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
            DatabaseReference reference = mDatabase.getReference("users");
            User user = new User(account.getEmail(), account.getDisplayName(), account.getPhotoUrl().toString());
            reference.child(account.getId()).setValue(user);
        }
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

    private void loadAccounts(){
        
        mDatabase = FirebaseDatabase.getInstance();
        Query queryAccount = mDatabase.getReference("accounts");
        queryAccount.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
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
                    if(!accountList.contains(a))
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
        if(!accountList.contains(accountName)){
            List<String> usersId = new ArrayList<>();
            for(User u : userList)
                    usersId.add(u.getUserId());

            DatabaseReference reference = mDatabase.getReference("accounts");
            reference.child(accountName).child("usersId").setValue(usersId);
        }
    }

    public void updateAccount(String accountName, List<User> userList){
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
                toPath.setValue(dataSnapshot.getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isComplete()) {
                            fromPath.removeValue();
                        } else {
                            // todo show toast
                        }
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
        mDatabase.getReference("accounts").child(account.getAccountName()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                notifyAccountObservers();
            }
        });
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
        for(AccountDataObserver o : mDataObservers){
            Collections.sort(accountList, (u1, u2) -> (u1.getAccountName().compareTo(u2.getAccountName())));
            o.onAccountDataChange(accountList);
        }
    }

    public Account getAccountByName(String name){
        for(Account account : accountList){
            if(account.getAccountName().equals(name)){
                accountList.remove(account);
                return account;
            }
        }
        return null;
    }

    public List<User> searchUsers(String searchSequence){
        List<User> searchedUserList = new ArrayList<>();
        for(User u : userList){
            if(u.getUserName().toLowerCase().contains(searchSequence.toLowerCase()))
                searchedUserList.add(u);
        }
        return searchedUserList;
    }
}
