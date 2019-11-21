package com.example.jarvis.Todo;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.example.jarvis.R;
import com.example.jarvis.SQLite.SQLiteDatabaseHelper;
import com.example.jarvis.Util.DatePickerFragment;
import com.example.jarvis.Util.TimePickerFragment;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Objects;

public class UpdateTaskActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{

    /** Buttons */
    private Button updateBtn;
    private Button cancelBtn;

    /** Switch */
    private Switch remindMeSwitch;

    /** EditTexts */
    private EditText titleEditText;
    private EditText descriptionEditText;
    private EditText dateEditText;
    private EditText timeEditText;

    /** Task Variables (Old)*/
    private String oldTitle;
    private String oldDescription;

    private String oldYear, oldMonth, oldDay;
    private String oldHour=null, oldMinute=null,  oldAmPm = " AM";
    private Integer oldReminderState=0;
    private Integer oldIsCompleted=0;
    private Integer oldIsDeleted=0;
    private Integer oldIsIgnored=0;
    private String oldUpdateTimestamp;

    /** Task Variables (Current) */
    private Integer userId;
    private String title;
    private String description;

    private String year, month, day;
    private String hour=null, minute=null, amPm = " AM";
    private Integer reminderState;
    private Integer isCompleted;
    private Integer isDeleted=0;
    private Integer isIgnored=0;
    private String updateTimestamp;

    private String date, time="Set Time";

    private AlarmManager alarmManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_todo);

        setUI();
    }

    void setUI(){
        findXmlElements();
        setListeners();
        getDataFromTodoActivity();
        initializeUI();
    }

    public void findXmlElements(){
        updateBtn = (Button) findViewById(R.id.update_todo_add_btn);
        cancelBtn = (Button) findViewById(R.id.update_todo_cancel_btn);
        remindMeSwitch = (Switch) findViewById(R.id.update_todo_remind_me_switch);

        titleEditText = (EditText) findViewById(R.id.update_todo_title_editText);
        descriptionEditText = (EditText) findViewById(R.id.update_todo_description_editText);
        dateEditText = (EditText) findViewById(R.id.update_todo_date_editText);
        timeEditText = (EditText) findViewById(R.id.update_todo_time_editText);

        alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

    }

    public void setListeners(){
        updateBtn.setOnClickListener(this);
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
                /** Checking if title has been updated or not */
                if(!editable.toString().equals(oldTitle) && editable.toString().length()!=0){
                    enableButton(updateBtn);
                    title = editable.toString();
                }
                else
                    disableButton(updateBtn);
            }
        });

        descriptionEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                /** Checking if description has been updated or not */
                if(!editable.toString().equals(oldDescription)){
                    enableButton(updateBtn);
                    description = editable.toString();
                }
                else
                    disableButton(updateBtn);
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

    public void getDataFromTodoActivity(){
        if(getIntent().getExtras() != null) {
            // Getting Old Data from Todo Activity
            oldTitle = getIntent().getExtras().getString("todo_title");
            oldDescription = getIntent().getExtras().getString("todo_description");

            oldYear = getIntent().getExtras().getString("todo_year");
            oldMonth = getIntent().getExtras().getString("todo_month");
            oldDay = getIntent().getExtras().getString("todo_day");

            oldHour = getIntent().getExtras().getString("todo_hour");
            oldMinute = getIntent().getExtras().getString("todo_minute");

            oldReminderState = Integer.parseInt(Objects.requireNonNull(getIntent().getExtras().getString("todo_reminderState")));
            oldIsCompleted = Integer.parseInt(Objects.requireNonNull(getIntent().getExtras().getString("todo_isCompleted")));

            oldIsDeleted = Integer.parseInt(Objects.requireNonNull(getIntent().getExtras().getString("todo_isDeleted")));
            oldIsIgnored = Integer.parseInt(Objects.requireNonNull(getIntent().getExtras().getString("todo_isIgnored")));

            updateTimestamp = getIntent().getExtras().getString("todo_updateTimestamp");
        }
    }

    public void initializeUI(){
        // Initializing Current Data as Old Data
        title = oldTitle;
        description = oldDescription;

        year = oldYear;
        month = oldMonth;
        day = oldDay;

        reminderState = oldReminderState;
        isCompleted = oldIsCompleted;

        // Formatting Date to set on EditText
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        date = day + " " + months[Integer.parseInt(month)] + ", " + year;


        // Formatting Time to set on EditText
        if(oldHour!=null && oldMinute!=null){
            if(Integer.parseInt(oldHour)>=12){
                oldAmPm = " PM";
            }
        }

        if(oldHour!=null && oldMinute!=null && amPm!=null)
            time = Integer.toString(Integer.parseInt(oldHour) % 12) + ":" + oldMinute + amPm;
        else
            time = "Set Time";

        // Initialize UI with the old(received) data
        titleEditText.setText(title);
        descriptionEditText.setText(description);
        dateEditText.setText(date);
        timeEditText.setText(time);

        if(reminderState==1)
            remindMeSwitch.setChecked(true);
        else
            remindMeSwitch.setChecked(false);

        disableButton(updateBtn);
    }

    @Override
    public void onClick(View view) {
        if(view == updateBtn){
            SQLiteDatabaseHelper sqLiteDatabaseHelper = new SQLiteDatabaseHelper(this);
            SQLiteDatabase sqLiteDatabase = sqLiteDatabaseHelper.getWritableDatabase();

            title = titleEditText.getText().toString();
            description = descriptionEditText.getText().toString();

            // Checking if time hasn't been updated
            if(hour==null || minute == null){
                hour = oldHour;
                minute = oldMinute;
            }

            if(!(title.equals(oldTitle) && year.equals(oldYear) && month.equals(oldMonth) && day.equals(oldDay))){
                // Primary Key Field Violated
                oldIsIgnored = 1;

                // Ignore Existing Task
                Task task = new Task(oldTitle, oldDescription, oldYear, oldMonth, oldDay, oldHour, oldMinute, oldReminderState, oldIsCompleted, oldIsDeleted, oldIsIgnored, 1);
                sqLiteDatabaseHelper.updateTodo(task, oldYear, oldMonth, oldDay, oldTitle);

                // Add New Task
                task = new Task(title, description, year, month, day, hour, minute, reminderState, isCompleted, isDeleted, isIgnored, 0);
                sqLiteDatabaseHelper.insertTodo(task);
            } else {
                // Primary Key Field Not Violated
                // Update Existing Task
                Task task = new Task(title, description, year, month, day, hour, minute, reminderState, isCompleted, isDeleted, isIgnored, 0);
                sqLiteDatabaseHelper.updateTodo(task, oldYear, oldMonth, oldDay, oldTitle);
            }

            //Update remind me
            Integer oldNotificationId = (Integer.parseInt(oldYear)+Integer.parseInt(oldMonth)+Integer.parseInt(oldDay)+Integer.parseInt(oldHour)+Integer.parseInt(oldMinute));
            showToast("old Noti id: " + oldNotificationId.toString());

            Calendar c = Calendar.getInstance();
            c.set(Calendar.YEAR, Integer.parseInt(year));
            c.set(Calendar.MONTH, Integer.parseInt(month));
            c.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day));
            c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
            c.set(Calendar.MINUTE, Integer.parseInt(minute));
            c.set(Calendar.SECOND, 0);
            Integer todoNotificationID;
            todoNotificationID = (Integer.parseInt(year)+Integer.parseInt(month)+Integer.parseInt(day)+Integer.parseInt(hour)+Integer.parseInt(minute));
            showToast("updated noti id: "+todoNotificationID.toString());
            if(reminderState == 1 && isCompleted == 0) {
                Intent intent = new Intent(this, todoAlertReceiver.class);
                intent.putExtra("todoNotification", todoNotificationID);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, todoNotificationID, intent, 0);
                alarmManager.setExact(AlarmManager.RTC, c.getTimeInMillis(), pendingIntent);
            }


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
            else {
                reminderState = 1;
            }
        }
        else{
            reminderState = 0;
        }
        if(!reminderState.equals(oldReminderState))
            enableButton(updateBtn);
        else
            disableButton(updateBtn);
    }

    @Override
    public void onDateSet(DatePicker datePicker, int y, int m, int d) {
        // Formatting Selected Date so that we can set the date on EditText
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, y);
        calendar.set(Calendar.MONTH, m);
        calendar.set(Calendar.DAY_OF_MONTH, d);
        String currentDate = DateFormat.getDateInstance().format(calendar.getTime());

        // Setting Selected Date to the EditText
        dateEditText.setText(currentDate);

        // Assigning Selected Date to the following variables
        // so that we can use these variables to create task object
        year = Integer.toString(calendar.get(Calendar.YEAR));
        month = Integer.toString(calendar.get(Calendar.MONTH));
        day = Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));

        // Checking if date has been updated or not
        if(year.equals(oldYear) && month.equals(oldMonth) && day.equals(oldDay))
            disableButton(updateBtn);
        else
            enableButton(updateBtn);
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int h, int m) {
        // Assigning Selected Time to the following variables
        // so that we can use these variables to create task object
        hour = Integer.toString(h);
        minute = Integer.toString(m);

        // Formatting Selected Time so that we can set the time on EditText
        String amPm = " AM";
        if (h >= 12) {
            amPm = " PM";
            h = h - 12;
        }
        String selectedTime = String.format("%02d:%02d", h, m) + amPm;

        // Setting Selected Time to the EditText
        timeEditText.setText(selectedTime);

        // Checking if time has been updated or not
        if(Integer.toString(h).equals(oldHour) && Integer.toString(m).equals(oldMinute))
            disableButton(updateBtn);
        else
            enableButton(updateBtn);
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
