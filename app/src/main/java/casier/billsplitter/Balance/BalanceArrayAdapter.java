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

public class BalanceArrayAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private final List<Bill> billList;
    private int itemRessource;
    private OnItemClicked onClick;

    public interface OnItemClicked {
        void onClick(int position);
    }

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

    public void setOnClick(OnItemClicked onClick) {
        this.onClick = onClick;
    }

    public Bill getItemAtPosition(int position){
        return billList.get(position);
    }


}
