package com.heshamapps.heshe.thedentalstore.Login;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.heshamapps.heshe.thedentalstore.MainActivity;
import com.heshamapps.heshe.thedentalstore.Model.Users;
import com.heshamapps.heshe.thedentalstore.R;
import com.heshamapps.heshe.thedentalstore.fragment.MainFragment;
import com.heshamapps.heshe.thedentalstore.usersession.UserSession;
import com.heshamapps.heshe.thedentalstore.util.CONFIG;

import androidx.annotation.NonNull;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

public class LoginActivity extends Activity {




    @BindView(R.id.usernameET)
    EditText usernameET ;

    @BindView(R.id.passwordET)
    EditText passwordET;

    @BindView(R.id.progressBar)
     ProgressBar progressBar;

    String mUserName , mPassword ;


    private FirebaseAuth auth;
    FirebaseFirestore db;
    private UserSession session;
    CallbackManager mCallbackManager = CallbackManager.Factory.create();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        session = new UserSession(getApplicationContext());

        if(auth.getCurrentUser()!=null){
            auth.signOut();
            session.logoutUser();
        }
        LoginButton loginButton = findViewById(R.id.buttonFacebookLogin);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("TAG", "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult);
            }

            @Override
            public void onCancel() {
                Log.d("TAG", "facebook:onCancel");
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("TAG", "facebook:onError", error);
                // ...
            }
        });

    }


    private void handleFacebookAccessToken(LoginResult loginResult) {
   //     Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(loginResult.getAccessToken().getToken());
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //      Log.d(TAG, "signInWithCredential:success");
                            AuthResult user = task.getResult();
                            if (user != null ) {
                                getFireBaseUser(user.getUser());
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            //     Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                       //     updateUI(null);
                        }

                        // ...
                    }
                });
    }

        //Login BTN
    public void login(View view){
        mUserName = usernameET.getText().toString();
        mPassword = passwordET.getText().toString();

        if (TextUtils.isEmpty(mUserName)) {
            Toasty.warning(LoginActivity.this, "error" + "Enter email address!", Toast.LENGTH_SHORT).show();

            return;
        }

        if (TextUtils.isEmpty(mPassword)) {
            Toasty.warning(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        if(mUserName.equals("admin@gmail.com")&&mPassword.equals("adminadmin")){
            getSharedPreferences("myPref", MODE_PRIVATE).edit().putInt("userType",1).apply();
            session.createLoginSession();
            lunchAdminScreen();
            progressBar.setVisibility(View.INVISIBLE);

        }
        else{


        //authenticate user
        auth.signInWithEmailAndPassword(mUserName, mPassword)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            // there was an error
                            if (mPassword.length() < 6) {
                                passwordET.setError(getString(R.string.minimum_password));
                                progressBar.setVisibility(View.INVISIBLE);
                            } else {
                                Toasty.error(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.INVISIBLE);

                            }
                        } else {
                            AuthResult user = task.getResult();
                            if (user != null ) {
                                getFireBaseUser(user.getUser());
                            }

                        }
                    }
                });
        }
    }

   void getFireBaseUser(FirebaseUser FireUser){

        DocumentReference docRef = db.collection("users").document(FireUser.getUid());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Users user = documentSnapshot.toObject(Users.class);
                progressBar.setVisibility(View.GONE);
                session.createLoginSession(FireUser.getDisplayName(),FireUser.getEmail(),FireUser.getPhoneNumber(),FireUser.getUid());


                getSharedPreferences("myPref", MODE_PRIVATE).edit().putInt("userType",2).apply();
                lunchDoctorScreen();


            }
        });
    }
    //reset BTN
    public void reset(View view){
        startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
    }

    //signUp BTN
    public void signUp(View view){
        startActivity(new Intent(LoginActivity.this, SignupActivity.class));
    }

    public void lunchAdminScreen(){
    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
    startActivity(intent);
    finish();
    }

    public void lunchDoctorScreen(){
        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {

        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

}
