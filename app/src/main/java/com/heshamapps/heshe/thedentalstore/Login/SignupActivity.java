package com.heshamapps.heshe.thedentalstore.Login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.heshamapps.heshe.thedentalstore.MainActivity;
import com.heshamapps.heshe.thedentalstore.Model.Users;
import com.heshamapps.heshe.thedentalstore.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

public class SignupActivity extends Activity {

    @BindView(R.id.email)
    EditText inputEmail ;

    @BindView(R.id.password)
    EditText inputPassword ;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

   @BindView(R.id.userTypeRadioGroup)
    RadioGroup userTypeRadioGroup;

    @BindView(R.id.adminRB)
    RadioButton adminRB;

    @BindView(R.id.studentRB)
    RadioButton studentRB;

    int type ;


    private FirebaseAuth auth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);


        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


    }



    public void reset(View view){

        startActivity(new Intent(SignupActivity.this, ResetPasswordActivity.class));
    }


    public void signIn(View view){

        finish();
    }


    public void signUp(View view){


        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toasty.warning(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toasty.warning(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toasty.error(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        //create user
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if(task.isSuccessful()){
                            int selectedId  = userTypeRadioGroup.getCheckedRadioButtonId();
                            // find which radio button is selected
                            if(selectedId == R.id.adminRB) {
                                type = 1 ;
                                Toast.makeText(getApplicationContext(), "choice: admin",
                                        Toast.LENGTH_SHORT).show();
                            } else if
                                    (selectedId == R.id.studentRB) {
                                type = 2 ;
                                Toast.makeText(getApplicationContext(), "choice: student",
                                        Toast.LENGTH_SHORT).show();

                            }
                            // save user to database firestore
                            db.collection("users").document(auth.getUid())
                                    .set(new Users(email,auth.getUid(),type))
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(SignupActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                                            if(type==1){
                                                startActivity(new Intent(SignupActivity.this, MainActivity.class));
                                                finish();
                                            }
                                            else{
                                                startActivity(new Intent(SignupActivity.this, MainActivity.class));
                                                finish();
                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                        }
                                    });
                        }

                        if (!task.isSuccessful()) {
                            Toasty.error(SignupActivity.this, "Authentication failed." + task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                            Log.d("error", task.getException().toString());
                        }
                    }
                });

    }


        @Override
        protected void onResume() {
            super.onResume();
            progressBar.setVisibility(View.GONE);
        }

    }

