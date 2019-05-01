package com.heshamapps.heshe.thedentalstore.Login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.creativityapps.gmailbackgroundlibrary.BackgroundMail;
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
import com.heshamapps.heshe.thedentalstore.usersession.UserSession;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

import static com.heshamapps.heshe.thedentalstore.usersession.UserSession.KEY_EMAIL;

public class SignupActivity extends Activity {

    @BindView(R.id.email)
    EditText inputEmail ;

    @BindView(R.id.password)
    EditText inputPassword ;

    @BindView(R.id.cnfpass)
    EditText inputCnfpass ;


    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.name)
    EditText inputName;

    @BindView(R.id.phone)
    EditText inputPhone;

    @BindView(R.id.address)
    EditText inputAddress;


    private String check;

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


        inputName.addTextChangedListener(nameWatcher);
        inputEmail.addTextChangedListener(emailWatcher);
        inputPassword.addTextChangedListener(passWatcher);
        inputCnfpass.addTextChangedListener(cnfpassWatcher);
        inputPhone.addTextChangedListener(numberWatcher);
        inputAddress.addTextChangedListener(addressWatcher);
    }



    public void reset(View view){

        startActivity(new Intent(SignupActivity.this, ResetPasswordActivity.class));
    }


    public void signIn(View view){

        finish();
    }


    public void signUp(View view){



        if ( validateName() && validateEmail() && validatePass() && validateCnfPass() && validateNumber() && validateAddress()) {

            String email = inputEmail.getText().toString().trim();
            String password = inputPassword.getText().toString().trim();
            String name = inputName.getText().toString().trim();
            String phone = inputPhone.getText().toString().trim();
            String address = inputAddress.getText().toString().trim();

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
                            if (task.isSuccessful()) {

                                // save user to database firestore
                                db.collection("users").document(auth.getUid())
                                        .set(new Users(email, auth.getUid(), 2, name, phone, address))
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                 Toasty.success(SignupActivity.this, "Registration success", Toast.LENGTH_SHORT).show();

                                                sendRegistrationEmail(name,email);


                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toasty.error(SignupActivity.this, "Authentication failed." + e.getMessage(),
                                                        Toast.LENGTH_LONG).show();
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

        else{
            Toasty.error(SignupActivity.this, "please check your entries",
                    Toast.LENGTH_LONG).show();
        }
    }








    private boolean validateEmail() {

        check = inputEmail.getText().toString();

        if (check.length() < 4 || check.length() > 40) {
            return false;
        } else if (!check.matches("^[A-za-z0-9.@]+")) {
            return false;
        } else if (!check.contains("@") || !check.contains(".")) {
            return false;
        }

        return true;
    }



    private boolean validatePass() {


        check = inputPassword.getText().toString();

        if (check.length() < 4 || check.length() > 20) {
            return false;
        } else if (!check.matches("^[A-za-z0-9@]+")) {
            return false;
        }
        return true;
    }

    private boolean validateCnfPass() {

        check = inputCnfpass.getText().toString();

        return check.equals(inputPassword.getText().toString());
    }




    private boolean validateName() {

        check = inputName.getText().toString();

        return !(check.length() < 4 || check.length() > 20);

    }

    private boolean validateAddress() {

        check = inputAddress.getText().toString();

        return !(check.length() < 4 || check.length() > 20);

    }

    private boolean validateNumber() {

        check = inputPhone.getText().toString();
        Log.e("inside number",check.length()+" ");
        if (check.length()>10) {
            return false;
        }else if(check.length()<10){
            return false;
        }
        return true;
    }

    //TextWatcher for Name -----------------------------------------------------

    TextWatcher nameWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //none
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //none
        }

        @Override
        public void afterTextChanged(Editable s) {

            check = s.toString();

            if (check.length() < 4 || check.length() > 20) {
                inputName.setError("Name Must consist of 4 to 20 characters");
            }
        }

    };

    //TextWatcher for Name -----------------------------------------------------

    TextWatcher addressWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //none
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //none
        }

        @Override
        public void afterTextChanged(Editable s) {

            check = s.toString();

            if (check.length() < 4 || check.length() > 20) {
                inputAddress.setError("Address Must consist of 4 to 20 characters");
            }
        }

    };

    //TextWatcher for Email -----------------------------------------------------

    TextWatcher emailWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //none
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //none
        }

        @Override
        public void afterTextChanged(Editable s) {

            check = s.toString();

            if (check.length() < 4 || check.length() > 40) {
                inputEmail.setError("Email Must consist of 4 to 20 characters");
            } else if (!check.matches("^[A-za-z0-9.@]+")) {
                inputEmail.setError("Only . and @ characters allowed");
            } else if (!check.contains("@") || !check.contains(".")) {
                inputEmail.setError("Enter Valid Email");
            }

        }

    };

    //TextWatcher for pass -----------------------------------------------------

    TextWatcher passWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //none
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //none
        }

        @Override
        public void afterTextChanged(Editable s) {

            check = s.toString();

            if (check.length() < 4 || check.length() > 20) {
                inputPassword.setError("Password Must consist of 4 to 20 characters");
            } else if (!check.matches("^[A-za-z0-9@]+")) {
                inputEmail.setError("Only @ special character allowed");
            }
        }

    };


    //TextWatcher for repeat Password -----------------------------------------------------

    TextWatcher cnfpassWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //none
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //none
        }

        @Override
        public void afterTextChanged(Editable s) {

            check = s.toString();

            if (!check.equals(inputPassword.getText().toString())) {
                inputCnfpass.setError("Both the passwords do not match");
            }
        }

    };

//TextWatcher for Mobile -----------------------------------------------------

    TextWatcher numberWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //none
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //none
        }

        @Override
        public void afterTextChanged(Editable s) {

            check = s.toString();

            if (check.length()>10) {
                inputPhone.setError("Number cannot be grated than 10 digits");
            }else if(check.length()<10){
                inputPhone.setError("Number should be 10 digits");
            }
        }

    };



    private void sendRegistrationEmail(final String name, final String emails) {




        BackgroundMail.newBuilder(SignupActivity.this)
                .withSendingMessage("Sending Welcome Greetings to Your Email !")
                .withSendingMessageSuccess("Kindly Check Your Email now !")
                .withSendingMessageError("Failed to send password ! Try Again !")
                .withUsername("shreen.ods2019@gmail.com")
                .withPassword("$S15#07#1997m$")
                .withMailto(emails)
                .withType(BackgroundMail.TYPE_PLAIN)
                .withSubject("Greetings from ODS")
                .withBody("Hello Mr/Miss, "+ name + "\n " + "Welcome to ODS App! ")
                .withOnSuccessCallback(new BackgroundMail.OnSuccessCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d("BackgroundMail", "Success");
                        //do some magic
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                      startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                                          finish();
                            }
                        });
                    }
                })
                .withOnFailCallback(new BackgroundMail.OnFailCallback() {
                    @Override
                    public void onFail() {
                        Log.d("BackgroundMail", "Failed");
                        //do some magic
                        Toasty.error(SignupActivity.this, "Email not sent",
                                Toast.LENGTH_LONG).show();
                        startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                        finish();
                    }
                })
                .send();

    }


        @Override
        protected void onResume() {
            super.onResume();
            progressBar.setVisibility(View.GONE);
        }

    }

