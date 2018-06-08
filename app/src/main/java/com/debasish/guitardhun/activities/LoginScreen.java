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

public class LoginScreen extends AppCompatActivity {

    @BindView(R.id.link_signup) TextView tvCreateAccount;
    @BindView(R.id.input_email) EditText etEmail;
    @BindView(R.id.input_password)EditText etPassword;
    @BindView(R.id.btn_login)android.support.v7.widget.AppCompatButton btnSignIn;
    private FirebaseAuth firebaseAuth;


    // Applying the click event on the sign up link
    @OnClick(R.id.link_signup) void submit() {
       startActivity(new Intent(LoginScreen.this, SignUpScreen.class));
    }

    // Applying the click event on the sign in button
    @OnClick(R.id.btn_login) void letUserIn(){
        login();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login_screen);
        ButterKnife.bind(this);
        getSupportActionBar().hide();
        firebaseAuth = FirebaseAuth.getInstance();

        etEmail.setText("debasish@gmail.com");
        etPassword.setText("1234567890");
    }


    // Function Responsible for letting the user login
    public void login() {
        if (!validate()) {
            onLoginFailed();
            return;
        }
        btnSignIn.setEnabled(false);


        LoaderUtils.showProgressBar(LoginScreen.this, "Please Wait..");
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        //signIn(email, password);

        UserModel userModel = new UserModel();
        userModel.setUserId("123");
        userModel.setUserFullName("userFullName");
        userModel.setEmail("email");
        storeUserInfo("123", userModel);
    }

    public void signIn(final String email, final String password){
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            onLoginSuccess();
                        } else {
                            // If sign in fails, display a message to the user.
                            onLoginFailed();
                        }
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


    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        LoaderUtils.dismissProgress();
        btnSignIn.setEnabled(false);
        Toast.makeText(getBaseContext(), "Successfully logged in", Toast.LENGTH_LONG).show();
        //finish();
    }

    public void onLoginFailed() {
        LoaderUtils.dismissProgress();
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        btnSignIn.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        if (email.isEmpty() ||
                !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter a valid email address");
            valid = false;
        } else {
            etEmail.setError(null);
        }

        if (password.isEmpty() ||
                password.length() < 4 ||
                password.length() > 10) {
            etPassword.setError("Password should not be less than 4 characters");
            valid = false;
        } else {
            etPassword.setError(null);
        }

        return valid;
    }
}
