package casier.billsplitter.Account;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import casier.billsplitter.AccountDataObserver;
import casier.billsplitter.AccountSettings.AccountSettingsActivity;
import casier.billsplitter.Balance.BalanceActivity;
import casier.billsplitter.FriendSearch.FriendSearchActivity;
import casier.billsplitter.Model.Account;
import casier.billsplitter.R;
import casier.billsplitter.Utils;

public class AccountActivity extends Activity implements AccountDataObserver, AccountPickerAdapter.OnItemClicked {

    @BindView(R.id.recycler_account_picker)
    RecyclerView accountPicker;

    @BindView(R.id.account_icn)
    ImageView accountIcon;

    @BindView(R.id.account_tv)
    TextView accountText;

    @BindView(R.id.balance_icn)
    ImageView balanceIcon;

    @BindView(R.id.balance_tv)
    TextView balanceText;

    @BindView(R.id.pie_icn)
    ImageView pieIcon;

    @BindView(R.id.pie_tv)
    TextView pieText;

    @BindView(R.id.settings_icn)
    ImageView settingsIcon;

    @BindView(R.id.settings_tv)
    TextView settingsText;

    @BindView(R.id.account_title)
    TextView titleScreen;

    @BindView(R.id.account_placeholder_layout)
    RelativeLayout placeholder;

    private Utils mUtils;
    private AccountPresenter presenter;

    private AccountPickerAdapter adapter;

    static final int CREATE_ACCOUNT_REQUEST = 1;

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
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        accountPicker.setLayoutManager(layoutManager);
        accountPicker.setHasFixedSize(true);
        accountPicker.setAdapter(adapter);
        checkIfPlaceholder();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUtils.removeAccountObserver(this);
    }

    @Override
    public void onAccountDataChange(List<Account> accountList) {
        adapter.notifyDataSetChanged();
        checkIfPlaceholder();
    }

    public void checkIfPlaceholder(){
        if(adapter.getItemCount() == 0){
            placeholder.setVisibility(View.VISIBLE);
            titleScreen.setVisibility(View.GONE);
        } else {
            placeholder.setVisibility(View.GONE);
            titleScreen.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(int position) {
        presenter.setSelectedAccount(position);
        Intent intent = new Intent(this, BalanceActivity.class);
        startActivity(intent);
    }

    @Override
    public void onSettingsClick(int position) {
        presenter.setSelectedAccount(position);
        Intent intent = new Intent(this, AccountSettingsActivity.class);
        startActivityForResult(intent, 55);
    }

    //region top menu tbd
    @OnClick(R.id.account_layout)
    public void onAccountClick(){
        int finalWidth = ((View) accountText.getParent()).getMeasuredWidth();
        ValueAnimator widthAnimator = ValueAnimator.ofInt(0, finalWidth);
        widthAnimator.setDuration(1500);
        widthAnimator.setInterpolator(new DecelerateInterpolator());
        widthAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                accountText.getLayoutParams().width = (int) animation.getAnimatedValue();
                accountText.requestLayout();
            }
        });
        widthAnimator.start();
        balanceText.setWidth(0);
        pieText.setWidth(0);
        settingsText.setWidth(0);

        accountIcon.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
    }

    @OnClick(R.id.balance_layout)
    public void onBalanceClick(){
        accountText.setWidth(0);
        int finalWidth = ((View) balanceText.getParent()).getMeasuredWidth();
        Animation animation = new ScaleAnimation(0, finalWidth, balanceText.getY(), balanceText.getY());
        animation.setFillBefore(false);
        animation.setDuration(500);
        balanceText.startAnimation(animation);
        ValueAnimator widthAnimator = ValueAnimator.ofInt(0, finalWidth);
        widthAnimator.setDuration(1500);
        widthAnimator.setInterpolator(new DecelerateInterpolator());
        widthAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                balanceText.getLayoutParams().width = (int) animation.getAnimatedValue();
                balanceText.requestLayout();
            }
        });
        //widthAnimator.start();
        accountIcon.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.text), android.graphics.PorterDuff.Mode.MULTIPLY);
        pieText.setVisibility(View.GONE);
        settingsText.setVisibility(View.GONE);
    }

    @OnClick(R.id.pie_layout)
    public void onPieClick(){
        accountText.setVisibility(View.GONE);
        balanceText.setVisibility(View.GONE);
        pieText.setVisibility(View.VISIBLE);
        settingsText.setVisibility(View.GONE);
    }

    @OnClick(R.id.settings_layout)
    public void onSettingsClick(){
        accountText.setVisibility(View.GONE);
        balanceText.setVisibility(View.GONE);
        pieText.setVisibility(View.GONE);
        settingsText.setVisibility(View.VISIBLE);
    }

    //endregion

    private static final int REQUEST_INVITE = 0;

    @OnClick(R.id.account_add)
    public void onAddClick(){
        Intent intent = new Intent(this, FriendSearchActivity.class);
        //Intent intent = new Intent(this, CreateAccountActivity.class);
        startActivityForResult(intent, CREATE_ACCOUNT_REQUEST);

    }
}
