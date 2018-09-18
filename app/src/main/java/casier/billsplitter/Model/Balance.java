package casier.billsplitter.Model;

public class Balance {
    private String payerId;
    private String paidId;
    private Float amount;

    public Balance(String payerId, String paidId, Float amount){
        this.payerId = payerId;
        this.paidId = paidId;
        this.amount = amount;
    }

    public String getPayerId() {
        return payerId;
    }

    public void setPayerId(String payerId) {
        this.payerId = payerId;
    }

    public String getPaidId() {
        return paidId;
    }

    public void setPaidId(String paidId) {
        this.paidId = paidId;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }
}
