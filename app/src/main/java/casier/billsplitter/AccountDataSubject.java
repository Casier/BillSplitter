package casier.billsplitter;

public interface AccountDataSubject {
    void registerAccountObserver(AccountDataObserver dataObserver);
    void removeAccountObserver(AccountDataObserver dataObserver);
    void notifyAccountObservers();
}
