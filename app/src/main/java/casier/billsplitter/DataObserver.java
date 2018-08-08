package casier.billsplitter;

import java.util.List;
import java.util.Map;

import casier.billsplitter.Model.Bill;

public interface DataObserver {
    void onDataChange(List<Bill> billList, Map<String, String> usersImageUrl);
}
