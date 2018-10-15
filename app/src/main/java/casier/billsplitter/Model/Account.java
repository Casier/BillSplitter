package casier.billsplitter.Model;

import com.google.firebase.database.Exclude;

import java.util.List;

import casier.billsplitter.Utils;

public class Account {

    @Exclude
    private String accountName;
    private List<String> usersId;
    @Exclude
    private List<Bill> bills;

    public Account(){

    }

    public Account(List<String> usersId, List<Bill> bills){
        this.bills = bills;
        this.usersId = usersId;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public List<String> getAccountUsers() {
        return usersId;
    }

    public void setAccountUsers(List<String> accountUsers) {
        this.usersId = accountUsers;
    }

    public void addUserToAccount(User user){
        if(!usersId.contains(user.getUserId()))
            usersId.add(user.getUserId());
    }

    public boolean removeUserToAccount(User user){
        boolean userBillFree = true;
        if(usersId.contains(user.getUserId())){
             for(Bill b : Utils.getInstance().getBillList()){
                 if(b.getUsersId().contains(user.getUserId()))
                     userBillFree = false;
             }
        }
        if(userBillFree){
            // TODO : Supprimer l'utililsateur de la liste des utilisateurs de l'account
            usersId.remove(user.getUserId());
        }
        return userBillFree;
    }

    public List<Bill> getBills() {
        return bills;
    }

    public void setBills(List<Bill> bills) {
        this.bills = bills;
    }
}