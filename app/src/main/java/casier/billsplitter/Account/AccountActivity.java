package casier.billsplitter.Account;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import casier.billsplitter.AccountDataObserver;
import casier.billsplitter.Balance.BalanceActivity;
import casier.billsplitter.Model.Account;
import casier.billsplitter.R;
import casier.billsplitter.Utils;

public class AccountActivity extends Activity implements AccountDataObserver, AccountPickerAdapter.OnItemClicked {

    @BindView(R.id.recycler_account_picker)
    RecyclerView accountPicker;

    private Utils mUtils;
    private AccountPresenter presenter;

    private AccountPickerAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        ButterKnife.bind(this);

        presenter = new AccountPresenter(this);

        mUtils = Utils.getInstance();
        mUtils.registerAccountObserver(this);

        adapter = new AccountPickerAdapter(this, R.layout.row_account_picker, mUtils.getAccountList());
        adapter.setOnclick(AccountActivity.this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        accountPicker.setLayoutManager(layoutManager);
        accountPicker.setHasFixedSize(true);
        accountPicker.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUtils.removeAccountObserver(this);
    }

    @Override
    public void onAccountDataChange(List<Account> accountList) {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(int position) {
        mUtils.setSelectedAccount(mUtils.getAccountList().get(position));
        Intent intent = new Intent(this, BalanceActivity.class);
        startActivity(intent);
    }
}
