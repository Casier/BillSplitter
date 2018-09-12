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

import casier.billsplitter.Model.Bill;
import casier.billsplitter.Model.User;
import casier.billsplitter.UserDataObserver;

public class BalancePresenter {

    private FirebaseDatabase mDatabase;
    private BalanceActivity balanceActivity;
    private List<Bill> billList = new ArrayList<>();
    private Map<String, User> usersList;
    private ArrayList<UserDataObserver> mObservers;

    public BalancePresenter(final BalanceActivity balanceActivity) {
        this.balanceActivity = balanceActivity;
        usersList = new HashMap<>();
        mObservers = new ArrayList<>();
        getBills();
        getImagesUrl();
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
                        usersList.put(snapshot.getKey(), u);
                    }
                }
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
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void removeBill(Bill bill){
        // TODO : Remove bill from database
    }


    public List<String> getNames() {

        List<String> names = new ArrayList<>();

        Map<String, Float> usersBalance = new HashMap<>();
        for(Bill b : billList){
            Float amount = Float.parseFloat(b.getAmount());
            if(usersBalance.containsKey(b.getOwnerId())){
                usersBalance.put(b.getOwnerId(), (usersBalance.get(b.getOwnerId()) + amount));
            } else {
                usersBalance.put(b.getOwnerId(), amount);
            }
        }

        for (Map.Entry<String, Float> entry : usersBalance.entrySet())
        {
            System.out.println(entry.getKey() + "/" + entry.getValue());
        }

        return names;
    }
}
