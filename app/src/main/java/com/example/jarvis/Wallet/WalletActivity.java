package com.example.jarvis.Wallet;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
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
import com.example.jarvis.Firebase.FirebaseDataAdd;
import com.example.jarvis.Firebase.FirebaseDataUpdate;
import com.example.jarvis.Home.HomeActivity;
import com.example.jarvis.Journal.JournalActivity;
import com.example.jarvis.R;
import com.example.jarvis.Reminder.ReminderActivity;
import com.example.jarvis.SQLite.SQLiteDatabaseHelper;
import com.example.jarvis.Settings.SettingsActivity;
import com.example.jarvis.Todo.TodoActivity;
import com.example.jarvis.UserHandling.SignInActivity;
import com.example.jarvis.Util.NetworkReceiver;
import com.example.jarvis.Util.RecyclerTouchListener;
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
import java.util.List;
import java.util.Objects;

public class WalletActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener, RecognitionListener {
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

    private Button binBtn;

    /** FAB */
    private FloatingActionButton fab;

    /** RecyclerView Variables */
    RecyclerView walletRecyclerView;
    ArrayList<Record> records;
    RecordAdapter recordAdapter;

    RecyclerTouchListener touchListener;

    /** Voice Command Variables */
    private static final int REQUEST_RECORD_PERMISSION = 100;
    private ProgressBar progressBar;
    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;
    private String LOG_TAG = "WalletActivity";
    private ToggleButton voiceCommandToggleButton;

    private TextToSpeech mTTS;

    private boolean isVcOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        initializeVariables();
        setUI();
        handleDatabase();
        setVoiceCommandFeature();
        isVoiceCommandOn();
    }

    public void initializeVariables(){
        records = new ArrayList<Record>();
    }

    void setUI(){
        findXmlElements();
        setToolbar();
        setListeners();
        initializeUI();
    }

    public void findXmlElements(){
        drawerLayout = (DrawerLayout) findViewById(R.id.wallet_drawer_layout);
        toolbar = (Toolbar) findViewById(R.id.wallet_toolbar);
        userDrawerBtn = (Button) findViewById(R.id.user_drawer_btn);
        activityDrawerBtn = (Button) findViewById(R.id.activity_drawer_btn);
        fab = (FloatingActionButton) findViewById(R.id.wallet_fab);
        activityTitle = (TextView) findViewById(R.id.activity_title);
        userNavigationView = (NavigationView) findViewById(R.id.user_navigation_view);

        walletRecyclerView = findViewById(R.id.wallet_recycler_view);

        progressBar = (ProgressBar) findViewById(R.id.wallet_progress_bar);
        voiceCommandToggleButton = (ToggleButton) findViewById(R.id.wallet_voice_command_toggle_btn);

        profilePictureImageView = (ImageView) userNavigationView.getHeaderView(0).findViewById(R.id.user_profile_picture);
        profileEmailTextView = (TextView) userNavigationView.getHeaderView(0).findViewById(R.id.user_profile_email);

        // Activity Navigation Drawer
        activityNavigationView = (NavigationView) findViewById(R.id.wallet_navigation_view);
        View activityNavigationViewHeaderView = activityNavigationView.getHeaderView(0);
        pieChart = (PieChart) activityNavigationViewHeaderView.findViewById(R.id.wallet_piechart);

        binBtn = (Button) activityNavigationViewHeaderView.findViewById(R.id.wallet_bin_option);

        daily = (Button) activityNavigationViewHeaderView.findViewById(R.id.wallet_pie_daily);
        weekly = (Button) activityNavigationViewHeaderView.findViewById(R.id.wallet_pie_weekly);
        monthly = (Button) activityNavigationViewHeaderView.findViewById(R.id.wallet_pie_monthly);
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
        fab.setOnClickListener(this);

        // Navigation Drawer
        userNavigationView.setNavigationItemSelectedListener(this);
        activityNavigationView.setNavigationItemSelectedListener(this);

        daily.setOnClickListener(this);
        weekly.setOnClickListener(this);
        monthly.setOnClickListener(this);

        // Recycler View
        walletRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Swipe Options
        touchListener = new RecyclerTouchListener(this,walletRecyclerView);
        touchListener
                .setClickable(new RecyclerTouchListener.OnRowClickListener() {
                    @Override
                    public void onRowClicked(int position) {
//                        Toast.makeText(getApplicationContext(),records.get(position).getTitle(),Toast.LENGTH_SHORT).show();
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

        // Voice Command On/Off
        voiceCommandToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setIndeterminate(true);
                    ActivityCompat.requestPermissions
                            (WalletActivity.this,
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

        activityDrawerBtn.setBackgroundResource(R.drawable.icon_activity_wallet);
        activityTitle.setText(R.string.wallet_txt);

        Picasso.get().load(HomeActivity.getActiveUser().getPhoto()).into(profilePictureImageView);
        profileEmailTextView.setText(HomeActivity.getActiveUser().getEmail());

        userNavigationView.getMenu().findItem(R.id.user_wallet_option).setCheckable(true);
        userNavigationView.getMenu().findItem(R.id.user_wallet_option).setChecked(true);

        // Initialize PieChart
        SQLiteDatabaseHelper sqLiteDatabaseHelper = new SQLiteDatabaseHelper(this);
        double ratio = sqLiteDatabaseHelper.getWalletFeedbackRatio("weekly");
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

    void handleDatabase(){
        SQLiteDatabaseHelper sqLiteDatabaseHelper = new SQLiteDatabaseHelper(this);
        SQLiteDatabase sqLiteDatabase = sqLiteDatabaseHelper.getReadableDatabase();

        loadData(sqLiteDatabaseHelper);
    }

    void loadData(SQLiteDatabaseHelper sqLiteDatabaseHelper){
        records.clear();
        records = sqLiteDatabaseHelper.loadWalletItems();
        walletRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recordAdapter = new RecordAdapter(WalletActivity.this, records);
        walletRecyclerView.setAdapter(recordAdapter);
        recordAdapter.notifyDataSetChanged();
    }

    void handleDeleteAction(int position){
        SQLiteDatabaseHelper sqLiteDatabaseHelper = new SQLiteDatabaseHelper(this);
        SQLiteDatabase sqLiteDatabase = sqLiteDatabaseHelper.getReadableDatabase();

        sqLiteDatabaseHelper.deleteRecord(
                records.get(position).getYear(), records.get(position).getMonth(), records.get(position).getDay(),
                records.get(position).getTitle(),
                records.get(position).getType());

        loadData(sqLiteDatabaseHelper);

        // Refresh PieChart
        double ratio = sqLiteDatabaseHelper.getWalletFeedbackRatio("weekly");
        setPieChart(ratio);
        pressedWeekly();

        sqLiteDatabase.close();
    }

    void handleEditAction(int position){
        SQLiteDatabaseHelper sqLiteDatabaseHelper = new SQLiteDatabaseHelper(this);
        SQLiteDatabase sqLiteDatabase = sqLiteDatabaseHelper.getReadableDatabase();

        Record record = sqLiteDatabaseHelper.findRecord(
                records.get(position).getYear(),records.get(position).getMonth(),records.get(position).getDay(),
                records.get(position).getTitle(),
                records.get(position).getType()
        );

        Intent intent = new Intent(getApplicationContext(), UpdateRecordActivity.class);

        intent.putExtra("wallet_title", record.getTitle());
        intent.putExtra("wallet_description", record.getDescription());

        intent.putExtra("wallet_year", record.getYear());
        intent.putExtra("wallet_month", record.getMonth());
        intent.putExtra("wallet_day", record.getDay());

        intent.putExtra("wallet_type", record.getType().toString());
        intent.putExtra("wallet_amount", record.getAmount());

        intent.putExtra("wallet_isDeleted", record.getIsDeleted().toString());
        intent.putExtra("wallet_isIgnored", record.getIsIgnored().toString());

        intent.putExtra("wallet_syncState", record.getSyncState());

        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        walletRecyclerView.addOnItemTouchListener(touchListener);
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
                        onClick(weekly);
                    }

                    else if(drawerLayout.isDrawerOpen(userNavigationView)){
                        drawerLayout.closeDrawer(userNavigationView);
                        onClick(weekly);
                    }
                }
            }.start();
        }
        else if(view == fab){
            Intent intent = new Intent(getApplicationContext(), AddRecordActivity.class);
            startActivity(intent);
        }

        // Activity Navigation Drawer
        else if(view == daily){
            SQLiteDatabaseHelper sqLiteDatabaseHelper = new SQLiteDatabaseHelper(this);
            double ratio = sqLiteDatabaseHelper.getWalletFeedbackRatio("daily");
            setPieChart(ratio);
            pressedDaily();
        } else if(view == weekly){
            SQLiteDatabaseHelper sqLiteDatabaseHelper = new SQLiteDatabaseHelper(this);
            double ratio = sqLiteDatabaseHelper.getWalletFeedbackRatio("weekly");
            setPieChart(ratio);
            pressedWeekly();
        } else if(view == monthly){
            SQLiteDatabaseHelper sqLiteDatabaseHelper = new SQLiteDatabaseHelper(this);
            double ratio = sqLiteDatabaseHelper.getWalletFeedbackRatio("monthly");
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
            if(!isConnectedToInternet())
                Snackbar.make(drawerLayout, "Can't Sign Out Without Internet Access!", Snackbar.LENGTH_SHORT).show();
            else
                signOut();
        }

        else if (id == R.id.wallet_bin_option) {
            Intent intent = new Intent(getApplicationContext(), WalletBinActivity.class);
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
                    Toast.makeText(WalletActivity.this, "Permission Denied!", Toast
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
            Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
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

//        else if(matches.get(0).equals("scroll up")){
//            disableVoiceCommand();
//            scroll(-1);
//        } else if(matches.get(0).equals("scroll down")){
//            disableVoiceCommand();
//            scroll(1);
//        }

        else if(matches.get(0).equals("turn off voice command")){
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

        add.deleteAlarmFromFirebase();

        FirebaseDataAdd addAlarm = new FirebaseDataAdd(FirebaseFirestore.getInstance(),uid);
        addAlarm.addAlarmInFireBase(sqLiteDatabaseHelper.syncAlarmItems());
        sqLiteDatabaseHelper.updateSyncTime(uid);
    }

    public void signOut() {
        sync();
        initializeGoogleVariable();
        mAuth.signOut();

        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
                        SQLiteDatabaseHelper sqLiteDatabaseHelper = new SQLiteDatabaseHelper(getApplicationContext());
                        SQLiteDatabase sqLiteDatabase = sqLiteDatabaseHelper.getWritableDatabase();

                        sqLiteDatabaseHelper.refreshDatabase(sqLiteDatabase);

                        Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
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
