package casier.billsplitter.AddBill;

import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import casier.billsplitter.DAO;
import casier.billsplitter.Model.Bill;
import casier.billsplitter.Model.LocalUser;
import casier.billsplitter.Model.User;
import casier.billsplitter.Utils;

public class AddBillPresenter {

    private AddBillActivity addBillActivity;
    private FirebaseDatabase mDatabase;
    private Utils mUtils;
    private DAO data;

    public AddBillPresenter(AddBillActivity addBillActivity){
        this.addBillActivity = addBillActivity;
        this.mDatabase = FirebaseDatabase.getInstance();
        mUtils = Utils.getInstance();
        data = DAO.getInstance();
    }

    public void addBill(String billName, String billAmount, List<User> billUsersList){

        List<String> billUserIdList = new ArrayList<>();
        for(User u : billUsersList)
            billUserIdList.add(u.getUserId());

        billUserIdList.add(LocalUser.getInstance().getUserId());
        Date date = Calendar.getInstance().getTime();
        Bill newBill = new Bill(date.toString(), billAmount, LocalUser.getInstance().getUserId(), billName, billUserIdList);
        data.addBill(newBill);
    }

    public List<User> getAccountUserList(){
        List<User> accountUserList = new ArrayList<>();
        for(String s : mUtils.getSelectedAccount().getAccountUsers())
            accountUserList.add(mUtils.getUserById(s));

        Iterator<User> iterator = accountUserList.iterator();
        while (iterator.hasNext()){
            User u = iterator.next();
            if(u.getUserId().equals(LocalUser.getInstance().getUserId()))
                iterator.remove();
        }

        return accountUserList;
    }
}
