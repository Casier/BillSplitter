package casier.billsplitter.AddBill;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import com.google.firebase.database.FirebaseDatabase;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import casier.billsplitter.DAO;
import casier.billsplitter.Model.Bill;
import casier.billsplitter.Model.LocalUser;
import casier.billsplitter.Model.User;
import casier.billsplitter.UserDataObserver;
import casier.billsplitter.Utils;

public class AddBillPresenter implements UserDataObserver {

    private AddBillActivity activity;
    private FirebaseDatabase mDatabase;
    private Utils mUtils;
    private DAO data;

    public AddBillPresenter(AddBillActivity addBillActivity){
        this.activity = addBillActivity;
        this.mDatabase = FirebaseDatabase.getInstance();
        mUtils = Utils.getInstance();
        data = DAO.getInstance();
        data.registerUserObserver(this);
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

    public Bitmap decodeBitmapUri(Context ctx, Uri uri) throws FileNotFoundException {
        int targetW = 600;
        int targetH = 600;
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(ctx.getContentResolver().openInputStream(uri), null, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        return BitmapFactory.decodeStream(ctx.getContentResolver()
                .openInputStream(uri), null, bmOptions);
    }

    public void clear(){
        data.removeUserObserver(this);
    }

    @Override
    public void onUserDataChange(List<User> userList) {
        activity.adapter.notifyDataSetChanged();
    }
}
