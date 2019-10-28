package com.example.jarvis.Reminder;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.example.jarvis.R;
import com.example.jarvis.SQLite.SQLiteDatabaseHelper;
import com.example.jarvis.Todo.TodoActivity;
import com.example.jarvis.Util.DatePickerFragment;
import com.example.jarvis.Util.TimePickerFragment;

import java.text.DateFormat;
import java.util.Calendar;

public class AddReminderActivity extends AppCompatActivity implements TimePicker.OnTimeChangedListener, View.OnClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{

    private Button cancelBtn;
    private Button addBtn;
    private Button alarmBtn;
    private Button eventBtn;

    private EditText titleEditText;
    private EditText subtitleEditText;
    private EditText dateEditText;
    private EditText timeEditText;

    private LinearLayout titleLinearLayout;
    private LinearLayout descriptionLinearLayout;
    private LinearLayout dateLinearLayout;

    private TimePicker timePicker;

    private boolean alarm = true;

    /***Changed****/

    /***Task Variables***/
    private String title;
    private String description;

    private String day, month, year;
    private String hour, minute;
    private Integer reminderState;
    private Integer isDeleted;
    private Integer isIgnored;

    private String updateTimestamp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);

        setUI();
    }

    void setUI(){
        findXmlElements();
        setListeners();
        initializeUI();
    }

    public void initializeUI(){
        /** Initializing variables */
        title = null;
        description = null;
        hour = null;
        minute = null;
        reminderState = 0;

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
    }


    void findXmlElements() {
        addBtn = (Button) findViewById(R.id.add_reminder_add_btn);
        cancelBtn = (Button) findViewById(R.id.add_reminder_cancel_btn);
        alarmBtn = (Button) findViewById(R.id.add_reminder_alarm_btn);
        eventBtn = (Button) findViewById(R.id.add_reminder_event_btn);


        titleEditText = (EditText) findViewById(R.id.add_reminder_title_editText);
        subtitleEditText = (EditText) findViewById(R.id.add_reminder_description_editText);
        dateEditText = (EditText) findViewById(R.id.add_reminder_date_editText);

        timePicker = (TimePicker) findViewById(R.id.add_reminder_time_picker);

        titleLinearLayout = (LinearLayout) findViewById(R.id.add_reminder_title_linear_layout);
        descriptionLinearLayout = (LinearLayout) findViewById(R.id.add_reminder_description_linear_layout);
        dateLinearLayout = (LinearLayout) findViewById(R.id.add_reminder_date_linear_layout);
    }

    public void setListeners(){

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

        addBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
        alarmBtn.setOnClickListener(this);
        eventBtn.setOnClickListener(this);

        titleLinearLayout.setVisibility(View.GONE);
        descriptionLinearLayout.setVisibility(View.GONE);
        dateLinearLayout.setVisibility(View.GONE);

    }

    public void showToast(String message){
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    /********/

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


    /*********/


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

    @Override
    public void onClick(View view) {

        /**Database helper**/
        SQLiteDatabaseHelper sqLiteDatabaseHelper = new SQLiteDatabaseHelper(this);
        SQLiteDatabase sqLiteDatabase = sqLiteDatabaseHelper.getWritableDatabase();

        if(view == addBtn){
            showToast("Added");
        }
        else if(view == cancelBtn){
            onBackPressed();
        }
        else if(view == dateEditText){
            DialogFragment datePicker = new DatePickerFragment();
            datePicker.show(getSupportFragmentManager(),"date picker");
        }
        else if(view == alarmBtn){
            if(!alarm){
                alarmBtn.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                alarmBtn.setTextColor(getResources().getColor(R.color.colorPrimary));

                eventBtn.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                eventBtn.setTextColor(getResources().getColor(R.color.colorWhite));
            }
        }
        else if(view == eventBtn){
            if(alarm){
                eventBtn.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                eventBtn.setTextColor(getResources().getColor(R.color.colorPrimary));

                alarmBtn.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                alarmBtn.setTextColor(getResources().getColor(R.color.colorWhite));
            }
        }
    }

    @Override
    public void onTimeChanged(TimePicker timePicker, int i, int i1) {

    }
}
