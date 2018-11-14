package casier.billsplitter.CreateAccount;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import casier.billsplitter.AddBill.UserPickerAdapter;
import casier.billsplitter.R;

public class CreateAccountActivity extends Activity {

    @BindView(R.id.account_user_picker_recycler)
    RecyclerView usersPicker;

    @BindView(R.id.account_name)
    EditText accountNameEditText;

    @BindView(R.id.account_users_search)
    EditText userSearch;


    private CreateAccountPresenter presenter;
    private UserPickerAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        ButterKnife.bind(this);
        presenter = new CreateAccountPresenter(this);

        adapter = new UserPickerAdapter(this, R.layout.row_user_picker, presenter.getUserFriendList());
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        usersPicker.setLayoutManager(layoutManager);
        usersPicker.setHasFixedSize(true);
        usersPicker.setAdapter(adapter);
    }

    @OnClick(R.id.button_create_account)
    public void onClickCreateAccount() {

        String accountName = accountNameEditText.getText().toString();

        if (accountName.equals("")) {
            Toast.makeText(this, "Veuillez nommer votre compte !", Toast.LENGTH_LONG).show();
            return;
        }

        presenter.createAccount(accountName, adapter.getSelectedUserList());
        Intent intent = new Intent();
        setResult(1, intent);
        finish();
    }

    @OnTextChanged(R.id.account_users_search)
    public void onSearchTextChanged(Editable editable){
        adapter.setUserList(presenter.getFilteredUserFriendList(editable.toString()));
        adapter.notifyDataSetChanged();
    }

    @OnClick(R.id.search_clear_text)
    public void clearSearch(){
        userSearch.setText(""); // trigger onSearchTextChanged
    }

}
