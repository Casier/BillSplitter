package casier.billsplitter.Balance;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
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

    private final List<Bill> billList;
    private OnItemClicked onClick;
    private Utils mUtils;

    public interface OnItemClicked {
        void onClick(int position);
    }

    public BalanceArrayAdapter(List<Bill> billList) {
        this.billList = billList;
        this.mUtils = Utils.getInstance();
        setHasStableIds(true);
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
                billsSelfHolder.layout.setOnClickListener(view -> onClick.onClick(position));
                break;
            case 2:
                BillsHolder billsHolder = (BillsHolder) holder;
                billsHolder.bindBill(billList.get(position));
                billsHolder.layout.setOnClickListener(view -> onClick.onClick(position));
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

    public class BillsSelfHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {

        private Context context;
        private final TextView billFirstLine;
        private final TextView billSecondLine;
        private final CircleImageView userImage;
        public RelativeLayout layout;


        BillsSelfHolder(Context context, View itemView) {
            super(itemView);

            this.context = context;

            this.billFirstLine = itemView.findViewById(R.id.billFirstLine);
            this.billSecondLine = itemView.findViewById(R.id.billSecondLine);
            this.userImage = itemView.findViewById(R.id.billUserImage);
            this.layout = itemView.findViewById(R.id.selfContainer);

            itemView.setOnClickListener(this);
        }

        void bindBill(Bill bill){
            billFirstLine.setText(Html.fromHtml(bill.getTitle() + " - Montant : <b>" + bill.getAmount() + "$</b>"));
            billSecondLine.setText(Html.fromHtml("payé par <b>" + mUtils.getUserById(bill.getOwnerId()).getUserName() + "</b>"));

            Glide.with(context)
                    .load(Uri.parse(mUtils.getImageUrlByUserId(bill.getOwnerId())))
                    .into(userImage);
        }

        @Override
        public void onClick(View view) {

        }
    }

    public class BillsHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Context context;
        private final TextView billFirstLine;
        private final TextView billSecondLine;
        private final CircleImageView userImage;
        public RelativeLayout layout;

        BillsHolder(Context context, View itemView) {
            super(itemView);

            this.context = context;

            this.billFirstLine = itemView.findViewById(R.id.billFirstLine);
            this.billSecondLine = itemView.findViewById(R.id.billSecondLine);
            this.userImage = itemView.findViewById(R.id.billUserImage);
            this.layout = itemView.findViewById(R.id.globalContainer);

            itemView.setOnClickListener(this);
        }

        void bindBill(Bill bill){

            billFirstLine.setText(Html.fromHtml(bill.getTitle() + " - Montant : <b>" + bill.getAmount() + "$</b>"));
            billSecondLine.setText(Html.fromHtml("payé par <b>" + mUtils.getUserById(bill.getOwnerId()).getUserName() + "</b>"));

            Glide.with(context)
                    .load(Uri.parse(Utils.getInstance().getImageUrlByUserId(bill.getOwnerId())))
                    .into(userImage);
        }

        @Override
        public void onClick(View view) {

        }
    }
}
