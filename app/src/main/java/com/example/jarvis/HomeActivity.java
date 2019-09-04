package com.example.jarvis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener{

    private boolean doubleBackToExitPressedOnce = false;
    private Button leftNavOn;
    private Button rightNavOn;

    private DrawerLayout leftDrawer;
    private boolean leftDrawerState=false;

    private DrawerLayout rightDrawer;
    private boolean rightDrawerState=false;

    private Toolbar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        loadXmlElements();
        setListeners();
    }


    void loadXmlElements(){
        leftNavOn = (Button) findViewById(R.id.left_nav_on_btn);
        rightNavOn = (Button) findViewById(R.id.right_nav_on_btn);

        actionBar = (Toolbar) findViewById(R.id.action_bar);
        setSupportActionBar(actionBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        leftDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    }

    void setListeners(){
        leftNavOn.setOnClickListener(this);
        rightNavOn.setOnClickListener(this);
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

    @Override
    public void onClick(View view) {
        if(view == leftNavOn){
            new CountDownTimer(100, 20){
                int i;
                @Override
                public void onTick(long l) {
                    if(i%2==0) {
                        leftNavOn.setVisibility(View.INVISIBLE);
                    }
                    else{
                        leftNavOn.setVisibility(View.VISIBLE);
                    }
                    i++;
                }

                @Override
                public void onFinish() {
                    leftNavOn.setVisibility(View.VISIBLE);
                    leftDrawer.openDrawer(GravityCompat.START);
                    if(!leftDrawerState)
                        leftDrawerState = true;
                    else leftDrawerState = false;
                }
            }.start();
        }

        /*else if(view == rightNavOn){
            new CountDownTimer(100, 20){
                int i;
                @Override
                public void onTick(long l) {
                    if(i%2==0) {
                        rightNavOn.setVisibility(View.INVISIBLE);
                    }
                    else{
                        rightNavOn.setVisibility(View.VISIBLE);
                    }
                    i++;
                }

                @Override
                public void onFinish() {
                    rightNavOn.setVisibility(View.VISIBLE);
                    rightDrawer.openDrawer(GravityCompat.END);
                    if(!rightDrawerState)
                        rightDrawerState = true;
                    else rightDrawerState = false;
                }
            }.start();
        }*/
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        if (id == R.id.home_option) {
            // Handle the camera action
        } else if (id == R.id.voice_command_option) {

        } else if (id == R.id.home_option) {

        } else if (id == R.id.todo_option) {

        } else if (id == R.id.journal_option) {

        } else if (id == R.id.wallet_option) {

        } else if (id == R.id.settings_option) {

        } else if (id == R.id.sign_out_option) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
