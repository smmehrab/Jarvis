package com.example.jarvis;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class JournalActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(JournalActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}

