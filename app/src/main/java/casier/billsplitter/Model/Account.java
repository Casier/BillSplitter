package casier.billsplitter.Model;

import java.util.List;

import casier.billsplitter.Utils;

public class Account {

    private String accountName;
    private List<String> accountUsersId;
    private List<Bill> accountBills;

    public Account(String accountName, List<String> accountUsers){
        this.accountName = accountName;
        this.accountUsersId = accountUsers;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public List<String> getAccountUsers() {
        return accountUsersId;
    }

    public void setAccountUsers(List<String> accountUsers) {
        this.accountUsersId = accountUsers;
    }

    public void addUserToAccount(User user){
        if(!accountUsersId.contains(user.getUserId()))
            accountUsersId.add(user.getUserId());
    }

    public boolean removeUserToAccount(User user){
        boolean userBillFree = true;
        if(accountUsersId.contains(user.getUserId())){
             for(Bill b : Utils.getInstance().getBillList()){
                 if(b.getUsersId().contains(user.getUserId()))
                     userBillFree = false;
             }
        }
        if(userBillFree){
            // TODO : Supprimer l'utililsateur de la liste des utilisateurs de l'account
            accountUsersId.remove(user.getUserId());
        }
        return userBillFree;
    }
}
