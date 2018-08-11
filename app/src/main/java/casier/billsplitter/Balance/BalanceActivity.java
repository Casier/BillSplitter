package casier.billsplitter.Balance;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import casier.billsplitter.DataObserver;
import casier.billsplitter.Model.Bill;
import casier.billsplitter.R;

public class BalanceActivity extends Activity implements DataObserver{

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
        BalanceArrayAdapter adapter = new BalanceArrayAdapter(this, R.layout.row_bill_layout, billList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        billsList.setLayoutManager(layoutManager);
        billsList.setHasFixedSize(true);
        billsList.setAdapter(adapter);
    }

    @Override
    public void onDataChange(List<Bill> billList, Map<String, String> usersImageUrl) {
        if(billList.size() > 0 && usersImageUrl.size() > 0){
            notifyAdapter(billList);
        }
    }
}
