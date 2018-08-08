package casier.billsplitter.Balance;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import casier.billsplitter.Model.Bill;
import casier.billsplitter.Model.UserInfo;
import casier.billsplitter.R;

public class BalanceArrayAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context context;
    private final List<Bill> billList;
    private int itemRessource;

    public BalanceArrayAdapter(@NonNull Context context, int resource, List<Bill> billList) {

        this.itemRessource = resource;
        this.context = context;
        this.billList = billList;
    }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            RecyclerView.ViewHolder viewHolder = null;
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());

            Log.d("panda", "viewType : " + Integer.toString(viewType));

            switch (viewType) {
                case 1 :
                    View billSelfView = inflater.inflate(R.layout.row_bill_self_layout, parent, false);
                    viewHolder = new BillsSelfHolder(parent.getContext(), billSelfView);
                    break;
                case 2:
                    View billView = inflater.inflate(R.layout.row_bill_layout, parent, false);
                    viewHolder = new BillsHolder(parent.getContext(), billView);
                    break;
            }

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            switch (holder.getItemViewType()){
                case 1:
                    BillsSelfHolder billsSelfHolder = (BillsSelfHolder) holder;
                    billsSelfHolder.bindBill(billList.get(position));
                    break;
                case 2:
                    BillsHolder billsHolder = (BillsHolder) holder;
                    billsHolder.bindBill(billList.get(position));
                    break;
            }
        }



        @Override
            public int getItemViewType(int position) {
                if (billList.get(position).getOwnerId().equals(UserInfo.getInstance().getUserId())) {
                    return 1;
                } else {
                    return 2;
                }
            }

            @Override
            public int getItemCount() {
                return this.billList.size();
            }
    }
