package com.example.jarvis;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

public class ResetPassActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private Button userDrawerBtn;
    private Button activityDrawerBtn;

    private TextView activityTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pass);

        settingUpXmlElements();
    }

    void settingUpXmlElements(){

        // Setting Up Toolbar
        toolbar = (Toolbar) findViewById(R.id.reset_pass_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Setting Up Components Inside Toolbar
        userDrawerBtn = (Button) findViewById(R.id.user_drawer_btn);
        activityDrawerBtn = (Button) findViewById(R.id.activity_drawer_btn);

        activityDrawerBtn.setBackgroundResource(R.drawable.icon_key);

        activityTitle = (TextView) findViewById(R.id.activity_title);
        activityTitle.setText(R.string.reset_password_txt);
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ResetPassActivity.this, SignInActivity.class);
        startActivity(intent);
        finish();
    }
}
