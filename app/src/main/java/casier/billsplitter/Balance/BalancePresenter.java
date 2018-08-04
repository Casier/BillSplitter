package casier.billsplitter.Balance;

import com.google.firebase.database.ChildEventListener;
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

public class BalancePresenter {

    private FirebaseDatabase mDatabase;
    private BalanceActivity balanceActivity;
    private List<Bill> billList = new ArrayList<>();
    private String url;
    private Map<String, String> usersImageUrl = new HashMap<>();

    public BalancePresenter(BalanceActivity balanceActivity) {
        this.balanceActivity = balanceActivity;
        getBills();
    }

    public BalancePresenter(){

    }

    public String getImageUrlByUserId(String userId) throws InterruptedException {
        /*final CountDownLatch done = new CountDownLatch(1);
        mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = mDatabase.getReference("users");
        Query queryUser = myRef.child(userId);
        queryUser.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d("panda", "oui");
                User u = dataSnapshot.getValue(User.class);
                url = u.getUserPhotoUrl();
                done.countDown();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d("panda", "ouii");
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d("panda", "ouiii");
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Log.d("panda", "oui2");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("panda", "oui4");
            }
        });
        done.await();*/
        // TODO : s'assurer que la Map soit remplie avant de load l'interface
        String url = usersImageUrl.get(userId);
        return url;
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
                    String id = b .getOwnerId();
                    mDatabase = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = mDatabase.getReference("users");
                    Query queryBills = myRef.orderByChild(id);
                    queryBills.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            User u = dataSnapshot.getValue(User.class);
                            String key = dataSnapshot.getKey();
                            if(u != null && key != "") {
                                usersImageUrl.put(key, u.getUserPhotoUrl());
                                balanceActivity.notifyAdapter(billList);
                            }
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
