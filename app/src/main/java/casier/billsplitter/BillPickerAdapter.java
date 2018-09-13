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

    private Context context;
    private int resource;
    private final List<String> amount;

    public BillPickerAdapter(Context context, int resource, List<String> amount){
        this.context = context;
        this.resource = resource;
        this.amount = amount;
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
        billPickerHolder.bindAmount(amount.get(position));
    }

    @Override
    public int getItemCount() {
        return amount.size();
    }

    @Override
    public void onClick(View view) {

    }

    public class BillPickerHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private final TextView billAmount;

        public BillPickerHolder(View itemView){
            super(itemView);
            this.billAmount = itemView.findViewById(R.id.ocr_bill_amount);
            itemView.setOnClickListener(this);

        }

        public void bindAmount(String amount){
            billAmount.setText(amount);
        }

        @Override
        public void onClick(View view) {

        }
    }
}
