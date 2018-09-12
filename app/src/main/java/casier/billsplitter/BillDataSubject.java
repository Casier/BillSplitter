package casier.billsplitter;

public interface BillDataSubject {
    void registerBillObserver(BillDataObserver dataObserver);
    void removeBillObserver(BillDataObserver dataObserver);
    void notifyBillObservers();
}
