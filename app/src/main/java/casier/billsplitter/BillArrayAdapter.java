package casier.billsplitter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;

import java.util.HashMap;
import java.util.List;

public class BillArrayAdapter extends ArrayAdapter<String> {

    HashMap<String, Integer> mIdMap = new HashMap<>();


    public BillArrayAdapter(@NonNull Context context, int resource, List<String> objects) {
        super(context, resource, objects);
        if(objects != null){
            for (int i = 0; i < objects.size(); ++i) {
                mIdMap.put(objects.get(i), i);
            }
        }
    }

    public void setmIdMap(List<String> objects){
        for (int i = 0; i < objects.size(); ++i) {
            mIdMap.put(objects.get(i), i);
        }
    }

    @Override
    public long getItemId(int position) {
        String item = getItem(position);
        return mIdMap.get(item);
    }

    public String getItemValue(int position) {
        return mIdMap.get(position).toString();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

}
