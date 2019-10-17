package com.example.jarvis.Wallet;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jarvis.About.AboutActivity;
import com.example.jarvis.Home.HomeActivity;
import com.example.jarvis.Journal.JournalActivity;
import com.example.jarvis.Profile.ProfileActivity;
import com.example.jarvis.R;
import com.example.jarvis.Reminder.ReminderActivity;
import com.example.jarvis.SQLite.SQLiteDatabaseHelper;
import com.example.jarvis.Settings.SettingsActivity;
import com.example.jarvis.Todo.TodoActivity;
import com.example.jarvis.Util.RecyclerTouchListener;
import com.example.jarvis.WelcomeScreen.WelcomeActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class WalletActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;

    private NavigationView userNavigationView;
    private NavigationView activityNavigationView;

    private Toolbar toolbar;

    private Button userDrawerBtn;
    private Button activityDrawerBtn;
    private FloatingActionButton fab;

    private TextView activityTitle;

    DatabaseReference reference;
    FirebaseDatabase database;
    RecyclerView walletItems;
    ArrayList<Record> records;
    RecordAdapter recordAdapter;

    RecyclerTouchListener touchListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        settingUpXmlElements();
        loadingDataFromDB();
    }

    void settingUpXmlElements(){
        // Finding the Parent Layout
        drawerLayout = (DrawerLayout) findViewById(R.id.wallet_drawer_layout);
        drawerLayout.setDrawerListener(drawerToggle);

        // Setting Up Toolbar
        toolbar = (Toolbar) findViewById(R.id.wallet_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Setting Up Components Inside Toolbar
        userDrawerBtn = (Button) findViewById(R.id.user_drawer_btn);
        activityDrawerBtn = (Button) findViewById(R.id.activity_drawer_btn);
        fab = (FloatingActionButton) findViewById(R.id.wallet_fab);

        userDrawerBtn.setOnClickListener(this);
        activityDrawerBtn.setOnClickListener(this);
        fab.setOnClickListener(this);

        activityDrawerBtn.setBackgroundResource(R.drawable.icon_activity_wallet);

        activityTitle = (TextView) findViewById(R.id.activity_title);
        activityTitle.setText(R.string.wallet_txt);

        // Two Navigation View for Two Navigation Drawers
        userNavigationView = (NavigationView) findViewById(R.id.user_navigation_view);
        activityNavigationView = (NavigationView) findViewById(R.id.wallet_navigation_view);

        userNavigationView.setNavigationItemSelectedListener(this);
        activityNavigationView.setNavigationItemSelectedListener(this);

        userNavigationView.getMenu().findItem(R.id.user_wallet_option).setCheckable(true);
        userNavigationView.getMenu().findItem(R.id.user_wallet_option).setChecked(true);

        // Recycler View
        walletItems = findViewById(R.id.wallet_items);
        walletItems.setLayoutManager(new LinearLayoutManager(this));

        records = new ArrayList<Record>();
    }

    void loadingDataFromDB(){
        SQLiteDatabaseHelper sqLiteDatabaseHelper = new SQLiteDatabaseHelper(this);
        SQLiteDatabase sqLiteDatabase = sqLiteDatabaseHelper.getReadableDatabase();
        dataRetrieveAndShow(sqLiteDatabaseHelper);

        touchListener = new RecyclerTouchListener(this,walletItems);
        touchListener
                .setClickable(new RecyclerTouchListener.OnRowClickListener() {
                    @Override
                    public void onRowClicked(int position) {
                        Toast.makeText(getApplicationContext(),records.get(position).getTitle(),Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onIndependentViewClicked(int independentViewID, int position) {

                    }
                })
                .setSwipeOptionViews(R.id.wallet_item_delete_rl,R.id.wallet_item_edit_rl)
                .setSwipeable(R.id.wallet_item_fg, R.id.wallet_item_bg, new RecyclerTouchListener.OnSwipeOptionsClickListener() {
                    @Override
                    public void onSwipeOptionClicked(int viewID, int position) {
                        switch (viewID){
                            case R.id.wallet_item_delete_rl:
                                handleDeleteAction(position);
                                break;
                            case R.id.wallet_item_edit_rl:
                                handleEditAction(position);
                                break;
                        }
                    }
                });
    }

    void dataRetrieveAndShow(SQLiteDatabaseHelper sqLiteDatabaseHelper){
        records.clear();
        records = sqLiteDatabaseHelper.loadWalletItems();
        walletItems.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recordAdapter = new RecordAdapter(WalletActivity.this, records);
        walletItems.setAdapter(recordAdapter);
        recordAdapter.notifyDataSetChanged();
    }

    void handleDeleteAction(int position){
        SQLiteDatabaseHelper sqLiteDatabaseHelper = new SQLiteDatabaseHelper(this);
        SQLiteDatabase sqLiteDatabase = sqLiteDatabaseHelper.getReadableDatabase();
        sqLiteDatabaseHelper.deleteRecord(sqLiteDatabaseHelper.getUserId(HomeActivity.getCurrentUser()),
                records.get(position).getYear(),records.get(position).getMonth(),records.get(position).getDay(),
                records.get(position).getTitle(),
                records.get(position).getType());

        dataRetrieveAndShow(sqLiteDatabaseHelper);
        showToast("Delete Action");
    }

    void handleEditAction(int position){
        SQLiteDatabaseHelper sqLiteDatabaseHelper = new SQLiteDatabaseHelper(this);
        SQLiteDatabase sqLiteDatabase = sqLiteDatabaseHelper.getReadableDatabase();

        Record record = sqLiteDatabaseHelper.findRecord(sqLiteDatabaseHelper.getUserId(HomeActivity.getCurrentUser()),
                records.get(position).getYear(),records.get(position).getMonth(),records.get(position).getDay(),
                records.get(position).getTitle(),
                records.get(position).getType());

        Intent intent = new Intent(getApplicationContext(), UpdateRecordActivity.class);
        intent.putExtra("user_id", record.getUserId().toString());
        intent.putExtra("wallet_title", record.getTitle());
        intent.putExtra("wallet_description", record.getDescription());

        intent.putExtra("wallet_year", record.getYear());
        intent.putExtra("wallet_month", record.getMonth());
        intent.putExtra("wallet_day", record.getDay());

        intent.putExtra("wallet_type", record.getType().toString());
        intent.putExtra("wallet_amount", record.getAmount());

        showToast("Edit Action");
        startActivity(intent);
    }


    @Override
    public void onResume() {
        super.onResume();
        walletItems.addOnItemTouchListener(touchListener);
    }

    public void showToast(String message){
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(WalletActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
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
        else if(view == fab){
            Intent intent = new Intent(getApplicationContext(), AddRecordActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        if (id == R.id.user_sync_option) {

        } else if(id == R.id.user_profile_option) {
            Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.user_voice_command_option) {

        } else if (id == R.id.user_home_option) {
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.user_todo_option) {
            Intent intent = new Intent(getApplicationContext(), TodoActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.user_journal_option) {
            Intent intent = new Intent(getApplicationContext(), JournalActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.user_wallet_option) {

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
