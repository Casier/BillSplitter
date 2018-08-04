package casier.billsplitter.Balance;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import casier.billsplitter.Model.Bill;
import casier.billsplitter.Model.UserInfo;
import casier.billsplitter.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class BalanceArrayAdapter extends ArrayAdapter<Bill> {

    private Context context;
    private List<Bill> billList;

    public BalanceArrayAdapter(@NonNull Context context, int resource, List<Bill> billList) {
        super(context, resource);
        this.context = context;
        this.billList = billList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Bill bill = billList.get(position);


        View v = convertView;
        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(context);
            if(bill.getOwnerId().equals(UserInfo.getInstance().getUserId())){
                v = vi.inflate(R.layout.row_bill_self_layout, null);
            } else {
                v = vi.inflate(R.layout.row_bill_layout, null);
            }
        }
        if(!billList.isEmpty() && billList.size() > 0){

            if (bill != null) {
                TextView tv1 = v.findViewById(R.id.billName);
                TextView tv2 = v.findViewById(R.id.billAmount);
                CircleImageView iv = v.findViewById(R.id.billUserImage);
                if (tv1 != null) {
                    tv1.setText(bill.getTitle());
                }
                if(tv2 != null) {
                    tv2.setText(bill.getAmount());
                }
                if (iv != null) {
                    BalancePresenter presenter = new BalancePresenter();
                    try {
                        Glide.with(context)
                                .load(Uri.parse(presenter.getImageUrlByUserId(bill.getOwnerId())))
                                .into(iv);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Log.d("panda", "non");
                    }
                }
            }
        }
        return v;
    }

    @Override
    public int getCount() {
        return this.billList.size();
    }

    public void updateData(List<Bill> billList){
        this.billList = billList;
    }
}
