package casier.billsplitter;

public interface FriendDataSubject {
    void registerFriendObserver(FriendDataObserver dataObserver);
    void removeFriendObserver(FriendDataObserver dataObserver);
    void notifyFriendsObservers();
}
