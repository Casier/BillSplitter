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

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import casier.billsplitter.AddBill.AddBillActivity;
import casier.billsplitter.Model.Balance;
import casier.billsplitter.Model.Bill;
import casier.billsplitter.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class BalanceActivity extends Activity implements BalanceArrayAdapter.OnItemClicked, View.OnClickListener{

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
    public BalanceArrayAdapter balanceArrayAdapter;

    static final int ADD_BILL_REQUEST = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance);
        ButterKnife.bind(this);
        fab.setOnClickListener(this);

        presenter = new BalancePresenter(this);

        balanceArrayAdapter = new BalanceArrayAdapter(presenter.getSelectedAccountBills());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        billRecycler.setLayoutManager(layoutManager);
        billRecycler.setHasFixedSize(true);
        billRecycler.setAdapter(balanceArrayAdapter);
        balanceArrayAdapter.setOnClick(BalanceActivity.this);

        checkIfPlaceholder();
        doTheBalance();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.clear();
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

        Bill clickedBill = presenter.getBillAtPosition(position);

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

        DialogUserAdapter dialogUserAdapter = new DialogUserAdapter(this, R.layout.row_dialog_user, clickedBill.getUsersId());
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 4);
        userRecycler.setLayoutManager(layoutManager);
        userRecycler.setHasFixedSize(true);
        userRecycler.setAdapter(dialogUserAdapter);

        billName.setText(clickedBill.getTitle());
        billAmount.setText(clickedBill.getAmount() + "$");

        deleteBill.setOnClickListener(view -> {
            presenter.deleteBill(position);
            dialog.dismiss();
        });

        Glide.with(this)
                .load(Uri.parse(presenter.getOwnerImageUrl(position)))
                .into(userImage);
        dialog.show();
    }

    @Override
    public void onClick(View view) {
        fab.setEnabled(false);
        Intent intent = new Intent(this, AddBillActivity.class);
        startActivityForResult(intent, ADD_BILL_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        fab.setEnabled(true);
        if(requestCode ==  ADD_BILL_REQUEST){
            switch(resultCode){
                case 1:
                    doTheBalance();
                    break;
                case 2:
                    finish();
            }
        }
    }

    public void doTheBalance(){
        List<Balance> balanceList = presenter.doTheBalance();
        if(balanceList == null)
            return;

        BalanceSummaryAdapter balanceSummaryAdapter = new BalanceSummaryAdapter(this, balanceList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        balanceRecycler.setLayoutManager(layoutManager);
        balanceRecycler.setHasFixedSize(true);
        balanceRecycler.setAdapter(balanceSummaryAdapter);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
