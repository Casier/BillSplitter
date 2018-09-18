package casier.billsplitter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class BillPickerAdapter extends RecyclerView.Adapter implements View.OnClickListener {

    public interface OnItemClickListener {
        void onItemClick(String line);
    }

    private Context context;
    private int resource;
    private final List<String> amount;
    private final OnItemClickListener listener;

    public BillPickerAdapter(Context context, int resource, List<String> amount, OnItemClickListener listener){
        this.context = context;
        this.resource = resource;
        this.amount = amount;
        this.listener = listener;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder viewHolder = new BillPickerHolder(inflater.inflate(R.layout.row_bill_picker, parent, false));

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        BillPickerHolder billPickerHolder = (BillPickerHolder) holder;
        billPickerHolder.bindAmount(amount.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return amount.size();
    }

    @Override
    public void onClick(View view) {

    }

    public class BillPickerHolder extends RecyclerView.ViewHolder{

        private final TextView billAmount;

        BillPickerHolder(View itemView){
            super(itemView);
            this.billAmount = itemView.findViewById(R.id.ocr_bill_amount);

        }

        public void bindAmount(final String amount, final OnItemClickListener listener){
            billAmount.setText(amount);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(amount);
                }
            });
        }
    }
}
