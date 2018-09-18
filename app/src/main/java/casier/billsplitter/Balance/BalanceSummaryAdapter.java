package casier.billsplitter.Balance;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import casier.billsplitter.Model.Balance;
import casier.billsplitter.R;
import casier.billsplitter.Utils;
import de.hdodenhof.circleimageview.CircleImageView;

public class BalanceSummaryAdapter extends RecyclerView.Adapter {

    private List<Balance> balanceList;
    private Context context;
    private Utils mUtils;

    public BalanceSummaryAdapter(Context context, List<Balance> balanceList){
        this.balanceList = balanceList;
        this.context = context;
        mUtils = Utils.getInstance();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder viewHolder = new BalanceSummaryHolder(inflater.inflate(R.layout.row_balance_summary, parent, false));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        BalanceSummaryHolder balanceSummaryHolder = (BalanceSummaryHolder) holder;
        balanceSummaryHolder.bindBalance(balanceList.get(position));
    }

    @Override
    public int getItemCount() {
        return balanceList.size();
    }

    public class BalanceSummaryHolder extends RecyclerView.ViewHolder {

        private CircleImageView payerPicture;
        private TextView balanceText;
        private CircleImageView paidPicture;

        public BalanceSummaryHolder(View itemView) {
            super(itemView);
            this.payerPicture = itemView.findViewById(R.id.payer_picture);
            this.balanceText = itemView.findViewById(R.id.balance_text);
            this.paidPicture = itemView.findViewById(R.id.paid_picture);
        }

        void bindBalance(Balance balance){
            Glide.with(context)
                    .load(Uri.parse(mUtils.getImageUrlByUserId(balance.getPayerId())))
                    .into(payerPicture);

            Glide.with(context)
                    .load(Uri.parse(mUtils.getImageUrlByUserId(balance.getPaidId())))
                    .into(paidPicture);

            balanceText.setText(" doit payer " + balance.getAmount().toString() + "$ à ");
        }
    }
}
