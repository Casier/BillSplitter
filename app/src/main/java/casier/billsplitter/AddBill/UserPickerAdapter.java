package casier.billsplitter.AddBill;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import casier.billsplitter.Model.LocalUser;
import casier.billsplitter.Model.User;
import casier.billsplitter.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserPickerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private int itemResource;
    private SparseBooleanArray itemStateArray = new SparseBooleanArray();
    private List<User> userList;

    public UserPickerAdapter(Context context, int resource, List<User> userList){
        this.context = context;
        this.userList = userList;
        this.itemResource = resource;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder viewHolder = new UserPickerHolder(inflater.inflate(R.layout.row_user_picker, parent, false));

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        UserPickerHolder userPickerHolder = (UserPickerHolder) holder;
        userPickerHolder.bindUser(userList.get(position));
    }

    @Override
    public int getItemCount() {
        return this.userList.size();
    }

    public SparseBooleanArray getItemStateArray() {
        return itemStateArray;
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
            if(user.getUserName().equals(LocalUser.getInstance().getUserName())){
                checkBox.setChecked(true);
                itemStateArray.put(getAdapterPosition(), true);
            } else {
                itemStateArray.put(getAdapterPosition(), false);
            }

            Glide.with(context)
                    .load(Uri.parse(user.getUserPhotoUrl()))
                    .into(userImage);
        }

        @Override
        public void onClick(View view) {
            view.clearFocus();
            int adapterPosition = getAdapterPosition();
            if (!itemStateArray.get(adapterPosition, false)) {
                checkBox.setChecked(true);
                itemStateArray.put(adapterPosition, true);
            }
            else  {
                checkBox.setChecked(false);
                itemStateArray.put(adapterPosition, false);
            }
        }
    }
}
