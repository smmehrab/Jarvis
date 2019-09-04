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

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;

    private NavigationView leftNavigationView;
    private NavigationView rightNavigationView;

    private Toolbar actionBar;

    private Button leftDrawerBtn;
    private Button rightDrawerBtn;

    private Button todoActivityBtn;
    private Button journalActivityBtn;
    private Button walletActivityBtn;
    private Button reminderActivityBtn;

    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        settingUpXmlElements();
    }


    void settingUpXmlElements(){
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.setDrawerListener(drawerToggle);

        leftNavigationView = (NavigationView) findViewById(R.id.left_navigation_view);
        rightNavigationView = (NavigationView) findViewById(R.id.right_navigation_view);

        actionBar = (Toolbar) findViewById(R.id.action_bar);
        setSupportActionBar(actionBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        leftDrawerBtn = (Button) findViewById(R.id.left_nav_on_btn);
        rightDrawerBtn = (Button) findViewById(R.id.right_nav_on_btn);

        todoActivityBtn = (Button) findViewById(R.id.todo_activity_btn);
        journalActivityBtn = (Button) findViewById(R.id.journal_activity_btn);
        walletActivityBtn = (Button) findViewById(R.id.wallet_activity_btn);
        reminderActivityBtn = (Button) findViewById(R.id.reminder_activity_btn);

        leftDrawerBtn.setOnClickListener(this);
        rightDrawerBtn.setOnClickListener(this);

        todoActivityBtn.setOnClickListener(this);
        journalActivityBtn.setOnClickListener(this);
        walletActivityBtn.setOnClickListener(this);
        reminderActivityBtn.setOnClickListener(this);
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
        if(view == leftDrawerBtn){
            new CountDownTimer(100, 20){
                int i;
                @Override
                public void onTick(long l) {
                    if(i%2==0) {
                        leftDrawerBtn.setVisibility(View.INVISIBLE);
                    }
                    else{
                        leftDrawerBtn.setVisibility(View.VISIBLE);
                    }
                    i++;
                }

                @Override
                public void onFinish() {
                    leftDrawerBtn.setVisibility(View.VISIBLE);
                    if(drawerLayout.isDrawerOpen(leftNavigationView)){
                        drawerLayout.closeDrawer(leftNavigationView);
                    }

                    else if(!drawerLayout.isDrawerOpen(leftNavigationView)){
                        drawerLayout.openDrawer(leftNavigationView);
                    }

                    else if(drawerLayout.isDrawerOpen(rightNavigationView)){
                        drawerLayout.closeDrawer(rightNavigationView);
                    }
                }
            }.start();
        }

        else if(view == rightDrawerBtn){
            new CountDownTimer(100, 20){
                int i;
                @Override
                public void onTick(long l) {
                    if(i%2==0) {
                        rightDrawerBtn.setVisibility(View.INVISIBLE);
                    }
                    else{
                        rightDrawerBtn.setVisibility(View.VISIBLE);
                    }
                    i++;
                }

                @Override
                public void onFinish() {
                    rightDrawerBtn.setVisibility(View.VISIBLE);

                    if(drawerLayout.isDrawerOpen(rightNavigationView)){
                        drawerLayout.closeDrawer(rightNavigationView);
                    }

                    else if(!drawerLayout.isDrawerOpen(rightNavigationView)){
                        drawerLayout.openDrawer(rightNavigationView);
                    }

                    else if(drawerLayout.isDrawerOpen(leftNavigationView)){
                        drawerLayout.closeDrawer(leftNavigationView);
                    }
                }
            }.start();
        }

        else if(view == todoActivityBtn){
            Intent intent = new Intent(HomeActivity.this, TodoActivity.class);
            startActivity(intent);
            finish();
        }

        else if(view == journalActivityBtn){
            Intent intent = new Intent(HomeActivity.this, JournalActivity.class);
            startActivity(intent);
            finish();
        }

        else if(view == walletActivityBtn){
            Intent intent = new Intent(HomeActivity.this, WalletActivity.class);
            startActivity(intent);
            finish();
        }

        else if(view == reminderActivityBtn){
            Intent intent = new Intent(HomeActivity.this, ReminderActivity.class);
            startActivity(intent);
            finish();
        }
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
