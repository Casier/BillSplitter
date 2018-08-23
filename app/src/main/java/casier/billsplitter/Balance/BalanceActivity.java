package casier.billsplitter.Balance;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import casier.billsplitter.DataObserver;
import casier.billsplitter.Model.Bill;
import casier.billsplitter.Model.UserInfo;
import casier.billsplitter.R;
import casier.billsplitter.Utils;

public class BalanceActivity extends Activity implements DataObserver, BalanceArrayAdapter.OnItemClicked{

    @BindView(R.id.billsList)
    RecyclerView billsList;

    private BalancePresenter presenter;
    private BalanceArrayAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance);
        ButterKnife.bind(this);
        presenter = new BalancePresenter(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void notifyAdapter(List<Bill> billList){
        adapter = new BalanceArrayAdapter(this, R.layout.row_bill_layout, billList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        billsList.setLayoutManager(layoutManager);
        billsList.setHasFixedSize(true);
        billsList.setAdapter(adapter);
        adapter.setOnClick(BalanceActivity.this);
    }

    @Override
    public void onDataChange(List<Bill> billList, Map<String, String> usersImageUrl) {
        if(billList.size() > 0 && usersImageUrl.size() > 0){
            notifyAdapter(billList);
        }
    }

    @Override
    public void onClick(int position) {
        final Bill bill = adapter.getItemAtPosition(position);
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this, R.style.MyDialogTheme);
        dialog.setTitle(bill.getTitle());
        dialog.setMessage(bill.getAmount());
        dialog.setPositiveButton("Ok", null);
        if(bill.getOwnerId().equals(UserInfo.getInstance().getUserId())){
            dialog.setNegativeButton("Supprimer", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    presenter.removeBill(bill);
                }
            });
        }

        Glide.with(this)
                .load(Uri.parse(Utils.getInstance().getImageUrlByUserId(bill.getOwnerId())))
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        dialog.setIcon(resource);
                        dialog.show();
                    }
                });
    }
}
