package casier.billsplitter.AccountSettings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.widget.EditText;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import casier.billsplitter.AddBill.UserPickerAdapter;
import casier.billsplitter.Model.User;
import casier.billsplitter.R;

public class AccountSettingsActivity extends Activity {

    @BindView(R.id.account_settings_name)
    EditText accountNameEditText;

    @BindView(R.id.account_settings_users)
    RecyclerView accountUsersRecycler;

    private AccountSettingsPresenter presenter;
    private UserPickerAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);
        ButterKnife.bind(this);
        presenter = new AccountSettingsPresenter(this);
        presenter.setInitialData();
    }

    public void setAccountName(String accountName){
        accountNameEditText.setText(accountName);
    }

    public void setAccountUsers(List<User> accountUserList){
        adapter = new UserPickerAdapter(this, R.layout.row_user_picker, accountUserList);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        accountUsersRecycler.setLayoutManager(layoutManager);
        accountUsersRecycler.setHasFixedSize(true);
        accountUsersRecycler.setAdapter(adapter);
    }

    @OnClick(R.id.account_settings_delete)
    public void onDeleteAccount(){
        presenter.deleteAccount();
        Intent intent = new Intent();
        setResult(2, intent);
        this.finish();
    }

    @OnClick(R.id.account_settings_save)
    public void onUpdateAccout(){
        String accountName = accountNameEditText.getText().toString();
        SparseBooleanArray pickedUsers = adapter.getItemStateArray();
        presenter.updateAccount(accountNameEditText.getText().toString(), pickedUsers);
        Intent intent = new Intent();
        setResult(3, intent);
        this.finish();
    }
}
