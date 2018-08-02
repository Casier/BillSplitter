package casier.billsplitter.Model;

import java.util.Date;

public class Bill {

    private String id;
    private Date date;
    private String title;
    private String ownerId;
    private String amount; // Saved as String because Firebase don't support float

    public Bill(){

    }

    public Bill(String id, Date date, String title, String ownerId, String amount){
        this.id = id;
        this.date = date;
        this.title = title;
        this.ownerId = ownerId;
        this.amount = amount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
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
}
