package com.example.jarvis.Todo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
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
import com.example.jarvis.Home.HomeActivity;
import com.example.jarvis.Journal.JournalActivity;
import com.example.jarvis.Profile.ProfileActivity;
import com.example.jarvis.R;
import com.example.jarvis.Reminder.ReminderActivity;
import com.example.jarvis.SQLite.SQLiteDatabaseHelper;
import com.example.jarvis.Settings.SettingsActivity;
import com.example.jarvis.Util.RecyclerTouchListener;
import com.example.jarvis.Wallet.WalletActivity;
import com.example.jarvis.WelcomeScreen.WelcomeActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class TodoActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener, RecognitionListener, View.OnTouchListener {

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

    /** FAB */
    private FloatingActionButton addTodoBtn;


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);

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
        drawerLayout = (DrawerLayout) findViewById(R.id.todo_drawer_layout);
        toolbar = (Toolbar) findViewById(R.id.todo_toolbar);
        userDrawerBtn = (Button) findViewById(R.id.user_drawer_btn);
        activityDrawerBtn = (Button) findViewById(R.id.activity_drawer_btn);
        addTodoBtn = (FloatingActionButton) findViewById(R.id.todo_add_todo_btn);
        activityTitle = (TextView) findViewById(R.id.activity_title);
        userNavigationView = (NavigationView) findViewById(R.id.user_navigation_view);
        activityNavigationView = (NavigationView) findViewById(R.id.todo_navigation_view);
        todoRecyclerView = (RecyclerView) findViewById(R.id.todo_recycler_view);

        progressBar = (ProgressBar) findViewById(R.id.todo_progress_bar);
        voiceCommandToggleButton = (ToggleButton) findViewById(R.id.todo_voice_command_toggle_btn);
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

        // Navigation Views
        userNavigationView.setNavigationItemSelectedListener(this);
        activityNavigationView.setNavigationItemSelectedListener(this);

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
        activityDrawerBtn.setBackgroundResource(R.drawable.icon_activity_todo);
        activityTitle.setText(R.string.todo_txt);

        userNavigationView.getMenu().findItem(R.id.user_todo_option).setCheckable(true);
        userNavigationView.getMenu().findItem(R.id.user_todo_option).setChecked(true);

        progressBar.setVisibility(View.INVISIBLE);
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

        sqLiteDatabaseHelper.deleteTodo(sqLiteDatabaseHelper.getUserId(HomeActivity.getCurrentUser()),  tasks.get(position).getYear(),tasks.get(position).getMonth(),tasks.get(position).getDay(), tasks.get(position).getTitle());

        loadData(sqLiteDatabaseHelper);
    }

    void handleEditAction(int position){
        showToast("Edit Action");

        SQLiteDatabaseHelper sqLiteDatabaseHelper = new SQLiteDatabaseHelper(this);
        SQLiteDatabase sqLiteDatabase = sqLiteDatabaseHelper.getReadableDatabase();

        Task todoItem = sqLiteDatabaseHelper.findTodo(sqLiteDatabaseHelper.getUserId(HomeActivity.getCurrentUser()), tasks.get(position).getYear(),tasks.get(position).getMonth(),tasks.get(position).getDay(),tasks.get(position).getTitle());

        Intent intent = new Intent(getApplicationContext(), UpdateTaskActivity.class);
        intent.putExtra("todo_title", todoItem.getTitle());
        intent.putExtra("todo_description", todoItem.getDescription());
        intent.putExtra("todo_year", todoItem.getYear());
        intent.putExtra("todo_month", todoItem.getMonth());
        intent.putExtra("todo_day", todoItem.getDay());
        intent.putExtra("todo_hour", todoItem.getHour());
        intent.putExtra("todo_minute", todoItem.getMinute());
        intent.putExtra("todo_reminderState", todoItem.getReminderState().toString());
        intent.putExtra("todo_isCompleted", todoItem.getIsCompleted().toString());
        intent.putExtra("todo_isDeleted", todoItem.getIsDeleted().toString());
        intent.putExtra("todo_isIgnored", todoItem.getIsIgnored().toString());
        intent.putExtra("todo_updateTimestamp", todoItem.getUpdateTimestamp());

        startActivity(intent);
    }

    void handleCheckAction(int position){
        // Change Checkbox State & Show Toast
        if(tasks.get(position).getIsCompleted()==0) {
            tasks.get(position).setIsCompleted(1);
            showToast("Marked as Completed");
        }
        else {
            tasks.get(position).setIsCompleted(0);
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
                    }

                    else if(drawerLayout.isDrawerOpen(userNavigationView)){
                        drawerLayout.closeDrawer(userNavigationView);
                    }
                }
            }.start();
        }
        else if(view == addTodoBtn){
            Intent intent = new Intent(getApplicationContext(), AddTaskActivity.class);
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
        }  else if (id == R.id.user_home_option) {
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
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
            startActivity(intent);
            finish();
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

        mTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = mTTS.setLanguage(Locale.ENGLISH);

                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Language not supported");
                    } else {

                    }
                } else {
                    Log.e("TTS", "Initialization failed");
                }
            }
        });
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

        if(matches.get(0).equals("go to profile")){
            Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
            intent.putExtra("voice_command", "true");
            startActivity(intent);
        } else if(matches.get(0).equals("sync data")){
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

        else if(matches.get(0).equals("show activity options")){
            activityDrawerBtn.callOnClick();
            restartVoiceCommand();
        } else if(matches.get(0).equals("hide activity options")){
            activityDrawerBtn.callOnClick();
            restartVoiceCommand();
        } else if(matches.get(0).equals("show user options")){
            userDrawerBtn.callOnClick();
            restartVoiceCommand();
        } else if(matches.get(0).equals("hide user options")){
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
}
