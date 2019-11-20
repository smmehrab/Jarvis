package com.example.jarvis.Todo;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jarvis.About.AboutActivity;
import com.example.jarvis.Home.HomeActivity;
import com.example.jarvis.Journal.JournalActivity;
import com.example.jarvis.R;
import com.example.jarvis.Reminder.ReminderActivity;
import com.example.jarvis.SQLite.SQLiteDatabaseHelper;
import com.example.jarvis.Settings.SettingsActivity;
import com.example.jarvis.Util.RecyclerTouchListener;
import com.example.jarvis.Wallet.WalletActivity;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class TodoBinActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener, RecognitionListener, View.OnTouchListener {

    /** Toolbar */
    private Toolbar toolbar;
    private TextView activityTitle;

    private Button userDrawerBtn;
    private Button activityDrawerBtn;

    /** RecyclerView Variables */
    RecyclerView todoBinRecyclerView;
    ArrayList<Task> tasks;
    BinTaskAdapter binTaskAdapter;
    RecyclerTouchListener binTouchListener;

    /** Voice Command Variables */
    private static final int REQUEST_RECORD_PERMISSION = 100;
    private ProgressBar progressBar;
    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;
    private String LOG_TAG = "TodoBinActivity";
    private ToggleButton voiceCommandToggleButton;

    private AlarmManager alarmManager;

    private boolean isVcOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_bin);

        initializeVariables();
        setUI();
        handleDatabase();
        setVoiceCommandFeature();
        isVoiceCommandOn();

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
        // Toolbar
        toolbar = (Toolbar) findViewById(R.id.todo_bin_toolbar);
        userDrawerBtn = (Button) findViewById(R.id.user_drawer_btn);
        activityDrawerBtn = (Button) findViewById(R.id.activity_drawer_btn);
        activityTitle = (TextView) findViewById(R.id.activity_title);

        todoBinRecyclerView = (RecyclerView) findViewById(R.id.todo_bin_recycler_view);

        // Voice Command
        progressBar = (ProgressBar) findViewById(R.id.todo_bin_progress_bar);
        voiceCommandToggleButton = (ToggleButton) findViewById(R.id.todo_bin_voice_command_toggle_btn);

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
    }

    public void setToolbar(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    public void setListeners(){
        // RecyclerView
        todoBinRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Swipe Options
        binTouchListener = new RecyclerTouchListener(this,todoBinRecyclerView);
        binTouchListener
                .setClickable(new RecyclerTouchListener.OnRowClickListener() {
                    @Override
                    public void onRowClicked(int position) {
//                        touchListener.openSwipeOptions(position);

                    }

                    @Override
                    public void onIndependentViewClicked(int independentViewID, int position) {

                    }
                })
                .setSwipeOptionViews(R.id.todo_bin_item_delete_rl,R.id.todo_bin_item_restore_rl)
                .setSwipeable(R.id.todo_bin_item_fg, R.id.todo_bin_item_bg_end, new RecyclerTouchListener.OnSwipeOptionsClickListener() {
                    @Override
                    public void onSwipeOptionClicked(int viewID, int position) {
                        switch (viewID){
                            case R.id.todo_bin_item_delete_rl:
                                handleDeleteAction(position);
                                break;
                            case R.id.todo_bin_item_restore_rl:
                                handleRestoreAction(position);
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
                            (TodoBinActivity.this,
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
        userDrawerBtn.setVisibility(View.GONE);
        activityDrawerBtn.setVisibility(View.GONE);
        activityTitle.setText("Todo Bin");
        progressBar.setVisibility(View.INVISIBLE);
    }


    public void handleDatabase(){
        SQLiteDatabaseHelper sqLiteDatabaseHelper = new SQLiteDatabaseHelper(this);
        SQLiteDatabase sqLiteDatabase = sqLiteDatabaseHelper.getReadableDatabase();

        loadData(sqLiteDatabaseHelper);
    }

    public void loadData(SQLiteDatabaseHelper sqLiteDatabaseHelper){
        tasks.clear();
        tasks = sqLiteDatabaseHelper.loadTodoBinItems();

        todoBinRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        binTaskAdapter = new BinTaskAdapter(TodoBinActivity.this, tasks);
        todoBinRecyclerView.setAdapter(binTaskAdapter);
        binTaskAdapter.notifyDataSetChanged();
    }

    void handleDeleteAction(int position){
        SQLiteDatabaseHelper sqLiteDatabaseHelper = new SQLiteDatabaseHelper(this);
        SQLiteDatabase sqLiteDatabase = sqLiteDatabaseHelper.getReadableDatabase();

        sqLiteDatabaseHelper.permanentlyDeleteTodo(tasks.get(position).getYear(),tasks.get(position).getMonth(),tasks.get(position).getDay(), tasks.get(position).getTitle());

        //delete TodoNotification
        Integer todoNotificationID;
        todoNotificationID = (Integer.parseInt(tasks.get(position).getYear())+Integer.parseInt(tasks.get(position).getMonth())+Integer.parseInt(tasks.get(position).getDay())+Integer.parseInt(tasks.get(position).getHour())+Integer.parseInt(tasks.get(position).getMinute()));
        showToast("permanently deleted id: "+todoNotificationID.toString());

        Intent intent = new Intent(this, todoAlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, todoNotificationID, intent, 0);
        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();

        loadData(sqLiteDatabaseHelper);
    }

    void handleRestoreAction(int position){
        SQLiteDatabaseHelper sqLiteDatabaseHelper = new SQLiteDatabaseHelper(this);
        SQLiteDatabase sqLiteDatabase = sqLiteDatabaseHelper.getReadableDatabase();

        sqLiteDatabaseHelper.restoreTodo(tasks.get(position).getYear(),tasks.get(position).getMonth(),tasks.get(position).getDay(),tasks.get(position).getTitle());

        //Restore todo reminder

        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, Integer.parseInt(tasks.get(position).getYear()));
        c.set(Calendar.MONTH, Integer.parseInt(tasks.get(position).getMonth()));
        c.set(Calendar.DAY_OF_MONTH, Integer.parseInt(tasks.get(position).getDay()));
        c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(tasks.get(position).getHour()));
        c.set(Calendar.MINUTE, Integer.parseInt(tasks.get(position).getMinute()));
        c.set(Calendar.SECOND, 0);
        Integer restoreTodoNotificationID;
        restoreTodoNotificationID = (Integer.parseInt(tasks.get(position).getYear())+Integer.parseInt(tasks.get(position).getMonth())+ Integer.parseInt(tasks.get(position).getDay())+Integer.parseInt(tasks.get(position).getHour())+Integer.parseInt(tasks.get(position).getMinute()));
        showToast("restored noti id: "+restoreTodoNotificationID.toString());

        if(tasks.get(position).getReminderState() == 1 && tasks.get(position).getIsCompleted() == 0) {
            Intent intent = new Intent(this, todoAlertReceiver.class);
            intent.putExtra("todoNotification", restoreTodoNotificationID);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, restoreTodoNotificationID, intent, 0);
            alarmManager.setExact(AlarmManager.RTC, c.getTimeInMillis(), pendingIntent);
        }


        loadData(sqLiteDatabaseHelper);
    }


    public void showToast(String message){
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        todoBinRecyclerView.addOnItemTouchListener(binTouchListener);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(TodoBinActivity.this, TodoActivity.class);
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
                    Toast.makeText(TodoBinActivity.this, "Permission Denied!", Toast
                            .LENGTH_SHORT).show();
                }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
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

        if(matches.get(0).equals("go to home")){
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
        }


        else if(matches.get(0).equals("restore a task")){

        } else if(matches.get(0).equals("delete a task permanently")){

        }


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
        if (errorMessage.equals("Client Side Error") || errorMessage.equals("No Such Command Found")  ||  errorMessage.equals("No Command Given")) {
            restartVoiceCommand();
        } else {
            showToast(errorMessage.toUpperCase());
            disableVoiceCommand();
        }

//        showToast(errorMessage.toUpperCase());
//        restartVoiceCommand();
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

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }
}
