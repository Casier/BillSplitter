package casier.billsplitter.Model;

import java.util.List;

public class Bill {

    private String date; // Savec as String because Firebase don't support Java Data format
    private String amount; // Saved as String because Firebase don't support float
    private String ownerId;
    private String title;
    private List<String> usersId; // Contains the id of users who are concerned by this bill

    public Bill(){

    }

    public Bill(String date, String amount, String ownerId, String title, List<String> usersId){
        this.date = date;
        this.amount = amount;
        this.ownerId = ownerId;
        this.title = title;
        this.usersId = usersId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public List<String> getUsersId() { return this.usersId; }

    public void setUsersId(List<String> usersId) { this.usersId = usersId; }

    @Override
    public boolean equals(Object obj) {
        if(this.ownerId.equals(((Bill)obj).getOwnerId()) && this.date.equals(((Bill)obj).getDate()))
            return true;

        return false;
    }
}
