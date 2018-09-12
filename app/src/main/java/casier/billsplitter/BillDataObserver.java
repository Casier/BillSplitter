package casier.billsplitter;

import java.util.List;

import casier.billsplitter.Model.Bill;

public interface BillDataObserver {
    void onBillDataChange(List<Bill> billList);
}
