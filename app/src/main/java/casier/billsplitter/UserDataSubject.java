package casier.billsplitter;

public interface UserDataSubject {
    void registerUserObserver(UserDataObserver dataObserver);
    void removeUserObserver(UserDataObserver dataObserver);
    void notifyUserObservers();
}
