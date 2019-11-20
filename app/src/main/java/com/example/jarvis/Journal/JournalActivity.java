package com.example.jarvis.Journal;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
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
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
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
import com.example.jarvis.R;
import com.example.jarvis.Reminder.ReminderActivity;
import com.example.jarvis.SQLite.SQLiteDatabaseHelper;
import com.example.jarvis.Settings.SettingsActivity;
import com.example.jarvis.Todo.TodoActivity;
import com.example.jarvis.Util.NetworkReceiver;
import com.example.jarvis.Util.RecyclerTouchListener;
import com.example.jarvis.Wallet.WalletActivity;
import com.example.jarvis.WelcomeScreen.WelcomeActivity;
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

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import jp.wasabeef.richeditor.RichEditor;

public class JournalActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener, RecognitionListener,View.OnTouchListener  {
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

    private Button userDrawerBtn;
    private Button activityDrawerBtn;

    private ImageView profilePictureImageView;
    private TextView profileEmailTextView;

    /** FAB */
    private FloatingActionButton addJournal;

    /** Voice Command Variables */
    private static final int REQUEST_RECORD_PERMISSION = 100;
    private ProgressBar progressBar;
    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;
    private String LOG_TAG = "JournalActivity";
    private ToggleButton voiceCommandToggleButton;

    private TextToSpeech mTTS;

    private boolean isVcOn;


    /** For Editors */
    private RichEditor mEditor;
    private TextView mPreview;

    /** RecyclerView Variables */
    RecyclerView journalRecyclerView;
    ArrayList<Journal> journals;
    JournalAdapter journalAdapter;
    RecyclerTouchListener touchListener;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal);
        setUI();
        handleDatabase();
    }

    void setUI(){
        findXmlElements();
        setToolbar();
        setListeners();
        initializeUI();
        setVoiceCommandFeature();
        isVoiceCommandOn();
    }

    public void findXmlElements(){
        // Drawer
        drawerLayout = (DrawerLayout) findViewById(R.id.journal_drawer_layout);

        // Toolbar
        toolbar = (Toolbar) findViewById(R.id.journal_toolbar);
        userDrawerBtn = (Button) findViewById(R.id.user_drawer_btn);
        activityDrawerBtn = (Button) findViewById(R.id.activity_drawer_btn);
        activityTitle = (TextView) findViewById(R.id.activity_title);

        //Main
        addJournal = (FloatingActionButton) findViewById(R.id.journal_add_journal);
        userNavigationView = (NavigationView) findViewById(R.id.user_navigation_view);
        journalRecyclerView = (RecyclerView) findViewById(R.id.journal_recycler_view);

        profilePictureImageView = (ImageView) userNavigationView.getHeaderView(0).findViewById(R.id.user_profile_picture);
        profileEmailTextView = (TextView) userNavigationView.getHeaderView(0).findViewById(R.id.user_profile_email);
      
        //Actitvity Navigation Drawer
        activityNavigationView = (NavigationView) findViewById(R.id.journal_navigation_view);
        progressBar = (ProgressBar) findViewById(R.id.journal_progress_bar);
        voiceCommandToggleButton = (ToggleButton) findViewById(R.id.journal_voice_command_toggle_btn);

        journals = new ArrayList<Journal>();
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
        addJournal.setOnClickListener(this);

        //RecyclerView
        journalRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Navigation Views
        userNavigationView.setNavigationItemSelectedListener(this);
        activityNavigationView.setNavigationItemSelectedListener(this);


        // Swipe Options
        touchListener = new RecyclerTouchListener(this,journalRecyclerView);
        touchListener
                .setClickable(new RecyclerTouchListener.OnRowClickListener() {
                    @Override
                    public void onRowClicked(int position) {
                        /**
                         * Trigers when any
                         * RecyclerButton is clicked
                         * In this case
                         * The journal will be
                         * showed in reading mode
                         */
                        handleShowAction(position);
                    }

                    @Override
                    public void onIndependentViewClicked(int independentViewID, int position) {

                    }
                })
                .setSwipeOptionViews(R.id.journal_item_delete_rl,R.id.journal_item_edit_rl)
                .setSwipeable(R.id.journal_item_fg, R.id.journal_item_bg_end, new RecyclerTouchListener.OnSwipeOptionsClickListener() {
                    @Override
                    public void onSwipeOptionClicked(int viewID, int position) {
                        switch (viewID){
                            case R.id.journal_item_delete_rl:
                                // Very much Important is that
                                // As Journal is important so
                                // Show a confirmation window
                                handleDeleteAction(position);
                                break;
                            case R.id.journal_item_edit_rl:
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
                            (JournalActivity.this,
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

        activityDrawerBtn.setBackgroundResource(R.drawable.icon_activity_journal);
        activityTitle.setText(R.string.journal_txt);

        Picasso.get().load(HomeActivity.getActiveUser().getPhoto()).into(profilePictureImageView);
        profileEmailTextView.setText(HomeActivity.getActiveUser().getEmail());

        userNavigationView.getMenu().findItem(R.id.user_journal_option).setCheckable(true);
        userNavigationView.getMenu().findItem(R.id.user_journal_option).setChecked(true);

        progressBar.setVisibility(View.INVISIBLE);

        // Load Journals from database
        journalRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        journalAdapter = new JournalAdapter(JournalActivity.this, journals);
        journalRecyclerView.setAdapter(journalAdapter);
        journalAdapter.notifyDataSetChanged();
    }



   public void handleShowAction(int position){
        // This method will pass the activity to Show JOurnal
        Intent intent = new Intent(JournalActivity.this, ShowJournalActivity.class);
        intent.putExtra("status", "show_this_journal");
        Journal sendJournal = journals.get(position);
        sendJournalToActivity(sendJournal, intent);
        startActivity(intent);

   }

   public void handleEditAction(int position){

       Intent intent = new Intent(JournalActivity.this, AddJournalActivity.class);
       intent.putExtra("status", "old_journal_from_journal_activity");
       Journal sendJournal = journals.get(position);
       sendJournalToAddActivity(sendJournal, intent);
       startActivity(intent);
   }

   public void sendJournalToActivity(Journal j, Intent i){

        i.putExtra("ja_show_title", j.getTitle());
        i.putExtra("ja_show_description", j.getDescription());
       i.putExtra("ja_show_year", j.getYear());
       i.putExtra("ja_show_month", j.getMonth());
       i.putExtra("ja_show_day", j.getDay());
       i.putExtra("ja_show_hour", j.getHour());
       i.putExtra("ja_show_minute", j.getMinute());
       i.putExtra("ja_show_fileLink", j.getFileLink());
       i.putExtra("ja_show_imageLink", j.getImageLink());


   }


    public void sendJournalToAddActivity(Journal j, Intent i){

        i.putExtra("ja_edit_title", j.getTitle());
        i.putExtra("ja_edit_description", j.getDescription());
        i.putExtra("ja_edit_year", j.getYear());
        i.putExtra("ja_edit_month", j.getMonth());
        i.putExtra("ja_edit_day", j.getDay());
        i.putExtra("ja_edit_hour", j.getHour());
        i.putExtra("ja_edit_minute", j.getMinute());
        i.putExtra("ja_edit_fileLink", j.getFileLink());
        i.putExtra("ja_edit_imageLink", j.getImageLink());


    }

    /** Database Add **/
    private void handleDatabase() {
        SQLiteDatabaseHelper sqLiteDatabaseHelper = new SQLiteDatabaseHelper(this);
        SQLiteDatabase sqLiteDatabase = sqLiteDatabaseHelper.getReadableDatabase();

        loadData(sqLiteDatabaseHelper);
    }

    private void loadData(SQLiteDatabaseHelper sqLiteDatabaseHelper) {
        journals.clear();
        journals = sqLiteDatabaseHelper.loadAllJournalItems();

        journalRecyclerView.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL)

        );

        journalAdapter = new JournalAdapter(JournalActivity.this, journals);
        journalRecyclerView.setAdapter(journalAdapter);
        journalAdapter.notifyDataSetChanged();
    }



    private void handleDeleteAction(int position){

        AlertDialog.Builder alertbuilder = new AlertDialog.Builder(JournalActivity.this);
        alertbuilder.setMessage("Do you want to delete the journal ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SQLiteDatabaseHelper sqLiteDatabaseHelper = new SQLiteDatabaseHelper(JournalActivity.this);
                        SQLiteDatabase sqLiteDatabase = sqLiteDatabaseHelper.getReadableDatabase();
                        new File(journals.get(position).getFileLink()).deleteOnExit();
                        sqLiteDatabaseHelper.deleteJournal(journals.get(position).getFileLink());
                        loadData(sqLiteDatabaseHelper);
                    }
                })
                .setNegativeButton("No", null);
        AlertDialog alert = alertbuilder.create();
        alert.show();

    }



    public void showToast(String message){
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(JournalActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        journalRecyclerView.addOnItemTouchListener(touchListener);
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
        else if(view == addJournal){
            Intent intent = new Intent(JournalActivity.this, AddJournalActivity.class);
            intent.putExtra("status", "new_journal_from_journal_activity");
            startActivity(intent);
        }
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
                    Toast.makeText(JournalActivity.this, "Permission Denied!", Toast
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

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
    }
}

