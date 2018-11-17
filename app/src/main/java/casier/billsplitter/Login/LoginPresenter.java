package casier.billsplitter.Login;

import android.content.Intent;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import casier.billsplitter.DAO;
import casier.billsplitter.Model.LocalUser;
import casier.billsplitter.Utils;

public class LoginPresenter {

    private LoginActivity loginActivity;
    private static final int RC_SIGN_IN = 2018;
    private Utils mUtils;
    private DAO data;

    private GoogleSignInClient mGoogleSignInClient;

    public LoginPresenter(LoginActivity loginActivity){
        this.loginActivity = loginActivity;
        this.mUtils = Utils.getInstance();
        this.data = DAO.getInstance();


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

    public void onActivityResult(int requestCode, int resultCode, Intent intentData) {
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(intentData);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                updateUserInfo(account);
                loginActivity.loginSuccess();
                data.addUser(account);
            } catch (ApiException e) {
                loginActivity.loginError();
            }
        }
    }

    public void updateUserInfo(GoogleSignInAccount account) {
        LocalUser.getInstance().setUserEmail(account.getEmail());
        LocalUser.getInstance().setUserId(account.getId());
        LocalUser.getInstance().setUserName(account.getDisplayName());
        LocalUser.getInstance().setUserPhotoUrl(account.getPhotoUrl());
        data.getUserFriends(account.getId());
    }
}