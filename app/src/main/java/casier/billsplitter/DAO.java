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
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import casier.billsplitter.Model.Account;
import casier.billsplitter.Model.Bill;
import casier.billsplitter.Model.LocalUser;
import casier.billsplitter.Model.User;

public class DAO implements UserDataSubject, FriendDataSubject, AccountDataSubject, BillDataSubject{

    private static DAO mInstance = null;

    private Utils mUtils = Utils.getInstance();

    private FirebaseDatabase mDatabase;

    private ArrayList<UserDataObserver> mUserObservers = new ArrayList<>();
    private ArrayList<FriendDataObserver> mFriendObservers = new ArrayList<>();
    private ArrayList<AccountDataObserver> mAccountObservers = new ArrayList<>();
    private ArrayList<BillDataObserver> mBillObservers = new ArrayList<>();

    public List<User> userList = new ArrayList<>();
    public List<Account> accountList = new ArrayList<>();


    // Singleton
    public static DAO getInstance(){
        if(mInstance == null) {
            mInstance = new DAO();
            mInstance.loadUsers();
            mInstance.loadAccounts();
        }
        return mInstance;
    }

    // Add user at first login
    public void addUser(GoogleSignInAccount account){
        User user = new User(account.getEmail(), account.getDisplayName(), account.getPhotoUrl().toString());
        DatabaseReference reference = mDatabase.getReference("users");
        reference.child(account.getId()).setValue(user);
    }

    // Load users data & track updates
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
                    Utils.getInstance().addUserImageUrl(snapshot.getKey(), u.getUserPhotoUrl());
                }
                // Add friends that added you
                /*
                for(User u : userList) {
                    if (u.getFriends() != null) {
                        for (String s : u.getFriends()) {
                            if (s.equals(LocalUser.getInstance().getUserId())) {
                                addFriend(u.getUserId());
                            }
                        }
                    }
                }*/
                notifyUserObservers();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadLocalUserFriends(){
        LocalUser localUser = LocalUser.getInstance();
        DatabaseReference reference = mDatabase.getReference("users").child(localUser.getUserId()).child("friends");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    localUser.addFriend(snapshot.getValue(String.class));
                }
                notifyFriendsObservers();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void addFriend(String friendId){
        LocalUser currentUser = LocalUser.getInstance();
        currentUser.addFriend(friendId);
        DatabaseReference reference = mDatabase.getReference("users").child(LocalUser.getInstance().getUserId());
        reference.child("friends").setValue(currentUser.getFriendList());
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

    public void addBill(Bill bill){
        Date date = Calendar.getInstance().getTime();
        DatabaseReference reference = mDatabase.getReference("accounts").child(mUtils.getSelectedAccount().getAccountName()).child("bills");
        reference.child(date.toString()).setValue(bill);

        notifyAccountObservers();
        notifyBillObservers();
    }

    public void deleteBill(Bill bill){
        mDatabase = FirebaseDatabase.getInstance();
        mDatabase.getReference("accounts").child(mUtils.getSelectedAccount().getAccountName()).child("bills").child(bill.getDate()).removeValue();
        notifyBillObservers();
    }

    public void createAccount(String accountName, List<String> usersId){
        DatabaseReference reference = mDatabase.getReference("accounts");
        reference.child(accountName).child("usersId").setValue(usersId);
    }

    public void deleteAccount(Account account){
        mDatabase = FirebaseDatabase.getInstance();
        mDatabase.getReference("accounts").child(account.getAccountName()).removeValue().addOnCompleteListener(task -> {
            Iterator<Account> iterator = accountList.iterator();
            while(iterator.hasNext()){
                Account current = iterator.next();
                if(current.getAccountName().equals(account.getAccountName()))
                    iterator.remove();
            }
            notifyAccountObservers();
        });
    }

    public void updateAccount(String accountName, List<User> userList){
        DatabaseReference oldReference = mDatabase.getReference("accounts").child(mUtils.getSelectedAccount().getAccountName());
        DatabaseReference newReference = mDatabase.getReference("accounts").child(accountName);
        copyRecord(oldReference, newReference);
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

    //region User data observers methods
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
    //endregion

    //region LocalUser friends data observers methods
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
    //endregion

    //region Account data observers methods
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
    //endregion

    //region Bill data observers methods
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
            Collections.sort(mUtils.getSelectedAccount().getBills(), (u1, u2) -> (u1.getTitle().compareTo(u2.getTitle()))  );
            o.onBillDataChange(mUtils.getSelectedAccount().getBills());
        }
    }
    //endregion
}
