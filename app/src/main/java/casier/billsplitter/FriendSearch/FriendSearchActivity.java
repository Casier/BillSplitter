package casier.billsplitter.FriendSearch;

import android.app.Activity;
import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import casier.billsplitter.Model.User;
import casier.billsplitter.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class FriendSearchActivity extends Activity implements FriendListAdapter.OnUserClicked {

    @BindView(R.id.search_friend_recycler)
    RecyclerView friendRecycler;

    private FriendSearchPresenter presenter;
    private FriendListAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friend);
        ButterKnife.bind(this);

        presenter = new FriendSearchPresenter(this);

        adapter = new FriendListAdapter(this, R.layout.row_friend_list, presenter.getUserList());
        adapter.setOnclick(FriendSearchActivity.this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        friendRecycler.setLayoutManager(layoutManager);
        friendRecycler.setHasFixedSize(true);
        friendRecycler.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.clear();
    }

    @Override
    public void onUserClick(int position) {
        // TODO dialog de confirmation

        final Dialog dialog = new Dialog(this, android.R.style.Theme_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirm_add_friend);
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        TextView userName = dialog.findViewById(R.id.add_friend_text);
        CircleImageView userImage = dialog.findViewById(R.id.add_friend_image);
        Button addFriend = dialog.findViewById(R.id.add_friend_btn);

        userName.setText("Ajouter " + adapter.getUserList().get(position).getUserName() + " aux amis ?");


        addFriend.setOnClickListener(view -> {
            presenter.addFriend(adapter.getUserList().get(position).getUserId());
            dialog.dismiss();
        });

        Glide.with(this)
                .load(Uri.parse(adapter.getUserList().get(position).getUserPhotoUrl()))
                .into(userImage);
        dialog.show();

    }

    @OnTextChanged(R.id.search_friend_input)
    public void onSearchFriend(Editable editable){
        adapter.setUserList(presenter.getFilteredUserList(editable.toString()));
    }

    @OnClick(R.id.search_friend_invite_button)
    public void onInviteClick(){
        presenter.sendInviteToFriend("","", "");

        // TODO dialog d'input pour les data ou fragment maybe
    }

    public void updateAdapter(List<User> friendList){
        adapter.setUserList(friendList);
    }
}
