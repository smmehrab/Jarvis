package com.example.jarvis;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private Button signUpBtn;
    private Button signUpWithGoogleBtn;

    private EditText emailEditTxt;
    private EditText passEditTxt;
    private EditText confirmPassEditTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        loadXmlElements();
        setListeners();
    }

    void loadXmlElements(){
        signUpBtn = (Button) findViewById(R.id.sign_up_btn);
        signUpWithGoogleBtn = (Button) findViewById(R.id.sign_up_with_google_btn);

        emailEditTxt = (EditText) findViewById(R.id.sign_up_email_edit_txt);
        passEditTxt = (EditText) findViewById(R.id.sign_up_pass_edit_txt);
        confirmPassEditTxt = (EditText) findViewById(R.id.sign_up_confirm_pass_edit_txt);
    }

    void setListeners(){
        signUpBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view == signUpBtn){
            Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
        else if(view == signUpWithGoogleBtn){
            // Here you go
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SignUpActivity.this, WelcomeActivity.class);
        startActivity(intent);
        finish();
    }
}
