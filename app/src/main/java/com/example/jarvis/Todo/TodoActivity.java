package com.example.jarvis.Todo;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jarvis.About.AboutActivity;
import com.example.jarvis.Firebase.FirebaseDataUpdate;
import com.example.jarvis.Home.HomeActivity;
import com.example.jarvis.Journal.JournalActivity;
import com.example.jarvis.R;
import com.example.jarvis.Reminder.AlertReceiver;
import com.example.jarvis.Reminder.ReminderActivity;
import com.example.jarvis.SQLite.SQLiteDatabaseHelper;
import com.example.jarvis.Settings.SettingsActivity;
import com.example.jarvis.Util.NetworkReceiver;
import com.example.jarvis.Util.RecyclerTouchListener;
import com.example.jarvis.Wallet.WalletActivity;
import com.example.jarvis.WelcomeScreen.WelcomeActivity;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class TodoActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener, RecognitionListener, View.OnTouchListener {

    /** Network Variables */
    private BroadcastReceiver networkReceiver = null;

    /** Firebase Variables */
    private static final int RC_SIGN_IN = 1;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth.AuthStateListener mAuthListener;
    GoogleSignInOptions googleSignInOptions;

    /** Toolbar */
    private Toolbar toolbar;
    private TextView activityTitle;

    /** Drawer Variables */
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;

    private NavigationView userNavigationView;
    private NavigationView activityNavigationView;

    private ImageView profilePictureImageView;
    private TextView profileEmailTextView;
    private Button userDrawerBtn;
    private Button activityDrawerBtn;

    private PieChart pieChart;

    private Button daily, weekly, monthly;

    /** FAB */
    private FloatingActionButton addTodoBtn;

    private AlarmManager alarmManager;

    /** RecyclerView Variables */
    RecyclerView todoRecyclerView;
    ArrayList<Task> tasks;
    TaskAdapter taskAdapter;
    RecyclerTouchListener touchListener;

    /** Voice Command Variables */
    private static final int REQUEST_RECORD_PERMISSION = 100;
    private ProgressBar progressBar;
    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;
    private String LOG_TAG = "TodoActivity";
    private ToggleButton voiceCommandToggleButton;

    private TextToSpeech mTTS;

    private boolean isVcOn;

    public static final String CHANNEL_ID = "todoNotificationChannelId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);

        initializeVariables();
        setUI();
        handleDatabase();
        setVoiceCommandFeature();
        isVoiceCommandOn();
        createTodoNotificationChannel();

    }

    ////////////
    public void createTodoNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "whatever", NotificationManager.IMPORTANCE_HIGH);
            channel.enableVibration(true);
            channel.enableLights(true);
            channel.setLightColor(R.color.colorPrimary);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void initializeVariables(){
        tasks = new ArrayList<Task>();
        isVcOn = false;
    }

    void setUI(){
        findXmlElements();
        setToolbar();
        setListeners();
        initializeUI();
    }

    public void findXmlElements(){
        // Drawer
        drawerLayout = (DrawerLayout) findViewById(R.id.todo_drawer_layout);

        // Toolbar
        toolbar = (Toolbar) findViewById(R.id.todo_toolbar);
        userDrawerBtn = (Button) findViewById(R.id.user_drawer_btn);
        activityDrawerBtn = (Button) findViewById(R.id.activity_drawer_btn);
        activityTitle = (TextView) findViewById(R.id.activity_title);

        // Main
        addTodoBtn = (FloatingActionButton) findViewById(R.id.todo_add_todo_btn);
        userNavigationView = (NavigationView) findViewById(R.id.user_navigation_view);
        todoRecyclerView = (RecyclerView) findViewById(R.id.todo_recycler_view);

        profilePictureImageView = (ImageView) userNavigationView.getHeaderView(0).findViewById(R.id.user_profile_picture);
        profileEmailTextView = (TextView) userNavigationView.getHeaderView(0).findViewById(R.id.user_profile_email);

        // Voice Command
        progressBar = (ProgressBar) findViewById(R.id.todo_progress_bar);
        voiceCommandToggleButton = (ToggleButton) findViewById(R.id.todo_voice_command_toggle_btn);

        // Activity Navigation Drawer
        activityNavigationView = (NavigationView) findViewById(R.id.todo_navigation_view);
        View activityNavigationViewHeaderView = activityNavigationView.getHeaderView(0);
        pieChart = (PieChart) activityNavigationViewHeaderView.findViewById(R.id.todo_piechart);

        daily = (Button) activityNavigationViewHeaderView.findViewById(R.id.todo_pie_daily);
        weekly = (Button) activityNavigationViewHeaderView.findViewById(R.id.todo_pie_weekly);
        monthly = (Button) activityNavigationViewHeaderView.findViewById(R.id.todo_pie_monthly);

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
    }

    public void setToolbar(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    public void setListeners(){
        // Drawer
        drawerLayout.setDrawerListener(drawerToggle);

        // Buttons
        userDrawerBtn.setOnClickListener(this);
        activityDrawerBtn.setOnClickListener(this);
        addTodoBtn.setOnClickListener(this);

        // Navigation Drawer
        userNavigationView.setNavigationItemSelectedListener(this);
        activityNavigationView.setNavigationItemSelectedListener(this);

        daily.setOnClickListener(this);
        weekly.setOnClickListener(this);
        monthly.setOnClickListener(this);

        // RecyclerView
        todoRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Swipe Options
        touchListener = new RecyclerTouchListener(this,todoRecyclerView);
        touchListener
                .setClickable(new RecyclerTouchListener.OnRowClickListener() {
                    @Override
                    public void onRowClicked(int position) {
                        handleCheckAction(position);
//                        showToast("Double Tap on Task to Mark as Completed");
                    }

                    @Override
                    public void onIndependentViewClicked(int independentViewID, int position) {

                    }
                })
                .setSwipeOptionViews(R.id.todo_item_delete_rl,R.id.todo_item_edit_rl)
                .setSwipeable(R.id.todo_item_fg, R.id.todo_item_bg_end, new RecyclerTouchListener.OnSwipeOptionsClickListener() {
                    @Override
                    public void onSwipeOptionClicked(int viewID, int position) {
                        switch (viewID){
                            case R.id.todo_item_delete_rl:
                                handleDeleteAction(position);
                                break;
                            case R.id.todo_item_edit_rl:
                                handleEditAction(position);
                                break;
                        }
                    }
                })
                .setLongClickable(false, new RecyclerTouchListener.OnRowLongClickListener() {
            @Override
            public void onRowLongClicked(int position) {

            }
        });

        // Voice Command On/Off
        voiceCommandToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setIndeterminate(true);
                    ActivityCompat.requestPermissions
                            (TodoActivity.this,
                                    new String[]{Manifest.permission.RECORD_AUDIO},
                                    REQUEST_RECORD_PERMISSION);
                } else {
                    progressBar.setIndeterminate(false);
                    progressBar.setVisibility(View.INVISIBLE);
                    speech.stopListening();
                }
            }
        });

    }

    public void initializeUI(){
        mAuth = FirebaseAuth.getInstance();
        networkReceiver = new NetworkReceiver();
        broadcastIntent();

        activityDrawerBtn.setBackgroundResource(R.drawable.icon_activity_todo);
        activityTitle.setText(R.string.todo_txt);

        Picasso.get().load(HomeActivity.getActiveUser().getPhoto()).into(profilePictureImageView);
        profileEmailTextView.setText(HomeActivity.getActiveUser().getEmail());

        userNavigationView.getMenu().findItem(R.id.user_todo_option).setCheckable(true);
        userNavigationView.getMenu().findItem(R.id.user_todo_option).setChecked(true);


        // Initialize PieChart
        SQLiteDatabaseHelper sqLiteDatabaseHelper = new SQLiteDatabaseHelper(this);
        double ratio = sqLiteDatabaseHelper.getTodoFeedbackRatio("weekly");
        setPieChart(ratio);
        pressedWeekly();

        progressBar.setVisibility(View.INVISIBLE);
    }

    public void setPieChart(double ratio){
        float completed = (float) ((float) ratio*100.00);
        float incomplete = (float) 100.00 - completed;

        pieChart.setUsePercentValues(true);
        pieChart.animateXY(500, 1000);

        List<PieEntry> value = new ArrayList<>();
        value.add(new PieEntry(completed, ""));
        value.add(new PieEntry(incomplete, ""));

        PieDataSet pieDataSet = new PieDataSet(value, "");

        ArrayList<Integer> colors = new ArrayList<Integer>();
        final int[] ALL_COLORS = {Color.rgb(76,175,80), Color.rgb(241,91,64)};
        for(int color: ALL_COLORS)
            colors.add(color);

        pieDataSet.setColors(colors);

        pieDataSet.setSliceSpace(2);
        pieDataSet.setDrawIcons(false);
        pieDataSet.setValueTextColor(Color.WHITE);
        pieDataSet.setDrawIcons(false);
        pieDataSet.setHighlightEnabled(true);

        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);

        pieChart.setDrawEntryLabels(false);
        pieChart.setDrawSliceText(false); // To remove slice text
        pieChart.setDrawMarkers(false); // To remove markers when click
        pieChart.setDrawEntryLabels(false); // To remove labels from piece of pie
        pieChart.getDescription().setEnabled(false); // To remove description of pie
        pieChart.setDrawMarkers(false);
        pieChart.setEntryLabelColor(Color.WHITE);

        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(50f);
        Legend l = pieChart.getLegend();
        l.setEnabled(false);
    }

    public void handleDatabase(){
        SQLiteDatabaseHelper sqLiteDatabaseHelper = new SQLiteDatabaseHelper(this);
        SQLiteDatabase sqLiteDatabase = sqLiteDatabaseHelper.getReadableDatabase();

        loadData(sqLiteDatabaseHelper);
    }

    public void loadData(SQLiteDatabaseHelper sqLiteDatabaseHelper){
        tasks.clear();
        tasks = sqLiteDatabaseHelper.loadTodoItems();

        todoRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        taskAdapter = new TaskAdapter(TodoActivity.this, tasks);
        todoRecyclerView.setAdapter(taskAdapter);
        taskAdapter.notifyDataSetChanged();
    }

    void handleDeleteAction(int position){
        SQLiteDatabaseHelper sqLiteDatabaseHelper = new SQLiteDatabaseHelper(this);
        SQLiteDatabase sqLiteDatabase = sqLiteDatabaseHelper.getReadableDatabase();

        sqLiteDatabaseHelper.deleteTodo(tasks.get(position).getYear(),tasks.get(position).getMonth(),tasks.get(position).getDay(), tasks.get(position).getTitle());

        //delete TodoNotification
        Integer todoNotificationID;
        todoNotificationID = (Integer.parseInt(tasks.get(position).getYear())+Integer.parseInt(tasks.get(position).getMonth())+Integer.parseInt(tasks.get(position).getDay())+Integer.parseInt(tasks.get(position).getHour())+Integer.parseInt(tasks.get(position).getMinute()));
        showToast("temporary deleted id: "+todoNotificationID.toString());

        Intent intent = new Intent(this, todoAlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, todoNotificationID, intent, 0);
        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();

        loadData(sqLiteDatabaseHelper);

        // Refresh PieChart
        double ratio = sqLiteDatabaseHelper.getTodoFeedbackRatio("weekly");
        setPieChart(ratio);
        pressedWeekly();

    }

    void handleEditAction(int position){
        SQLiteDatabaseHelper sqLiteDatabaseHelper = new SQLiteDatabaseHelper(this);
        SQLiteDatabase sqLiteDatabase = sqLiteDatabaseHelper.getReadableDatabase();

        Task task = sqLiteDatabaseHelper.findTodo(tasks.get(position).getYear(),tasks.get(position).getMonth(),tasks.get(position).getDay(),tasks.get(position).getTitle());

        Intent intent = new Intent(getApplicationContext(), UpdateTaskActivity.class);
        intent.putExtra("todo_title", task.getTitle());
        intent.putExtra("todo_description", task.getDescription());
        intent.putExtra("todo_year", task.getYear());
        intent.putExtra("todo_month", task.getMonth());
        intent.putExtra("todo_day", task.getDay());
        intent.putExtra("todo_hour", task.getHour());
        intent.putExtra("todo_minute", task.getMinute());
        intent.putExtra("todo_reminderState", task.getReminderState().toString());
        intent.putExtra("todo_isCompleted", task.getIsCompleted().toString());
        intent.putExtra("todo_isDeleted", task.getIsDeleted().toString());
        intent.putExtra("todo_isIgnored", task.getIsIgnored().toString());
        intent.putExtra("todo_updateTimestamp", task.getUpdateTimestamp());

        startActivity(intent);
    }

    void handleCheckAction(int position){
        // Change Checkbox State & Show Toast
        if(tasks.get(position).getIsCompleted()==0) {
            tasks.get(position).setIsCompleted(1);

            //delete reminder

            Integer todoNotificationID;
            todoNotificationID = (Integer.parseInt(tasks.get(position).getYear())+Integer.parseInt(tasks.get(position).getMonth())+Integer.parseInt(tasks.get(position).getDay())+Integer.parseInt(tasks.get(position).getHour())+Integer.parseInt(tasks.get(position).getMinute()));
            showToast("Task complete id: "+todoNotificationID.toString());

            Intent intent = new Intent(this, todoAlertReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, todoNotificationID, intent, 0);
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();

            showToast("Marked as Completed");
        }
        else {
            tasks.get(position).setIsCompleted(0);

            //add reminder

            Calendar c = Calendar.getInstance();
            c.set(Calendar.YEAR, Integer.parseInt(tasks.get(position).getYear()));
            c.set(Calendar.MONTH, Integer.parseInt(tasks.get(position).getMonth()));
            c.set(Calendar.DAY_OF_MONTH, Integer.parseInt(tasks.get(position).getDay()));
            c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(tasks.get(position).getHour()));
            c.set(Calendar.MINUTE, Integer.parseInt(tasks.get(position).getMinute()));
            c.set(Calendar.SECOND, 0);
            Integer unmarkTodoNotificationID;
            unmarkTodoNotificationID = (Integer.parseInt(tasks.get(position).getYear())+Integer.parseInt(tasks.get(position).getMonth())+ Integer.parseInt(tasks.get(position).getDay())+Integer.parseInt(tasks.get(position).getHour())+Integer.parseInt(tasks.get(position).getMinute()));
            showToast("mark notification id: "+unmarkTodoNotificationID.toString());

            if(tasks.get(position).getReminderState() == 1 && tasks.get(position).getIsCompleted() == 0) {
                Intent intent = new Intent(this, todoAlertReceiver.class);
                intent.putExtra("todoNotification", unmarkTodoNotificationID);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, unmarkTodoNotificationID, intent, 0);
                alarmManager.setExact(AlarmManager.RTC, c.getTimeInMillis(), pendingIntent);
            }

            showToast("Marked as Incomplete");
        }

        // Refresh RecyclerView
        taskAdapter = new TaskAdapter(TodoActivity.this, tasks);
        todoRecyclerView.setAdapter(taskAdapter);
        taskAdapter.notifyDataSetChanged();

        // Update Local Database
        SQLiteDatabaseHelper sqLiteDatabaseHelper = new SQLiteDatabaseHelper(this);
        SQLiteDatabase sqLiteDatabase = sqLiteDatabaseHelper.getReadableDatabase();
        sqLiteDatabaseHelper.updateTodo(tasks.get(position), tasks.get(position).getYear(), tasks.get(position).getMonth(), tasks.get(position).getDay(), tasks.get(position).getTitle());

        // Refresh PieChart
        double ratio = sqLiteDatabaseHelper.getTodoFeedbackRatio("weekly");
        setPieChart(ratio);
        pressedWeekly();

        sqLiteDatabase.close();
    }

    public void showToast(String message){
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        todoRecyclerView.addOnItemTouchListener(touchListener);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(TodoActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    /** ON CLICK HANDLING */

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
                        onClick(weekly);
                    }

                    else if(drawerLayout.isDrawerOpen(userNavigationView)){
                        drawerLayout.closeDrawer(userNavigationView);
                        onClick(weekly);
                    }
                }
            }.start();
        }
        else if(view == addTodoBtn){
            Intent intent = new Intent(getApplicationContext(), AddTaskActivity.class);
            startActivity(intent);
        }

        // Activity Navigation Drawer
        else if(view == daily){
            SQLiteDatabaseHelper sqLiteDatabaseHelper = new SQLiteDatabaseHelper(this);
            double ratio = sqLiteDatabaseHelper.getTodoFeedbackRatio("daily");
            setPieChart(ratio);
            pressedDaily();
        } else if(view == weekly){
            SQLiteDatabaseHelper sqLiteDatabaseHelper = new SQLiteDatabaseHelper(this);
            double ratio = sqLiteDatabaseHelper.getTodoFeedbackRatio("weekly");
            setPieChart(ratio);
            pressedWeekly();
        } else if(view == monthly){
            SQLiteDatabaseHelper sqLiteDatabaseHelper = new SQLiteDatabaseHelper(this);
            double ratio = sqLiteDatabaseHelper.getTodoFeedbackRatio("monthly");
            setPieChart(ratio);
            pressedMonthly();
        }
    }

    public void pressedDaily(){
        daily.setBackground(getResources().getDrawable(R.color.colorPrimary));
        daily.setTextColor(getResources().getColor(R.color.colorWhite));
        daily.setElevation(0);

        weekly.setBackground(getResources().getDrawable(R.color.colorWhite));
        weekly.setTextColor(getResources().getColor(R.color.colorPrimary));
        weekly.setElevation(6);

        monthly.setBackground(getResources().getDrawable(R.color.colorWhite));
        monthly.setTextColor(getResources().getColor(R.color.colorPrimary));
        monthly.setElevation(6);
    }

    public void pressedWeekly(){
        daily.setBackground(getResources().getDrawable(R.color.colorWhite));
        daily.setTextColor(getResources().getColor(R.color.colorPrimary));
        daily.setElevation(6);

        weekly.setBackground(getResources().getDrawable(R.color.colorPrimary));
        weekly.setTextColor(getResources().getColor(R.color.colorWhite));
        weekly.setElevation(0);

        monthly.setBackground(getResources().getDrawable(R.color.colorWhite));
        monthly.setTextColor(getResources().getColor(R.color.colorPrimary));
        monthly.setElevation(6);
    }

    public void pressedMonthly(){
        daily.setBackground(getResources().getDrawable(R.color.colorWhite));
        daily.setTextColor(getResources().getColor(R.color.colorPrimary));
        daily.setElevation(6);

        weekly.setBackground(getResources().getDrawable(R.color.colorWhite));
        weekly.setTextColor(getResources().getColor(R.color.colorPrimary));
        weekly.setElevation(6);

        monthly.setBackground(getResources().getDrawable(R.color.colorPrimary));
        monthly.setTextColor(getResources().getColor(R.color.colorWhite));
        monthly.setElevation(0);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        if (id == R.id.user_sync_option) {
            if(!isConnectedToInternet())
                Snackbar.make(drawerLayout, "Can't Sync Without Internet Access!", Snackbar.LENGTH_SHORT).show();
            else
                sync();
        } else if (id == R.id.user_home_option) {
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.user_todo_option) {

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
            if(!isConnectedToInternet())
                Snackbar.make(drawerLayout, "Can't Sign Out Without Internet Access!", Snackbar.LENGTH_SHORT).show();
            else
                signOut();
        } else if(id == R.id.todo_bin_option){
            Intent intent = new Intent(getApplicationContext(), TodoBinActivity.class);
            startActivity(intent);
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    /** VOICE COMMAND HANDLING */

    public void setVoiceCommandFeature(){
        speech = SpeechRecognizer.createSpeechRecognizer(this);
        Log.i(LOG_TAG, "isRecognitionAvailable: " + SpeechRecognizer.isRecognitionAvailable(this));
        speech.setRecognitionListener(this);

        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);

//        mTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
//            @Override
//            public void onInit(int status) {
//                if (status == TextToSpeech.SUCCESS) {
//                    int result = mTTS.setLanguage(Locale.ENGLISH);
//
//                    if (result == TextToSpeech.LANG_MISSING_DATA
//                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
//                        Log.e("TTS", "Language not supported");
//                    } else {
//
//                    }
//                } else {
//                    Log.e("TTS", "Initialization failed");
//                }
//            }
//        });
    }

    public void isVoiceCommandOn(){
        if(getIntent().getExtras()!=null){
            if(getIntent().getExtras().getString("voice_command")!=null ){
                if(Objects.equals(getIntent().getExtras().getString("voice_command"), "true")) {
                    enableVoiceCommand();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_RECORD_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    speech.startListening(recognizerIntent);
                } else {
                    Toast.makeText(TodoActivity.this, "Permission Denied!", Toast
                            .LENGTH_SHORT).show();
                }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
//        unregisterReceiver(networkReceiver);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (speech != null) {
            speech.destroy();
            Log.i(LOG_TAG, "destroy");
        }
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.i(LOG_TAG, "onBeginningOfSpeech");
        progressBar.setIndeterminate(false);
        progressBar.setMax(10);
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        Log.i(LOG_TAG, "onBufferReceived: " + buffer);
    }

    @Override
    public void onEvent(int arg0, Bundle arg1) {
        Log.i(LOG_TAG, "onEvent");
    }

    @Override
    public void onPartialResults(Bundle arg0) {
        Log.i(LOG_TAG, "onPartialResults");
    }

    @Override
    public void onReadyForSpeech(Bundle arg0) {
        Log.i(LOG_TAG, "onReadyForSpeech");
    }

    @Override
    public void onRmsChanged(float rmsdB) {
        Log.i(LOG_TAG, "onRmsChanged: " + rmsdB);
        progressBar.setProgress((int) rmsdB);
    }

    @Override
    public void onResults(Bundle results) {
        Log.i(LOG_TAG, "onResults");
        ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

        String text = "";
        for (String result : matches)
            text += result + "\n";

        if(matches.get(0).equals("sync data")){
            showToast("Data Synced");
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            intent.putExtra("voice_command", "true");
            startActivity(intent);
        }  else if(matches.get(0).equals("go to home")){
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            intent.putExtra("voice_command", "true");
            startActivity(intent);
        } else if(matches.get(0).equals("go to to do")){
            Intent intent = new Intent(getApplicationContext(), TodoActivity.class);
            intent.putExtra("voice_command", "true");
            startActivity(intent);
        } else if(matches.get(0).equals("go to wallet")){
            Intent intent = new Intent(getApplicationContext(), WalletActivity.class);
            intent.putExtra("voice_command", "true");
            startActivity(intent);
        } else if(matches.get(0).equals("go to journal")){
            Intent intent = new Intent(getApplicationContext(), JournalActivity.class);
            intent.putExtra("voice_command", "true");
            startActivity(intent);
        } else if(matches.get(0).equals("go to reminder")){
            Intent intent = new Intent(getApplicationContext(), ReminderActivity.class);
            intent.putExtra("voice_command", "true");
            startActivity(intent);
        } else if(matches.get(0).equals("go to settings")){
            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            intent.putExtra("voice_command", "true");
            startActivity(intent);
        } else if(matches.get(0).equals("go to about")){
            Intent intent = new Intent(getApplicationContext(), AboutActivity.class);
            intent.putExtra("voice_command", "true");
            startActivity(intent);
        } else if(matches.get(0).equals("please sign out")){
            Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
            startActivity(intent);
            finish();
        }

        else if(matches.get(0).equals("open activity options")){
            activityDrawerBtn.callOnClick();
            restartVoiceCommand();
        } else if(matches.get(0).equals("close activity options")){
            activityDrawerBtn.callOnClick();
            restartVoiceCommand();
        } else if(matches.get(0).equals("open user options")){
            userDrawerBtn.callOnClick();
            restartVoiceCommand();
        } else if(matches.get(0).equals("close user options")){
            userDrawerBtn.callOnClick();
            restartVoiceCommand();
        }

        else if(matches.get(0).equals("add a new task")){

        } else if(matches.get(0).equals("update a task")){

        } else if(matches.get(0).equals("delete a task")){

        } else if(matches.get(0).equals("mark a task as completed")){

        }

        else if(matches.get(0).equals("scroll up")){
            disableVoiceCommand();
            scroll(-1);
        } else if(matches.get(0).equals("scroll down")){
            disableVoiceCommand();
            scroll(1);
        } else if(matches.get(0).equals("turn off voice command")){
            disableVoiceCommand();
        }

//        else if(matches.get(0).equals("read")){
////            read();
//        } else if(matches.get(0).equals("stop reading")){
//
//        }
        else {
            showToast("Didn't Recognize \"" + matches.get(0) + "\"! Try Again!");
            restartVoiceCommand();
        }
    }

    @Override
    public void onEndOfSpeech() {
        Log.i(LOG_TAG, "onEndOfSpeech");
        if(isVcOn) {
            restartVoiceCommand();
        }
        else
            disableVoiceCommand();
    }

    @Override
    public void onError(int errorCode) {
        String errorMessage = getErrorText(errorCode);
//        if (errorMessage.equals("Client Side Error") || errorMessage.equals("No Such Command Found")  ||  errorMessage.equals("No Command Given")) {
//            restartVoiceCommand();
//        } else {
//            showToast(errorMessage.toUpperCase());
//            disableVoiceCommand();
//        }

        showToast(errorMessage.toUpperCase());
        restartVoiceCommand();
    }

    public static String getErrorText(int errorCode) {
        String message;
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "Audio Recording Error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "Client Side Error";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "Insufficient Permissions";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "Network Error";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "Network Found";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "No Such Command Found";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "Recognition Service Busy";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "Server Error";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "No Command Given";
                break;
            default:
                message = "Didn't Understand. Please, Try Again!";
                break;
        }
        return message;
    }

    public void disableVoiceCommand(){
        isVcOn = false;
        voiceCommandToggleButton.setChecked(false);
    }

    public void enableVoiceCommand(){
        isVcOn = true;
        voiceCommandToggleButton.setChecked(true);
    }

    public void restartVoiceCommand(){
        voiceCommandToggleButton.setChecked(true);
    }

    //********************************************************

    public void scroll(Integer difference){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        if(difference==1) {
            todoRecyclerView.smoothScrollBy(0, height, null);
        }
        else {
            todoRecyclerView.smoothScrollBy(0, - height, null);
        }
//
//        todoRecyclerView.setLayoutManager(new SpeedyLinearLayoutManager(getApplicationContext(), SpeedyLinearLayoutManager.VERTICAL, false));
//        RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(getApplicationContext()) {
//            @Override protected int getVerticalSnapPreference() {
//                return LinearSmoothScroller.SNAP_TO_START;
//            }
//        };
//
//        int range;
//        if(difference==-1)
//            range = todoRecyclerView.getVerticalScrollbarPosition()+1;
//        else
//            range = todoRecyclerView.getAdapter().getItemCount()-todoRecyclerView.getVerticalScrollbarPosition()-1;
//
//        int interval = 700;
//
//        CountDownTimer scrollThread = new CountDownTimer((range)*interval, interval){
//            int position = todoRecyclerView.getVerticalScrollbarPosition();
//            @Override
//            public void onTick(long l) {
//                smoothScroller.setTargetPosition(position);
//                todoRecyclerView.getLayoutManager().startSmoothScroll(smoothScroller);
//                position+=difference;
//            }
//
//            @Override
//            public void onFinish() {
//                showToast("That's All");
//            }
//        };
//
//        scrollThread.start();
    }

    // For Auto Reader
    private final Handler readingHandler = new Handler();

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if(view.getId() == R.id.todo_drawer_layout)
            disableVoiceCommand();
        return true;
    }

    public class readingRunnable implements Runnable {
        int i=0;
        String isCompleted, time, date, amPm, speech;
        Task task;
        ArrayList<Task> tasks;

        public readingRunnable(ArrayList<Task> tasks) {
            this.tasks = tasks;
        }

        @Override
        public void run() {
            if (task.getIsCompleted() == 1)
                isCompleted = " is completed ";
            else
                isCompleted = " is to be completed ";

            /** Formatting Date to set on EditText */
            String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "Deccember"};
            date = "on " + task.getDay() + " " + months[Integer.parseInt(task.getMonth())] + ", " + task.getYear();


            /** Formatting Time to set on EditText */
            if (task.getHour() != null && task.getMinute() != null) {
                if (Integer.parseInt(task.getHour()) >= 12)
                    amPm = " PM";
                else
                    amPm = " AM";
                time = " at" + Integer.toString(Integer.parseInt(task.getHour()) % 12) + ":" + task.getMinute() + amPm;
            } else
                time = "";

            showToast("Task " + task.getTitle() + isCompleted + time + date);
            speech = "Task " + task.getTitle() + isCompleted + time + date;

            mTTS.speak(speech, TextToSpeech.QUEUE_FLUSH, null);
            readingHandler.postDelayed(this, 5000);
        }
    }

    private void read() {
        mTTS.setPitch((float) 0.8);
        mTTS.setSpeechRate((float) 0.9);

        SQLiteDatabaseHelper sqLiteDatabaseHelper = new SQLiteDatabaseHelper(this);
        SQLiteDatabase sqLiteDatabase = sqLiteDatabaseHelper.getReadableDatabase();
        tasks = sqLiteDatabaseHelper.loadTodoItems();

        readingRunnable readingObject = new readingRunnable(tasks);
        readingHandler.post(readingObject);

        Runnable runnable = new readingRunnable(tasks);
        new Thread(runnable).start();


        CountDownTimer readingThread = new CountDownTimer(5000*tasks.size(), 5000){
            int i=0;
            String isCompleted, time, date, amPm, speech;
            Task task;

            @Override
            public void onTick(long l) {
                task = tasks.get(i);
                if(task.getIsCompleted()==1)
                    isCompleted = " is completed ";
                else
                    isCompleted = " is to be completed ";

                /** Formatting Date to set on EditText */
                String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "Deccember"};
                date = "on " + task.getDay() + " " + months[Integer.parseInt(task.getMonth())] + ", " + task.getYear();


                /** Formatting Time to set on EditText */
                if(task.getHour()!=null && task.getMinute()!=null){
                    if(Integer.parseInt(task.getHour())>=12)
                        amPm = " PM";
                    else
                        amPm = " AM";
                    time = " at" + Integer.toString(Integer.parseInt(task.getHour()) % 12) + ":" + task.getMinute() + amPm;
                }
                else
                    time = "";

                showToast("Task " + task.getTitle() + isCompleted + time + date);
                speech = "Task " + task.getTitle() + isCompleted + time + date;

                mTTS.speak(speech, TextToSpeech.QUEUE_FLUSH, null);

                i++;
            }

            @Override
            public void onFinish() {
//                showToast("That's all for now.");
                speech = "That's all for now.";

                mTTS.speak(speech, TextToSpeech.QUEUE_FLUSH, null);
            }
        };

        readingThread.start();
    }


    /** Firebase Authentication Handling */

    private void initializeGoogleVariable() {
        mAuth = FirebaseAuth.getInstance();

        // Configuring Google Sign In
        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
    }

    /** Sync & SignOut */

    public void sync(){
        SQLiteDatabaseHelper sqLiteDatabaseHelper = new SQLiteDatabaseHelper(getApplicationContext());
        String uid = sqLiteDatabaseHelper.getUid();

        FirebaseDataUpdate add = new FirebaseDataUpdate(FirebaseFirestore.getInstance(), uid);
        add.queryOnMultipleTodoInput(sqLiteDatabaseHelper.syncTodoItems());
        add.queryOnMultipleWalletInput(sqLiteDatabaseHelper.syncWalletItems());

        sqLiteDatabaseHelper.updateSyncTime(uid);
    }

    public void signOut() {
    //    sync();
        initializeGoogleVariable();
        mAuth.signOut();

        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
                        SQLiteDatabaseHelper sqLiteDatabaseHelper = new SQLiteDatabaseHelper(getApplicationContext());
                        SQLiteDatabase sqLiteDatabase = sqLiteDatabaseHelper.getWritableDatabase();

                        sqLiteDatabaseHelper.refreshDatabase(sqLiteDatabase);

                        Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
    }

    /** For Checking Network Connection */
    public void broadcastIntent() {
        registerReceiver(networkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    public boolean isConnectedToInternet(){
        ConnectivityManager cm =
                (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }
}
