package casier.billsplitter;

public interface DataSubject {
    void registerObserver(DataObserver dataObserver);
    void removeObserver(DataObserver dataObserver);
    void notifyObservers();
}
