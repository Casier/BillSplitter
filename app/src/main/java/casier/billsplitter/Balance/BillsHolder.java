package casier.billsplitter.Balance;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import casier.billsplitter.Model.Bill;
import casier.billsplitter.R;
import casier.billsplitter.Utils;
import de.hdodenhof.circleimageview.CircleImageView;

public class BillsHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private Context context;
    private final TextView billName;
    private final TextView billAmount;
    private final CircleImageView userImage;
    public RelativeLayout layout;

    public BillsHolder(Context context, View itemView) {
        super(itemView);

        this.context = context;

        this.billName = itemView.findViewById(R.id.billName);
        this.billAmount = itemView.findViewById(R.id.billAmount);
        this.userImage = itemView.findViewById(R.id.billUserImage);
        this.layout = itemView.findViewById(R.id.globalContainer);

        itemView.setOnClickListener(this);
    }

    public void bindBill(Bill bill){

        billName.setText(bill.getTitle());
        billAmount.setText(bill.getAmount() + "$");

        Glide.with(context)
                .load(Uri.parse(Utils.getInstance().getImageUrlByUserId(bill.getOwnerId())))
                .into(userImage);
    }

    @Override
    public void onClick(View view) {

    }
}
