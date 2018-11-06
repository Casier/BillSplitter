package casier.billsplitter.CreateAccount;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import casier.billsplitter.AddBill.UserPickerAdapter;
import casier.billsplitter.Model.User;
import casier.billsplitter.R;
import casier.billsplitter.Utils;

public class CreateAccountActivity extends Activity {

    @BindView(R.id.account_user_picker_recycler)
    RecyclerView usersPicker;

    @BindView(R.id.account_name)
    EditText accountNameEditText;


    private CreateAccountPresenter presenter;
    private UserPickerAdapter adapter;
    private Utils mUtils;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        ButterKnife.bind(this);
        presenter = new CreateAccountPresenter(this);
        mUtils = Utils.getInstance();

        adapter = new UserPickerAdapter(this, R.layout.row_user_picker, mUtils.getUserList());
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        usersPicker.setLayoutManager(layoutManager);
        usersPicker.setHasFixedSize(true);
        usersPicker.setAdapter(adapter);
    }

    @OnClick(R.id.button_create_account)
    public void onClickCreateAccount(){

        String accountName = accountNameEditText.getText().toString();

        if(accountName.equals("")){
            Toast.makeText(this, "Veuillez nommer votre compte !", Toast.LENGTH_LONG).show();
            return;
        }
        SparseBooleanArray pickedUsers = adapter.getItemStateArray();
        List<User> billUsersList = new ArrayList<>();
        List<User> usersList = mUtils.getUserList();

        for(int i = 0 ; i < pickedUsers.size() ; i ++){
            if(pickedUsers.get(i)){
                billUsersList.add(usersList.get(i));
            }
        }

        presenter.createAccount(accountName, billUsersList);

        Intent intent = new Intent();
        setResult(1, intent);
        finish();
    }
}
