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
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener{

    //  Variables for Remote Database
    private static final int RC_SIGN_IN = 1;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth.AuthStateListener mAuthListener;
    GoogleSignInOptions googleSignInOptions;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;

    private NavigationView userNavigationView;
    private NavigationView activityNavigationView;

    private Toolbar toolbar;

    private Button userDrawerBtn;
    private Button activityDrawerBtn;

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
        mAuth = FirebaseAuth.getInstance();
        
        // Finding the Parent Layout
        drawerLayout = (DrawerLayout) findViewById(R.id.home_drawer_layout);
        drawerLayout.setDrawerListener(drawerToggle);

        // Setting Up Toolbar
        toolbar = (Toolbar) findViewById(R.id.home_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Setting Up Components Inside Toolbar
        userDrawerBtn = (Button) findViewById(R.id.user_drawer_btn);
        activityDrawerBtn = (Button) findViewById(R.id.activity_drawer_btn);

        userDrawerBtn.setOnClickListener(this);
        activityDrawerBtn.setOnClickListener(this);

        activityDrawerBtn.setBackgroundResource(R.drawable.icon_activity_home);

        // Two Navigation View for Two Navigation Drawers
        userNavigationView = (NavigationView) findViewById(R.id.user_navigation_view);
        activityNavigationView = (NavigationView) findViewById(R.id.home_navigation_view);

        userNavigationView.setNavigationItemSelectedListener(this);
        activityNavigationView.setNavigationItemSelectedListener(this);

        userNavigationView.getMenu().findItem(R.id.user_home_option).setCheckable(true);
        userNavigationView.getMenu().findItem(R.id.user_home_option).setChecked(true);

        // Setting Up Other Buttons of the Activity
        todoActivityBtn = (Button) findViewById(R.id.todo_activity_btn);
        journalActivityBtn = (Button) findViewById(R.id.journal_activity_btn);
        walletActivityBtn = (Button) findViewById(R.id.wallet_activity_btn);
        reminderActivityBtn = (Button) findViewById(R.id.reminder_activity_btn);

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
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onClick(View view) {
        if(view == userDrawerBtn){
            new CountDownTimer(100, 20){
                int i;
                @Override
                public void onTick(long l) {
                    if(i%2==0) {
                        userDrawerBtn.setVisibility(View.INVISIBLE);
                    }
                    else{
                        userDrawerBtn.setVisibility(View.VISIBLE);
                    }
                    i++;
                }

                @Override
                public void onFinish() {
                    userDrawerBtn.setVisibility(View.VISIBLE);
                    if(drawerLayout.isDrawerOpen(userNavigationView)){
                        drawerLayout.closeDrawer(userNavigationView);
                    }

                    else if(!drawerLayout.isDrawerOpen(userNavigationView)){
                        drawerLayout.openDrawer(userNavigationView);
                    }

                    else if(drawerLayout.isDrawerOpen(activityNavigationView)){
                        drawerLayout.closeDrawer(activityNavigationView);
                    }
                }
            }.start();
        }

        else if(view == activityDrawerBtn){
            new CountDownTimer(100, 20){
                int i;
                @Override
                public void onTick(long l) {
                    if(i%2==0) {
                        activityDrawerBtn.setVisibility(View.INVISIBLE);
                    }
                    else{
                        activityDrawerBtn.setVisibility(View.VISIBLE);
                    }
                    i++;
                }

                @Override
                public void onFinish() {
                    activityDrawerBtn.setVisibility(View.VISIBLE);

                    if(drawerLayout.isDrawerOpen(activityNavigationView)){
                        drawerLayout.closeDrawer(activityNavigationView);
                    }

                    else if(!drawerLayout.isDrawerOpen(activityNavigationView)){
                        drawerLayout.openDrawer(activityNavigationView);
                    }

                    else if(drawerLayout.isDrawerOpen(userNavigationView)){
                        drawerLayout.closeDrawer(userNavigationView);
                    }
                }
            }.start();
        }

        else if(view == todoActivityBtn){
            Intent intent = new Intent(HomeActivity.this, TodoActivity.class);
            startActivity(intent);
        }

        else if(view == journalActivityBtn){
            Intent intent = new Intent(HomeActivity.this, JournalActivity.class);
            startActivity(intent);
        }

        else if(view == walletActivityBtn){
            Intent intent = new Intent(HomeActivity.this, WalletActivity.class);
            startActivity(intent);
        }

        else if(view == reminderActivityBtn){
            Intent intent = new Intent(HomeActivity.this, ReminderActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        if(id == R.id.user_profile_option) {
            Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.user_voice_command_option) {

        } else if (id == R.id.user_home_option) {
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(intent);
        } else if (id == R.id.user_todo_option) {
            Intent intent = new Intent(getApplicationContext(), TodoActivity.class);
            startActivity(intent);
        } else if (id == R.id.user_journal_option) {
            Intent intent = new Intent(getApplicationContext(), JournalActivity.class);
            startActivity(intent);
        } else if (id == R.id.user_wallet_option) {
            Intent intent = new Intent(getApplicationContext(), WalletActivity.class);
            startActivity(intent);
        } else if (id == R.id.user_reminder_option) {
            Intent intent = new Intent(getApplicationContext(), ReminderActivity.class);
            startActivity(intent);
        } else if (id == R.id.user_settings_option) {
            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.user_about_option) {
            Intent intent = new Intent(getApplicationContext(), AboutActivity.class);
            startActivity(intent);
        }else if (id == R.id.user_sign_out_option) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
            startActivity(intent);
            finish();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
