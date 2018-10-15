package casier.billsplitter.AddBill;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import casier.billsplitter.Model.Bill;
import casier.billsplitter.Model.LocalUser;
import casier.billsplitter.Utils;

public class AddBillPresenter {

    private AddBillActivity addBillActivity;
    private FirebaseDatabase mDatabase;
    private Utils mUtils;

    public AddBillPresenter(AddBillActivity addBillActivity){
        this.addBillActivity = addBillActivity;
        this.mDatabase = FirebaseDatabase.getInstance();
        this.mUtils = Utils.getInstance();
    }

    public void addBill(String billName, String billAmount, List<String> billUsersList){

        Date date = Calendar.getInstance().getTime();
        Bill newBill = new Bill(date.toString(), billAmount, LocalUser.getInstance().getUserId(), billName, billUsersList);

        DatabaseReference myRef = mDatabase.getReference("accounts").child(mUtils.getSelectedAccount().getAccountName()).child("bills");
        myRef.child(date.toString()).setValue(newBill);
        mUtils.addBill(newBill);
    }

}