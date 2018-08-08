package casier.billsplitter.Balance;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ListView;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import casier.billsplitter.DataObserver;
import casier.billsplitter.Model.Bill;
import casier.billsplitter.R;

public class BalanceActivity extends Activity implements DataObserver{

    @BindView(R.id.billsList) ListView billsList;

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
        if(adapter == null){
            adapter = new BalanceArrayAdapter(this, R.layout.row_bill_layout , billList);
            adapter.setPresenter(presenter);
            billsList.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } else {
            adapter.updateData(billList);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDataChange(List<Bill> billList, Map<String, String> usersImageUrl) {
        if(billList.size() > 0 && usersImageUrl.size() > 0){
            notifyAdapter(billList);
        }
    }
}
