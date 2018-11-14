package casier.billsplitter.Account;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import casier.billsplitter.Balance.DialogUserAdapter;
import casier.billsplitter.Model.Account;
import casier.billsplitter.R;

public class AccountPickerAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<Account> accountList;
    private int itemResource;
    private OnItemClicked onClick;

    public interface OnItemClicked{
        void onClick(int position);
        void onSettingsClick(int position);
    }

    public AccountPickerAdapter(Context context, int resource, List<Account> accountList){
        this.context = context;
        this.itemResource = resource;
        this.accountList = new ArrayList<>(accountList);
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder viewHolder = new AccountPickerHolder(inflater.inflate(itemResource, parent, false));

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        AccountPickerHolder accountPickerHolder = (AccountPickerHolder) holder;
        ((AccountPickerHolder) holder).layout.setOnClickListener(view -> onClick.onClick(position));
        ((AccountPickerHolder) holder).settings.setOnClickListener(view -> onClick.onSettingsClick(position));

        accountPickerHolder.bindAccount(accountList.get(position), accountList.get(position).getAccountUsers());
    }

    @Override
    public int getItemCount() {
        return accountList.size();
    }

    public void setAccountList(List<Account> accountList){
        this.accountList = new ArrayList<>(accountList);
    }

    public void setOnclick(OnItemClicked onClick){
        this.onClick = onClick;
    }

    public class AccountPickerHolder extends RecyclerView.ViewHolder{

        private final TextView accountName;
        private final RelativeLayout layout;
        private final ImageView settings;
        private final RecyclerView userRecycler;
        private DialogUserAdapter adapter;

        public AccountPickerHolder(View itemView) {
            super(itemView);
            this.accountName = itemView.findViewById(R.id.account_name);
            this.layout = itemView.findViewById(R.id.account_picker_layout);
            this.settings = itemView.findViewById(R.id.account_settings);
            this.userRecycler = itemView.findViewById(R.id.account_user_list);
        }

        public void bindAccount(Account account, List<String> userList) {
            accountName.setText(account.getAccountName());
            adapter = new DialogUserAdapter(context, R.layout.row_dialog_user, userList);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            userRecycler.setLayoutManager(layoutManager);
            userRecycler.setHasFixedSize(true);
            userRecycler.setAdapter(adapter);
        }
    }
}
