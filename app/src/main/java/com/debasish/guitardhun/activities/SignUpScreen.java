package com.debasish.guitardhun.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.debasish.guitardhun.R;
import com.debasish.guitardhun.models.UserModel;
import com.debasish.guitardhun.utils.LoaderUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignUpScreen extends AppCompatActivity {

    @BindView(R.id.input_name) EditText etName;
    @BindView(R.id.input_email) EditText etEmail;
    @BindView(R.id.input_password) EditText etPassword;
    @BindView(R.id.btn_signup) android.support.v7.widget.AppCompatButton btnSignUp;
    @BindView(R.id.link_login) TextView tvLoginlink;
    private FirebaseAuth firebaseAuth;


    @OnClick (R.id.btn_signup) void signup(){
        signUp();
    }

    @OnClick (R.id.link_login) void RedirectUser(){
        SignUpScreen.this.finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sign_up_screen);
        ButterKnife.bind(this);
        firebaseAuth = FirebaseAuth.getInstance();

        etName.setText("Debasish");
        etEmail.setText("debasish@gmail.com");
        etPassword.setText("1234567890");
    }

    // Function responsible for making the user register
    public void signUp() {
        if (!validate()) {
            onSignupFailed();
            return;
        }

        btnSignUp.setEnabled(false);

        LoaderUtils.showProgressBar(SignUpScreen.this, "Please Wait");

        String name = etName.getText().toString();
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        registerUser(email, password, name);
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    // Function responsible for handling the sign up success
    public void onSignUpSuccess() {
        LoaderUtils.dismissProgress();
        btnSignUp.setEnabled(false);
        finish();
    }

    // Function responsible for handling the sign up failure
    public void onSignupFailed() {
        LoaderUtils.dismissProgress();
        Toast.makeText(getBaseContext(), "Registration failed", Toast.LENGTH_LONG).show();
        btnSignUp.setEnabled(true);
    }


    public boolean validate() {
        boolean valid = true;
        String name = etName.getText().toString();
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        if (name.isEmpty() ||
                name.length() < 3) {
            etName.setError("Name should be least 3 characters");
            valid = false;
        } else {
            etName.setError(null);
        }

        if (email.isEmpty() ||
                !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter a valid email address");
            valid = false;
        } else {
            etEmail.setError(null);
        }

        if (password.isEmpty() ||
                password.length() < 6 || password.length() > 10) {
            etPassword.setError("Password should not be less than 6 characters");
            valid = false;
        } else {
            etPassword.setError(null);
        }
        return valid;
    }

    /**
     * Function responsible for adding the user to the cloud server
     * @param email user given email address
     * @param password user given password
     */
    public void registerUser(final String email,
                             final String password,
                             final String userFullName){
        /*firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            String uId = null;

                            if(user != null){
                                uId = user.getUid();
                                UserModel userModel = new UserModel();
                                userModel.setUserId(uId);
                                userModel.setUserFullName(userFullName);
                                userModel.setEmail(email);
                                // Storing User Details
                                storeUserInfo(uId, userModel);
                            }
                            onSignUpSuccess();
                           // updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            onSignupFailed();
                            //updateUI(null);
                        }
                    }
                });*/


        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            onSignUpSuccess();
                        } else {
                            // If sign in fails, display a message to the user.
                            onSignupFailed();
                        }
                        // [START_EXCLUDE]
                        LoaderUtils.dismissProgress();
                        // [END_EXCLUDE]
                    }
                });
    }

    /**
     * Function responsible for storing user details
     * @param userId user Id
     * @param userModel User Model
     */
    public void storeUserInfo(String userId, UserModel userModel){
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users");
        // pushing user to 'users' node using the userId
        mDatabase.child(userId).setValue(userModel);
    }
}
