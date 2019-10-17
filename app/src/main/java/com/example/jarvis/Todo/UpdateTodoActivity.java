package com.example.jarvis.Todo;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.example.jarvis.Home.HomeActivity;
import com.example.jarvis.R;
import com.example.jarvis.SQLite.SQLiteDatabaseHelper;
import com.example.jarvis.Util.DatePickerFragment;
import com.example.jarvis.Util.TimePickerFragment;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Objects;

public class UpdateTodoActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{
    private ImageButton remindMeBtn;
    private Button cancelBtn;
    private Button updateBtn;

    private Switch remindMeSwitch;

    private EditText titleEditText;
    private EditText descriptionEditText;
    private EditText dateEditText;
    private EditText timeEditText;

    private LinearLayout dateLinearLayout;
    private LinearLayout timeLinearLayout;
    private LinearLayout timeGapLinearLayout;

    private String oldDescription, oldTitle;
    private Integer oldReminderState=0;
    private String oldYear, oldMonth, oldDay;
    private String oldHour=null, oldMinute=null, oldAmPm = " AM";

    private String description, title;
    private Integer reminderState=0, userId;
    private String year, month, day;
    private String hour=null, minute=null, amPm = " AM";

    private String date, time="Set Time";

    private Boolean isUpdated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_todo);

        settingUpXmlElements();
    }

    void settingUpXmlElements(){
        updateBtn = (Button) findViewById(R.id.update_todo_add_btn);
        cancelBtn = (Button) findViewById(R.id.update_todo_cancel_btn);
        remindMeBtn = (ImageButton) findViewById(R.id.update_todo_remind_me_btn);

        updateBtn.setOnClickListener(this);
        disableButton(updateBtn);

        cancelBtn.setOnClickListener(this);
        remindMeBtn.setOnClickListener(this);

        remindMeSwitch = (Switch) findViewById(R.id.update_todo_remind_me_switch);
        remindMeSwitch.setOnCheckedChangeListener(this);

        titleEditText = (EditText) findViewById(R.id.update_todo_title_editText);
        descriptionEditText = (EditText) findViewById(R.id.update_todo_description_editText);
        dateEditText = (EditText) findViewById(R.id.update_todo_date_editText);
        timeEditText = (EditText) findViewById(R.id.update_todo_time_editText);

        if(getIntent().getExtras() != null) {
            userId = Integer.parseInt(Objects.requireNonNull(getIntent().getExtras().getString("user_id")));
            oldTitle = getIntent().getExtras().getString("todo_title");
            title = oldTitle;

            oldDescription = getIntent().getExtras().getString("todo_description");
            description = oldDescription;

            oldYear = getIntent().getExtras().getString("todo_year");
            year = oldYear;

            oldMonth = getIntent().getExtras().getString("todo_month");
            month = oldMonth;

            oldDay = getIntent().getExtras().getString("todo_day");
            day = oldDay;

            oldReminderState = Integer.parseInt(Objects.requireNonNull(getIntent().getExtras().getString("todo_reminderState")));
            reminderState = oldReminderState;

            String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
            date = day + " " + months[Integer.parseInt(oldMonth)] + ", " + year;

            oldHour = getIntent().getExtras().getString("todo_hour");
            oldMinute = getIntent().getExtras().getString("todo_minute");

            if(oldHour!=null && oldMinute!=null){
                if(Integer.parseInt(oldHour)>=12){
                    oldAmPm = " PM";
                }
            }

            titleEditText.setText(oldTitle);
            descriptionEditText.setText(oldDescription);
            dateEditText.setText(date);

            if(oldHour!=null && oldMinute!=null && amPm!=null)
                time = Integer.toString(Integer.parseInt(oldHour) % 12) + ":" + oldMinute + amPm;
            else
                time = "Set Time";
            timeEditText.setText(time);

            if(oldReminderState==1)
                remindMeSwitch.setChecked(true);
            else
                remindMeSwitch.setChecked(false);
        }


        titleEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
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

        dateLinearLayout = (LinearLayout) findViewById(R.id.update_todo_date_linear_layout);
        timeLinearLayout = (LinearLayout) findViewById(R.id.update_todo_time_linear_layout);
        timeGapLinearLayout = (LinearLayout) findViewById(R.id.update_todo_time_gap_linear_layout);

    }

    public void showToast(String message){
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
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

    @Override
    public void onClick(View view) {
        if(view == updateBtn){
            SQLiteDatabaseHelper sqLiteDatabaseHelper = new SQLiteDatabaseHelper(this);
            SQLiteDatabase sqLiteDatabase = sqLiteDatabaseHelper.getWritableDatabase();

            String currentUser = HomeActivity.getCurrentUser();
            userId = sqLiteDatabaseHelper.getUserId(currentUser);

            title = titleEditText.getText().toString();
            description = descriptionEditText.getText().toString();

            if(hour==null || minute == null){
                hour = oldHour;
                minute = oldMinute;
            }

            TodoDetails todoDetails = new TodoDetails(userId, title, description, year, month, day, hour, minute, reminderState);
            sqLiteDatabaseHelper.updateTodo(todoDetails, oldYear, oldMonth, oldDay, oldTitle);
            showToast("Updated");
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
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, y);
        calendar.set(Calendar.MONTH, m);
        calendar.set(Calendar.DAY_OF_MONTH, d);


            year = Integer.toString(calendar.get(Calendar.YEAR));
            month = Integer.toString(calendar.get(Calendar.MONTH));
            day = Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));

            if(year.equals(oldYear) && month.equals(oldMonth) && day.equals(oldDay))
                disableButton(updateBtn);
            else
                enableButton(updateBtn);

        String currentDate = DateFormat.getDateInstance().format(calendar.getTime());
        dateEditText.setText(currentDate);
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int h, int m) {
        String amPm;
        if (h >= 12) {
            amPm = " PM";
        } else {
            amPm = " AM";
        }


            hour = Integer.toString(h);
            minute = Integer.toString(m);

        if(h>=12)
            h = h - 12;

        if(Integer.toString(h).equals(oldHour) && Integer.toString(m).equals(oldMinute))
            disableButton(updateBtn);
        else
            enableButton(updateBtn);

        timeEditText.setText(String.format("%02d:%02d", h, m) + amPm);
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
