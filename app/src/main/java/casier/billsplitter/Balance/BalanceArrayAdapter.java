package casier.billsplitter.Balance;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import casier.billsplitter.Model.Bill;
import casier.billsplitter.Model.LocalUser;
import casier.billsplitter.R;
import casier.billsplitter.Utils;
import de.hdodenhof.circleimageview.CircleImageView;

public class BalanceArrayAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private final List<Bill> billList;
    private int itemRessource;
    private OnItemClicked onClick;
    private Utils utils;

    public interface OnItemClicked {
        void onClick(int position);
    }

    public BalanceArrayAdapter(@NonNull Context context, int resource, List<Bill> billList) {

        this.itemRessource = resource;
        this.context = context;
        this.billList = billList;

        this.utils = Utils.getInstance();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View v;

        switch (viewType) {
            case 1:
                v = inflater.inflate(R.layout.row_bill_self_layout, parent, false);
                viewHolder = new BillsSelfHolder(parent.getContext(), v);
                break;
            case 2:
                v = inflater.inflate(R.layout.row_bill_layout, parent, false);
                viewHolder = new BillsHolder(parent.getContext(), v);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        switch (holder.getItemViewType()) {
            case 1:
                BillsSelfHolder billsSelfHolder = (BillsSelfHolder) holder;
                billsSelfHolder.bindBill(billList.get(position));
                billsSelfHolder.layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onClick.onClick(position);
                    }
                });
                break;
            case 2:
                BillsHolder billsHolder = (BillsHolder) holder;
                billsHolder.bindBill(billList.get(position));
                billsHolder.layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onClick.onClick(position);
                    }
                });
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (billList.get(position).getOwnerId().equals(LocalUser.getInstance().getUserId())) {
            return 1;
        } else {
            return 2;
        }
    }

    @Override
    public int getItemCount() {
        return this.billList.size();
    }

    public void setOnClick(OnItemClicked onClick) {
        this.onClick = onClick;
    }

    public Bill getItemAtPosition(int position){
        return billList.get(position);
    }

    public class BillsSelfHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {

        private Context context;
        private final TextView billName;
        private final TextView billAmount;
        private final CircleImageView userImage;
        public RelativeLayout layout;


        public BillsSelfHolder(Context context, View itemView) {
            super(itemView);

            this.context = context;

            this.billName = itemView.findViewById(R.id.billName);
            this.billAmount = itemView.findViewById(R.id.billAmount);
            this.userImage = itemView.findViewById(R.id.billUserImage);
            this.layout = itemView.findViewById(R.id.selfContainer);

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
}
