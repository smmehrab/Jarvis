package com.example.jarvis;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class ReminderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ReminderActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
