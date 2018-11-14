package casier.billsplitter.FriendSearch;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import casier.billsplitter.Model.User;
import casier.billsplitter.R;
import casier.billsplitter.Utils;
import de.hdodenhof.circleimageview.CircleImageView;

public class FriendListAdapter extends RecyclerView.Adapter {

    private List<User> userListWithoutFriends;
    private int rowLayout;
    private Context context;
    private OnUserClicked onClick;
    private Utils mUtils;


    public interface OnUserClicked {
        void onUserClick(int position);
    }

    public FriendListAdapter(Context context, int resource, List<User> userList){
        this.context = context;
        this.rowLayout = resource;
        this.userListWithoutFriends = userList;
        this.mUtils = Utils.getInstance();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder viewHolder = new FriendListHolder(inflater.inflate(rowLayout, parent, false));

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        FriendListHolder friendListHolder = (FriendListHolder) holder;
        ((FriendListHolder) holder).layout.setOnClickListener(view -> onClick.onUserClick(position));

        friendListHolder.bindUser(userListWithoutFriends.get(position));
    }

    @Override
    public int getItemCount() {
        return userListWithoutFriends.size();
    }

    public void setOnclick(OnUserClicked onClick){
        this.onClick = onClick;
    }

    public void setUserList(List<User> userList){
        this.userListWithoutFriends = new ArrayList<>();
        this.userListWithoutFriends.addAll(userList);

        this.notifyDataSetChanged();
    }

    public List<User> getUserList(){ return this.userListWithoutFriends; }

    public class FriendListHolder extends RecyclerView.ViewHolder{

        private RelativeLayout layout;
        private TextView userName;
        private CircleImageView userPhoto;

        public FriendListHolder(View itemView) {
            super(itemView);
            this.layout = itemView.findViewById(R.id.friend_list_layout);
            this.userName = itemView.findViewById(R.id.friend_list_user_name);
            this.userPhoto = itemView.findViewById(R.id.friend_list_user_photo);
        }

        public void bindUser(User user){
            userName.setText(user.getUserName());
            Glide.with(context)
                    .load(Uri.parse(mUtils.getImageUrlByUserId(user.getUserId())))
                    .into(userPhoto);
        }
    }
}
