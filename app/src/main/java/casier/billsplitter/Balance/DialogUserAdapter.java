package casier.billsplitter.Balance;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import java.util.List;

import casier.billsplitter.R;
import casier.billsplitter.Utils;
import de.hdodenhof.circleimageview.CircleImageView;

public class DialogUserAdapter extends RecyclerView.Adapter {

    private List<String> userIdList;
    private int itemResource;
    private Utils mUtils;
    private Context context;

    public DialogUserAdapter(Context context, int resource, List<String> userIdList){
            this.userIdList = userIdList;
            this.itemResource = resource;
            mUtils = Utils.getInstance();
            this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder viewHolder = new DialogUserHolder(inflater.inflate(itemResource, parent, false));

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        DialogUserHolder dialogUserHolder = (DialogUserHolder) holder;
        dialogUserHolder.bindUser(userIdList.get(position));
    }

    @Override
    public int getItemCount() {
        return userIdList.size();
    }

    public class DialogUserHolder extends RecyclerView.ViewHolder{

        private CircleImageView userImage;

        DialogUserHolder(View itemView) {
            super(itemView);
            this.userImage = itemView.findViewById(R.id.row_dialog_user_image);
        }

        void bindUser(String userId){
            Glide.with(context)
                    .load(Uri.parse(mUtils.getImageUrlByUserId(userId)))
                    .into(userImage);
        }
    }
}
