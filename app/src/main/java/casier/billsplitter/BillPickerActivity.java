package casier.billsplitter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BillPickerActivity extends AppCompatActivity implements View.OnClickListener {

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

        adapter = new BillPickerAdapter(this, R.layout.row_bill_picker, lines);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(layoutManager);
        recycler.setHasFixedSize(true);
        recycler.setAdapter(adapter);

    }

    @Override
    public void onClick(View view) {

    }
}
