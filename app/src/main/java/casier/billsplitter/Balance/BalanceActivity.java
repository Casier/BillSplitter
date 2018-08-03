package casier.billsplitter.Balance;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ListView;

import butterknife.BindView;
import butterknife.ButterKnife;
import casier.billsplitter.R;

public class BalanceActivity extends Activity {

    @BindView(R.id.billsList) ListView billsList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance);
        ButterKnife.bind(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}
