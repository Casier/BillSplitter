package casier.billsplitter.Account;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import casier.billsplitter.Model.Account;
import casier.billsplitter.R;

public class AccountPickerAdapter extends RecyclerView.Adapter {

    private Context context;
    private final List<Account> accountList;
    private int itemResource;

    public AccountPickerAdapter(Context context, int resource, List<Account> accountList){
        this.context = context;
        this.itemResource = resource;
        this.accountList = accountList;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder viewHolder = new AccountPickerHolder(inflater.inflate(itemResource, parent, false));

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        AccountPickerHolder accountPickerHolder = (AccountPickerHolder) holder;
        accountPickerHolder.bindAccount(accountList.get(position));
    }

    @Override
    public int getItemCount() {
        return accountList.size();
    }

    public class AccountPickerHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView accountName;

        public AccountPickerHolder(View itemView) {
            super(itemView);
            this.accountName = itemView.findViewById(R.id.account_name);
            itemView.setOnClickListener(this);
        }

        public void bindAccount(Account account){
            accountName.setText(account.getAccountName());
        }

        @Override
        public void onClick(View view) {

        }
    }
}
