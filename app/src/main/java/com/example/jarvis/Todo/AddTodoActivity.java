package com.example.jarvis.Todo;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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

public class AddTodoActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{
    private ImageButton remindMeBtn;
    private ImageButton copyToClipboardBtn;
    private Button cancelBtn;
    private Button addBtn;

    private Switch remindMeSwitch;

    private EditText titleEditText;
    private EditText descriptionEditText;
    private EditText dateEditText;
    private EditText timeEditText;

    private LinearLayout dateLinearLayout;
    private LinearLayout timeLinearLayout;
    private LinearLayout timeGapLinearLayout;

    private String description, title;
    private Integer reminderState=0, userId;
    private String date;
    private String time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_todo);

        settingUpXmlElements();
    }

    void settingUpXmlElements(){
        addBtn = (Button) findViewById(R.id.add_todo_add_btn);
        cancelBtn = (Button) findViewById(R.id.add_todo_cancel_btn);
        remindMeBtn = (ImageButton) findViewById(R.id.add_todo_remind_me_btn);

        addBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
        remindMeBtn.setOnClickListener(this);

        remindMeSwitch = (Switch) findViewById(R.id.add_todo_remind_me_switch);
        remindMeSwitch.setOnCheckedChangeListener(this);

        titleEditText = (EditText) findViewById(R.id.add_todo_title_editText);
        descriptionEditText = (EditText) findViewById(R.id.add_todo_description_editText);
        dateEditText = (EditText) findViewById(R.id.add_todo_date_editText);
        timeEditText = (EditText) findViewById(R.id.add_todo_time_editText);

        Calendar calendar = Calendar.getInstance();
        String currentDate = DateFormat.getDateInstance().format(calendar.getTime());
        dateEditText.setText(currentDate);

        timeEditText.setText("Set Time");

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

        dateLinearLayout = (LinearLayout) findViewById(R.id.add_todo_date_linear_layout);
        timeLinearLayout = (LinearLayout) findViewById(R.id.add_todo_time_linear_layout);
        timeGapLinearLayout = (LinearLayout) findViewById(R.id.add_todo_time_gap_linear_layout);

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
        if(view == addBtn){
            description = descriptionEditText.getText().toString();
            title = titleEditText.getText().toString();

            date = dateEditText.getText().toString();
            time = timeEditText.getText().toString();

            SQLiteDatabaseHelper sqLiteDatabaseHelper = new SQLiteDatabaseHelper(this);
            SQLiteDatabase sqLiteDatabase = sqLiteDatabaseHelper.getWritableDatabase();

            String currentUser = HomeActivity.getCurrentUser();
            userId = sqLiteDatabaseHelper.getUserId(currentUser);

            TodoDetails todoDetails = new TodoDetails(description, title, reminderState, userId, date, time);

            sqLiteDatabaseHelper.insertTodo(todoDetails);
            showToast("Added");
            onBackPressed();
        }
        else if(view == cancelBtn){
            onBackPressed();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        if(isChecked){
            reminderState = 1;
            if(timeEditText.getText().toString().equals("Set Time"))
                timeEditText.callOnClick();
        }
        else{
            reminderState = 0;
        }
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);

        String currentDate = DateFormat.getDateInstance().format(calendar.getTime());
        dateEditText.setText(currentDate);
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
        String amPm;
        if (hour >= 12) {
            amPm = " PM";
        } else {
            amPm = " AM";
        }
        timeEditText.setText(String.format("%02d:%02d", hour, minute) + amPm);
    }
}
