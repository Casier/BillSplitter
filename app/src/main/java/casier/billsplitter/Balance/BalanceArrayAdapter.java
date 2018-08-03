package casier.billsplitter.Balance;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import casier.billsplitter.Model.Bill;
import casier.billsplitter.Model.UserInfo;
import casier.billsplitter.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class BalanceArrayAdapter extends ArrayAdapter<Bill> {

    @BindView(R.id.billName) TextView billName;
    @BindView(R.id.billAmount) TextView billAmount;
    @BindView(R.id.billUserImage) CircleImageView imageView;

    private Context context;
    private List<Bill> billList;

    public BalanceArrayAdapter(@NonNull Context context, int resource, List<Bill> billList) {
        super(context, resource);
        this.context = context;
        this.billList = billList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Bill bill = getItem(position);

        if(getItemViewType(position) == 1){
            convertView = LayoutInflater.from(context).inflate(R.layout.row_bill_layout, null);
        } else {
            convertView = LayoutInflater.from(context).inflate(R.layout.row_bill_self_layout, null);
        }

        ButterKnife.bind(this, convertView);
        BalancePresenter presenter = new BalancePresenter();

        billName.setText(bill.getTitle());
        billAmount.setText(bill.getAmount());
        Glide.with(context)
                .load(Uri.parse(presenter.getImageUrlByUserId(bill.getOwnerId())))
                .into(imageView);



        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if(billList.get(position).getOwnerId() == UserInfo.getInstance().getUserId()){
            return 0;
        }
        return 1;
    }
}
