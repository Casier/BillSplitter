package casier.billsplitter.Model;

public class Bill {

    private String date; // Savec as String because Firebase don't support Java Data format
    private String amount; // Saved as String because Firebase don't support float
    private String ownerId;
    private String title;

    public Bill(){

    }

    public Bill(String date, String amount, String ownerId, String title){
        this.date = date;
        this.amount = amount;
        this.ownerId = ownerId;
        this.title = title;
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
}
