package casier.billsplitter.Login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.google.android.gms.common.SignInButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import casier.billsplitter.MainActivity;
import casier.billsplitter.R;

public class LoginActivity extends Activity {

    private LoginPresenter presenter;
    @BindView(R.id.sign_in_button) SignInButton signInButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        ButterKnife.bind(this);
        presenter = new LoginPresenter(this, new LoginInteractor());

    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.getLastSignedAccount();
    }

    @Override
    protected void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }

    @OnClick(R.id.sign_in_button)
    public void onSignInClick() {
        presenter.signIn();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        presenter.onActivityResult(requestCode, requestCode, data);
    }

    public void loginError(){
        Toast.makeText(getApplicationContext(), "Unable to connect you with Google", Toast.LENGTH_LONG).show();
    }

    public void loginSuccess(){
        Intent mainIntent = new Intent(this, MainActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.startActivity(mainIntent);
    }
}
