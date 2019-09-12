package com.example.jarvis;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class ForgotPassActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ForgotPassActivity.this, SignInActivity.class);
        startActivity(intent);
        finish();
    }
}
