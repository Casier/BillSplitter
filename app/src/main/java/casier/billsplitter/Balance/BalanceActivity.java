package casier.billsplitter.Balance;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import casier.billsplitter.AccountSettings.AccountSettingsActivity;
import casier.billsplitter.BillDataObserver;
import casier.billsplitter.Model.Balance;
import casier.billsplitter.Model.Bill;
import casier.billsplitter.Model.User;
import casier.billsplitter.R;
import casier.billsplitter.UserDataObserver;
import casier.billsplitter.Utils;
import de.hdodenhof.circleimageview.CircleImageView;

public class BalanceActivity extends Activity implements BalanceArrayAdapter.OnItemClicked, BillDataObserver, UserDataObserver, View.OnClickListener{

    //TODO probablement de la redondance ici, à fix.

    //TODO balancer toute la logique non-UI vers le presenter


    @BindView(R.id.billsList)
    RecyclerView billRecycler;

    @BindView(R.id.balance_recycler)
    RecyclerView balanceRecycler;

    @BindView(R.id.floating_add_bill)
    android.support.design.widget.FloatingActionButton fab;

    @BindView(R.id.balance_placeholder_one)
    RelativeLayout placeholderTop;

    @BindView(R.id.balance_placeholder_two)
    RelativeLayout placeholderBottom;

    @BindView(R.id.placeholder_arrow)
    ImageView placeholderArrow;

    private BalancePresenter presenter;
    private BalanceArrayAdapter balanceArrayAdapter;
    private Utils mUtils;

    static final int ADD_BILL_REQUEST = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance);
        ButterKnife.bind(this);
        presenter = new BalancePresenter(this);
        mUtils = Utils.getInstance();
        mUtils.registerBillObserver(this);
        mUtils.registerUserObserver(this);
        initializeBalanceArrayAdapter();
        doTheBalance();
        fab.setOnClickListener(this);
    }

    public void initializeBalanceArrayAdapter(){
        balanceArrayAdapter = new BalanceArrayAdapter(this, R.layout.row_bill_layout, mUtils.getSelectedAccount().getBills());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        billRecycler.setLayoutManager(layoutManager);
        billRecycler.setHasFixedSize(true);
        billRecycler.setAdapter(balanceArrayAdapter);
        balanceArrayAdapter.setOnClick(BalanceActivity.this);
        checkIfPlaceholder();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUtils.removeBillObserver(this);
        mUtils.removeUserObserver(this);
    }

    public void checkIfPlaceholder(){
        if(balanceArrayAdapter.getItemCount() == 0){
            placeholderTop.setVisibility(View.VISIBLE);
            placeholderBottom.setVisibility(View.VISIBLE);
            placeholderArrow.setVisibility(View.VISIBLE);
        } else {
            placeholderTop.setVisibility(View.GONE);
            placeholderBottom.setVisibility(View.GONE);
            placeholderArrow.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(final int position) {

        Bill clickedBill = mUtils.getBillList().get(position);

        final Dialog dialog = new Dialog(this, android.R.style.Theme_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_show_bill_info);
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        TextView billName = dialog.findViewById(R.id.dialog_bill_name);
        final TextView billAmount = dialog.findViewById(R.id.dialog_bill_amout);
        CircleImageView userImage = dialog.findViewById(R.id.dialog_user_image);
        Button deleteBill = dialog.findViewById(R.id.dialog_remove_bill);
        RecyclerView userRecycler = dialog.findViewById(R.id.dialog_recycler);

        DialogUserAdapter dialogUserAdapter = new DialogUserAdapter(this, clickedBill.getUsersId());
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 4);
        userRecycler.setLayoutManager(layoutManager);
        userRecycler.setHasFixedSize(true);
        userRecycler.setAdapter(dialogUserAdapter);

        billName.setText(clickedBill.getTitle());
        billAmount.setText(clickedBill.getAmount() + "$");

        deleteBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUtils.deleteBill(mUtils.getBillList().get(position));
                dialog.dismiss();
            }
        });

        Glide.with(this)
                .load(Uri.parse(mUtils.getImageUrlByUserId(mUtils.getBillList().get(position).getOwnerId())))
                .into(userImage);
        dialog.show();
    }

    @Override
    public void onClick(View view) {
        fab.setEnabled(false);
        Intent intent = new Intent(this, AccountSettingsActivity.class);
        //Intent intent = new Intent(this, AddBillActivity.class);
        startActivityForResult(intent, ADD_BILL_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        fab.setEnabled(true);
        if(requestCode ==  ADD_BILL_REQUEST){
            if(resultCode == 1){
                doTheBalance();
            } else if(resultCode == 2){
                finish();
            }
        }
    }

    public void doTheBalance(){
        if(mUtils.getUserList() == null || mUtils.getUserList().size() == 0)
            return;

        for(User u : mUtils.getUserList())
            u.clearBalance();

        for(Bill b : mUtils.getBillList()){
            if(b.getUsersId() != null)
            {
                for(String s : b.getUsersId()){
                    User u = mUtils.getUserById(s);
                    if(!u.getUserId().equals(b.getOwnerId())){
                        Float amountBeforeSplit = Float.valueOf(b.getAmount());
                        Float finalAmount = amountBeforeSplit / b.getUsersId().size();

                        Float finalRoundedAmount =  Math.round(finalAmount*100.0)/100.0f;
                        for(String userToAddBill : b.getUsersId()){
                            if(!userToAddBill.equals(u.getUserId())){
                                u.addToBalance(mUtils.getUserById(userToAddBill), finalRoundedAmount);
                            }
                        }
                    }
                }
            }
        }

        for(User payer : mUtils.getUserList()){
            for(User paid : mUtils.getUserList()){
                if(payer.getUsersBalance() !=  null){
                    if(payer.getUsersBalance().containsKey(paid) && paid.getUsersBalance() != null){
                        Float payerAmount = payer.getUsersBalance().get(paid);
                        Float paidAmount  = paid.getUsersBalance().get(payer);

                        if(payerAmount != null && paidAmount != null){
                            if(payerAmount > paidAmount){
                                payer.getUsersBalance().put(paid, payerAmount - paidAmount);
                                paid.getUsersBalance().remove(payer);
                            } else if (payerAmount.equals(paidAmount)){
                                payer.getUsersBalance().remove(paid);
                                paid.getUsersBalance().remove(payer);
                            } else {
                                paid.getUsersBalance().put(payer, paidAmount - payerAmount);
                                payer.getUsersBalance().remove(paid);
                            }
                        }
                    }
                }
            }
        }

        List<Balance> balanceList = new ArrayList<>();
        for(User u : mUtils.getUserList()){
            if(u.getUsersBalance() != null){
                for(Map.Entry<User, Float> entry : u.getUsersBalance().entrySet()){
                    balanceList.add(new Balance(u.getUserId(), entry.getKey().getUserId(), entry.getValue()));
                }
            }
        }

        BalanceSummaryAdapter balanceSummaryAdapter = new BalanceSummaryAdapter(this, balanceList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        balanceRecycler.setLayoutManager(layoutManager);
        balanceRecycler.setHasFixedSize(true);
        balanceRecycler.setAdapter(balanceSummaryAdapter);

    }

    @Override
    public void onUserDataChange(List<User> userList) {
        doTheBalance();
    }

    @Override
    public void onBillDataChange(List<Bill> billList) {
        balanceArrayAdapter.notifyDataSetChanged();
        doTheBalance();
        checkIfPlaceholder();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
