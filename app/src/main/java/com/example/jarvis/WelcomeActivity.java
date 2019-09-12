package com.example.jarvis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Button signInActivityBtn;
    private Button signUpActivityBtn;
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        mAuth = FirebaseAuth.getInstance();
        loadXmlComponents();
        setListeners();
        checkIfAlreadySignIn();
    }

    @Override
    protected void onStart() {

        mAuth.addAuthStateListener(mAuthListener);
        super.onStart();
    }

    private void checkIfAlreadySignIn() {
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() != null){
                    startActivity(new Intent(WelcomeActivity.this, HomeActivity.class));
                }
            }
        };
    }

    void loadXmlComponents(){
        signInActivityBtn = (Button) findViewById(R.id.sign_in_activity_btn);
        signUpActivityBtn = (Button) findViewById(R.id.sign_up_activity_btn);
    }

    void setListeners(){
        signInActivityBtn.setOnClickListener(this);
        signUpActivityBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view == signInActivityBtn){
            Intent intent = new Intent(WelcomeActivity.this, SignInActivity.class);
            startActivity(intent);
            finish();
        }
        else if(view == signUpActivityBtn){
            Intent intent = new Intent(WelcomeActivity.this, SignUpActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;

        showToast("Press Once Again to EXIT");

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}
