package com.example.jarvis;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {

    private Button signInBtn;
    private Button signInWithGoogleBtn;
    private Button forgotPassBtn;

    private EditText emailEditTxt;
    private EditText passEditTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        loadXmlElements();
        setListeners();
    }

    void loadXmlElements(){
        signInBtn = (Button) findViewById(R.id.sign_in_btn);
        signInWithGoogleBtn = (Button) findViewById(R.id.sign_in_with_google_btn);
        forgotPassBtn = (Button) findViewById(R.id.forgot_pass_btn);

        emailEditTxt = (EditText) findViewById(R.id.sign_in_email_edit_txt);
        passEditTxt = (EditText) findViewById(R.id.sign_in_pass_edit_txt);
    }

    void setListeners(){
        signInBtn.setOnClickListener(this);
        signInWithGoogleBtn.setOnClickListener(this);
        forgotPassBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view == signInBtn){
            Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }

        else if(view == signInWithGoogleBtn){
            // Here you go
        }

        else if(view == forgotPassBtn){
            Intent intent = new Intent(SignInActivity.this, ForgotPassActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SignInActivity.this, WelcomeActivity.class);
        startActivity(intent);
        finish();
    }
}
