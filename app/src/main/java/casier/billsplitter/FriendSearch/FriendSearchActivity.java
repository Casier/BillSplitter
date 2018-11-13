package casier.billsplitter.FriendSearch;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import casier.billsplitter.Model.User;
import casier.billsplitter.R;

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
    public void onUserClick(int position) {
        User clickedUser = presenter.getClickedUser(position);
        presenter.addFriend(clickedUser.getUserId());
        // TODO dialog de confirmation
    }

    @OnTextChanged(R.id.search_friend_input)
    public void onSearchFriend(Editable editable){
        adapter.setUserList(presenter.getFilteredUserList(editable.toString()));
        adapter.notifyDataSetChanged();
    }

    @OnClick(R.id.search_friend_invite_button)
    public void onInviteClick(){
        presenter.sendInviteToFriend("","", "");

        // TODO dialog d'input pour les data ou fragment maybe
    }
}
