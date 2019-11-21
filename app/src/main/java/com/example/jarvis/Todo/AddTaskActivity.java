package com.example.jarvis.Todo;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.example.jarvis.R;
import com.example.jarvis.Reminder.AlertReceiver;
import com.example.jarvis.SQLite.SQLiteDatabaseHelper;
import com.example.jarvis.Util.DatePickerFragment;
import com.example.jarvis.Util.TimePickerFragment;

import java.math.BigInteger;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;

public class AddTaskActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{

    /** Buttons */
    private Button addBtn;
    private Button cancelBtn;

    /** Switch */
    private Switch remindMeSwitch;

    /** EditTexts */
    private EditText titleEditText;
    private EditText descriptionEditText;
    private EditText dateEditText;
    private EditText timeEditText;

    /** Task Variables */
    private Integer uid;
    private String title;
    private String description;

    private String day, month, year;
    private String hour, minute;
    private Integer reminderState;
    private Integer isCompleted;
    private Integer isDeleted;
    private Integer isIgnored;

    private String updateTimestamp;

    private AlarmManager alarmManager;

    //Constant values in millisecond
    private static final long milMinute = 60000L;
    private static final long milHour = 3600000L;
    private static final long milDay = 86400000L;
    private static final long milWeek = 604800000L;
    private static final long milMonth = 2592000000L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_todo);

        setUI();
    }

    void setUI(){
        findXmlElements();
        setListeners();
        initializeUI();
    }

    public void findXmlElements(){
        addBtn = (Button) findViewById(R.id.add_todo_add_btn);
        cancelBtn = (Button) findViewById(R.id.add_todo_cancel_btn);

        remindMeSwitch = (Switch) findViewById(R.id.add_todo_remind_me_switch);

        titleEditText = (EditText) findViewById(R.id.add_todo_title_editText);
        descriptionEditText = (EditText) findViewById(R.id.add_todo_description_editText);
        dateEditText = (EditText) findViewById(R.id.add_todo_date_editText);
        timeEditText = (EditText) findViewById(R.id.add_todo_time_editText);

        alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

    }

    public void setListeners(){
        addBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
        remindMeSwitch.setOnCheckedChangeListener(this);

        titleEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().length()!=0)
                    enableButton(addBtn);
                else
                    disableButton(addBtn);
            }
        });

        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(),"date picker");
            }
        });

        timeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(),"time picker");
            }
        });
    }

    public void initializeUI(){
        /** Initializing variables */
        title = null;
        description = null;
        hour = null;
        minute = null;
        reminderState = 0;

        isCompleted = 0;
        isDeleted = 0;
        isIgnored = 0;

        Long tsLong = System.currentTimeMillis()/1000;
        String ts = tsLong.toString();

        updateTimestamp = ts;

        /** Formatting Present Date so that we can set the date on EditText */
        Calendar calendar = Calendar.getInstance();
        year = Integer.toString(calendar.get(Calendar.YEAR));
        month = Integer.toString(calendar.get(Calendar.MONTH));
        day = Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));
        String selectedDate = DateFormat.getDateInstance().format(calendar.getTime());

        /** Initializing UI */
        dateEditText.setText(selectedDate);
        timeEditText.setText("Set Time");
        disableButton(addBtn);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View view) {
        if(view == addBtn){
            SQLiteDatabaseHelper sqLiteDatabaseHelper = new SQLiteDatabaseHelper(this);
            SQLiteDatabase sqLiteDatabase = sqLiteDatabaseHelper.getWritableDatabase();

            // Getting Current Title & Description
            title = titleEditText.getText().toString();
            description = descriptionEditText.getText().toString();

            // Getting Current Timestamp
            Long tsLong = System.currentTimeMillis()/1000;
            String ts = tsLong.toString();
            updateTimestamp = ts;

            Task task = new Task(title, description, year, month, day, hour, minute, reminderState, isCompleted, isDeleted, isIgnored, updateTimestamp);
            sqLiteDatabaseHelper.insertTodo(task);

            //Set remind me
            Calendar c = Calendar.getInstance();

            c.set(Calendar.YEAR, Integer.parseInt(year));
            c.set(Calendar.MONTH, Integer.parseInt(month));
            c.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day));
            c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
            c.set(Calendar.MINUTE, Integer.parseInt(minute));
            c.set(Calendar.SECOND, 0);
            Integer todoNotificationID;
            todoNotificationID = (Integer.parseInt(year)+Integer.parseInt(month)+Integer.parseInt(day)+Integer.parseInt(hour)+Integer.parseInt(minute));
        //    showToast(todoNotificationID.toString());
            if(reminderState == 1) {
                Intent intent = new Intent(this, todoAlertReceiver.class);
                intent.putExtra("todoNotification", todoNotificationID);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, todoNotificationID, intent, 0);
                alarmManager.setExact(AlarmManager.RTC, c.getTimeInMillis(), pendingIntent);
            }
            /*sqLiteDatabase = sqLiteDatabaseHelper.getReadableDatabase();
            Cursor cursor = sqLiteDatabaseHelper.getAllTodo();
            int rowIndex=0;
            String mYear, mMonth, mDay, mHour, mMinute;
            int mReminderState, mIsComplete;
            if(cursor.getCount() == 0){
                showToast("No Data Found");
            }
            else{
                while(cursor.moveToNext()){
                    rowIndex++;
                    mYear = cursor.getString(2);
                    mMonth = cursor.getString(3);
                    mDay = cursor.getString(4);
                    mHour = cursor.getString(5);
                    mMinute = cursor.getString(6);
                    mReminderState = cursor.getInt(7);
                    mIsComplete = cursor.getInt(8);

                    if(mIsComplete == 0) {
                        if(mReminderState == 1){
                            c.set(Calendar.MONTH, Integer.parseInt(mMonth));
                            c.set(Calendar.YEAR, Integer.parseInt(mYear));
                            c.set(Calendar.DAY_OF_MONTH, Integer.parseInt(mDay));
                            c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(mHour));
                            c.set(Calendar.MINUTE, Integer.parseInt(mMinute));
                            c.set(Calendar.SECOND, 0);

                            Intent intent = new Intent(this, todoAlertReceiver.class);
                            intent.putExtra("todoNotification", rowIndex);
                            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, rowIndex, intent, 0);

                            alarmManager.setExact(AlarmManager.RTC, c.getTimeInMillis(), pendingIntent);
                        }
                    }
                }
            }*/

            onBackPressed();
        }
        else if(view == cancelBtn){
            onBackPressed();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        if(isChecked){
            if(timeEditText.getText().toString().equals("Set Time")) {
                showToast("Set Time First!");
                timeEditText.callOnClick();
                compoundButton.setChecked(false);
            }
            else
                reminderState = 1;
        }
        else{
            reminderState = 0;
        }
    }

    @Override
    public void onDateSet(DatePicker datePicker, int y, int m, int d) {
        /** Formatting Selected Date so that we can set the date on EditText */
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, y);
        calendar.set(Calendar.MONTH, m);
        calendar.set(Calendar.DAY_OF_MONTH, d);
        String selectedDate = DateFormat.getDateInstance().format(calendar.getTime());

        /** Setting Selected Date to the EditText */
        dateEditText.setText(selectedDate);

        /** Assigning Selected Date to the following variables
         * so that we can use these variables to create task object */
        year = Integer.toString(calendar.get(Calendar.YEAR));
        month = Integer.toString(calendar.get(Calendar.MONTH));
        day = Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int h, int m) {
        /** Assigning Selected Time to the following variables
         * so that we can use these variables to create task object */
        hour = Integer.toString(h);
        minute = Integer.toString(m);

        /** Formatting Selected Time so that we can set the time on EditText */
        String amPm = " AM";
        if (h >= 12) {
            amPm = " PM";
            h = h - 12;
        }
        String selectedTime = String.format("%02d:%02d", h, m) + amPm;

        /** Setting Selected Time to the EditText */
        timeEditText.setText(selectedTime);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), TodoActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void showToast(String message){
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    void disableButton(Button button){
        button.setEnabled(false);
        button.setAlpha(.5f);
        button.setClickable(false);
    }

    void enableButton(Button button){
        button.setEnabled(true);
        button.setAlpha(1f);
        button.setClickable(true);
    }
}
