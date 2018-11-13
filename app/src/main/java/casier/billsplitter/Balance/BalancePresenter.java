package casier.billsplitter.Balance;

import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import casier.billsplitter.Model.Balance;
import casier.billsplitter.Model.Bill;
import casier.billsplitter.Model.User;
import casier.billsplitter.Utils;

public class BalancePresenter {

    private FirebaseDatabase mDatabase;
    private BalanceActivity balanceActivity;
    private Utils mUtils;

    public BalancePresenter(final BalanceActivity balanceActivity) {
        this.balanceActivity = balanceActivity;
        this.mUtils = Utils.getInstance();
    }

    public void deleteBill(int position){
        mUtils.deleteBill(mUtils.getBillList().get(position));
    }

    public List<Balance> doTheBalance(){
        List<Balance> balanceList = new ArrayList<>();
        if(mUtils.getUserList() == null || mUtils.getUserList().size() == 0)
            return null;

        for(User u : mUtils.getUserList())
            u.clearBalance();

        for(Bill b : mUtils.getBillList()){
            if(b.getUsersId() != null)
            {
                for(String s : b.getUsersId()){
                    User u = mUtils.getUserById(s);
                    if(!u.getUserId().equals(b.getOwnerId())){
                        Float amountBeforeSplit = Float.valueOf(b.getAmount());
                        Float finalAmount = amountBeforeSplit / b.getUsersId().size();

                        Float finalRoundedAmount =  Math.round(finalAmount*100.0)/100.0f;
                        for(String userToAddBill : b.getUsersId()){
                            if(!userToAddBill.equals(u.getUserId())){
                                u.addToBalance(mUtils.getUserById(userToAddBill), finalRoundedAmount);
                            }
                        }
                    }
                }
            }
        }

        for(User payer : mUtils.getUserList()){
            for(User paid : mUtils.getUserList()){
                if(payer.getUsersBalance() !=  null){
                    if(payer.getUsersBalance().containsKey(paid) && paid.getUsersBalance() != null){
                        Float payerAmount = payer.getUsersBalance().get(paid);
                        Float paidAmount  = paid.getUsersBalance().get(payer);

                        if(payerAmount != null && paidAmount != null){
                            if(payerAmount > paidAmount){
                                payer.getUsersBalance().put(paid, payerAmount - paidAmount);
                                paid.getUsersBalance().remove(payer);
                            } else if (payerAmount.equals(paidAmount)){
                                payer.getUsersBalance().remove(paid);
                                paid.getUsersBalance().remove(payer);
                            } else {
                                paid.getUsersBalance().put(payer, paidAmount - payerAmount);
                                payer.getUsersBalance().remove(paid);
                            }
                        }
                    }
                }
            }
        }

        for(User u : mUtils.getUserList()){
            if(u.getUsersBalance() != null){
                for(Map.Entry<User, Float> entry : u.getUsersBalance().entrySet()){
                    balanceList.add(new Balance(u.getUserId(), entry.getKey().getUserId(), entry.getValue()));
                }
            }
        }

        return balanceList;
    }
}
