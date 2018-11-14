package casier.billsplitter.AddBill;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import casier.billsplitter.Model.User;
import casier.billsplitter.R;
import casier.billsplitter.Utils;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserPickerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private int itemResource;
    private List<User> userList;
    private List<User> selectedUserList = new ArrayList<>();
    private Utils mUtils;

    public UserPickerAdapter(Context context, int resource, List<User> userList){
        this.context = context;
        this.userList = new ArrayList<>(userList);
        this.itemResource = resource;
        this.mUtils = Utils.getInstance();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder viewHolder = new UserPickerHolder(inflater.inflate(itemResource, parent, false));

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        UserPickerHolder userPickerHolder = (UserPickerHolder) holder;
        userPickerHolder.bindUser(userList.get(position));
    }

    public boolean isSelected(int position){
        User u = userList.get(position);
        if(selectedUserList.contains(u)){
            selectedUserList.remove(u);
            return false;
        } else {
            selectedUserList.add(u);
            return true;
        }
    }

    public void setUserList(List<User> userList){
        this.userList = new ArrayList<>(userList);
    }

    @Override
    public int getItemCount() {
        return this.userList.size();
    }

    public List<User> getSelectedUserList(){
        return selectedUserList;
    }

    public void setSelectedUserList(List<User> selectedUserList){
        this.selectedUserList = new ArrayList<>(selectedUserList);
    }

    public class UserPickerHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private final TextView userName;
        private final CheckBox checkBox;
        private final CircleImageView userImage;


        public UserPickerHolder(View itemView){
            super(itemView);
            this.userName = itemView.findViewById(R.id.user_name);
            this.checkBox = itemView.findViewById(R.id.user_selected);
            this.userImage = itemView.findViewById(R.id.user_picture);
            itemView.setOnClickListener(this);
            checkBox.setOnClickListener(this);
        }

        public void bindUser(User user){
            userName.setText(user.getUserName());
            checkBox.setChecked(selectedUserList.contains(user));
            Glide.with(context)
                    .load(Uri.parse(user.getUserPhotoUrl()))
                    .into(userImage);
        }

        @Override
        public void onClick(View view) {
            view.clearFocus();
            int adapterPosition = getAdapterPosition();
            checkBox.setChecked(isSelected(adapterPosition));
        }
    }
}
