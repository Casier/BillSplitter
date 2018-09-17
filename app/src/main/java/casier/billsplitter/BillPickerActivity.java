package casier.billsplitter;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import casier.billsplitter.AddBill.AddBillActivity;

public class BillPickerActivity extends AppCompatActivity implements BillPickerAdapter.OnItemClickListener {

    @BindView(R.id.recycler_pick_bill)
    RecyclerView recycler;

    private BillPickerAdapter adapter;

    private List<String> lines = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_picker);
        ButterKnife.bind(this);

        lines = getIntent().getStringArrayListExtra("lines");

        adapter = new BillPickerAdapter(this, R.layout.row_bill_picker, lines, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 4);
        recycler.setLayoutManager(layoutManager);
        recycler.setHasFixedSize(true);
        recycler.setAdapter(adapter);

    }

    @Override
    public void onItemClick(String line) {
        line = line.replace(",", ".").replace(" ", "");
        for(String s : Utils.getInstance().getCurrency()){
            line = line.replace(s, "");
        }
        Intent intent = new Intent(this, AddBillActivity.class);
        intent.putExtra("line", line);
        startActivity(intent);
    }
}
