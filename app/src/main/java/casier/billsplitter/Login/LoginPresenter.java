package casier.billsplitter.Login;

import android.content.Intent;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import casier.billsplitter.Model.UserInfo;

public class LoginPresenter {

    private LoginActivity loginActivity;
    private static final int RC_SIGN_IN = 2018;
    private LoginInteractor loginInteractor;

    private GoogleSignInClient mGoogleSignInClient;

    public LoginPresenter(LoginActivity loginActivity, LoginInteractor loginInteractor){
        this.loginActivity = loginActivity;
        this.loginInteractor = loginInteractor;

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(loginActivity, gso);
    }

    public void getLastSignedAccount(){
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(loginActivity);
        if(account != null) {
            updateUserInfo(account);
            loginActivity.loginSuccess();
        }
    }

    public void onDestroy(){
        loginActivity = null;
    }

    public void signIn(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        loginActivity.startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                updateUserInfo(account);
                loginActivity.loginSuccess();
            } catch (ApiException e) {
                loginActivity.loginError();
            }
        }
    }

    public void updateUserInfo(GoogleSignInAccount account) {
        UserInfo.getInstance().setUserEmail(account.getEmail());
        UserInfo.getInstance().setUserId(account.getId());
        UserInfo.getInstance().setUserName(account.getDisplayName());
        UserInfo.getInstance().setUserPhotoUrl(account.getPhotoUrl());
    }
}