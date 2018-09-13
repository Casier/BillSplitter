package casier.billsplitter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

public class BillPickerActivity extends AppCompatActivity {

    private List<String> lines = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_picker);
        ButterKnife.bind(this);

        lines = getIntent().getStringArrayListExtra("lines");
        Log.d("panda", "test");

    }
}
