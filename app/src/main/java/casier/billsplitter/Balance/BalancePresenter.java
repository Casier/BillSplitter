package casier.billsplitter.Balance;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import casier.billsplitter.DataObserver;
import casier.billsplitter.DataSubject;
import casier.billsplitter.Model.Bill;
import casier.billsplitter.Model.User;
import casier.billsplitter.Utils;

public class BalancePresenter implements DataSubject {

    private FirebaseDatabase mDatabase;
    private BalanceActivity balanceActivity;
    private List<Bill> billList = new ArrayList<>();
    private Map<String, String> usersImageUrl;
    private ArrayList<DataObserver> mObservers;

    public BalancePresenter(final BalanceActivity balanceActivity) {
        this.balanceActivity = balanceActivity;
        usersImageUrl = new HashMap<>();
        mObservers = new ArrayList<>();
        getBills();
        getImagesUrl();
        registerObserver(balanceActivity);
    }

    private void getImagesUrl(){
        mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = mDatabase.getReference("users");
        Query queryUrl = myRef;
        queryUrl.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    User u = snapshot.getValue(User.class);
                    if(u != null){
                        usersImageUrl.put(snapshot.getKey(), u.getUserPhotoUrl());
                    }
                }
                Utils.getInstance().setUsersImageUrl(usersImageUrl);
                notifyObservers();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void getBills(){
        mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = mDatabase.getReference("bills");
        Query queryBills = myRef;
        queryBills.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Bill b = snapshot.getValue(Bill.class);
                    if(b != null) billList.add(b);
                }
                notifyObservers();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void removeBill(Bill bill){
        // TODO : Remove bill from database
    }

    @Override
    public void registerObserver(DataObserver dataObserver) {
        if(!mObservers.contains(dataObserver)){
            mObservers.add(dataObserver);
        }
    }

    @Override
    public void notifyObservers() {
        for(DataObserver d : mObservers){
            d.onDataChange(billList, usersImageUrl);
        }
    }
}
