package casier.billsplitter.AddBill;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import casier.billsplitter.Model.User;
import casier.billsplitter.R;
import casier.billsplitter.UserDataObserver;
import casier.billsplitter.UserPickerAdapter;
import casier.billsplitter.Utils;

public class AddBillActivity extends Activity implements UserDataObserver {

    @BindView(R.id.screenTitle)
    TextView screenTitle;

    @BindView(R.id.bill_name)
    EditText billName;

    @BindView(R.id.bill_amount)
    EditText billAmount;

    @BindView(R.id.users_picker)
    RecyclerView usersPicker;

    private Utils mUtils;
    private UserPickerAdapter adapter;
    private AddBillPresenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bill);
        ButterKnife.bind(this);

        presenter = new AddBillPresenter(this);
        mUtils = Utils.getInstance();
        mUtils.registerUserObserver(this);

        //region Using custom font
        AssetManager am = getApplicationContext().getAssets();
        Typeface typeface = Typeface.createFromAsset(am,"fonts/HelloMilkMoney.ttf");
        //screenTitle.setTypeface(typeface);
        //endregion

        adapter = new UserPickerAdapter(this, R.layout.row_user_picker, mUtils.getUserList());
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        usersPicker.setLayoutManager(layoutManager);
        usersPicker.setHasFixedSize(true);
        usersPicker.setAdapter(adapter);
    }

    @Override
    public void onUserDataChange(List<User> userList) {
        adapter.notifyDataSetChanged();
    }

    @OnClick(R.id.bill_add)
    public void submit(View v){
        SparseBooleanArray pickedUsers = adapter.getItemStateArray();
        List<String> billUsersList = new ArrayList<>();
        List<User> userList = mUtils.getUserList();

        for(int i = 0 ; i < pickedUsers.size() ; i ++){
            if(pickedUsers.get(i)){
                billUsersList.add(userList.get(i).getUserId());
            }
        }

        presenter.addBill(billName.getText().toString(), billAmount.getText().toString(), billUsersList);
    }
}
