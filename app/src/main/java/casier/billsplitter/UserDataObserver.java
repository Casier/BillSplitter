package casier.billsplitter;

import java.util.List;

import casier.billsplitter.Model.User;

public interface UserDataObserver {
    void onUserDataChange(List<User> userList);
}
