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

public class AddTaskActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{

    // Buttons
    private Button addBtn;
    private Button cancelBtn;

    // Switch
    private Switch remindMeSwitch;

    // EditTexts
    private EditText titleEditText;
    private EditText descriptionEditText;
    private EditText dateEditText;
    private EditText timeEditText;

    // Task Variables
    private Integer userId;
    private String title;
    private String description;

    private String day, month, year;
    private String hour, minute;
    private Integer reminderState=0;


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
        disableButton(addBtn);

        Calendar calendar = Calendar.getInstance();
        year = Integer.toString(calendar.get(Calendar.YEAR));
        month = Integer.toString(calendar.get(Calendar.MONTH));
        day = Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));

        String currentDate = DateFormat.getDateInstance().format(calendar.getTime());
        dateEditText.setText(currentDate);

        timeEditText.setText("Set Time");
        hour = null;
        minute = null;
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
            SQLiteDatabaseHelper sqLiteDatabaseHelper = new SQLiteDatabaseHelper(this);
            SQLiteDatabase sqLiteDatabase = sqLiteDatabaseHelper.getWritableDatabase();

            String currentUser = HomeActivity.getCurrentUser();
            userId = sqLiteDatabaseHelper.getUserId(currentUser);

            title = titleEditText.getText().toString();
            description = descriptionEditText.getText().toString();

            showToast(hour);
            Task task = new Task(userId, title, description, year, month, day, hour, minute, reminderState);

            sqLiteDatabaseHelper.insertTodo(task);
            //showToast("Added");
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
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, y);
        calendar.set(Calendar.MONTH, m);
        calendar.set(Calendar.DAY_OF_MONTH, d);

        year = Integer.toString(calendar.get(Calendar.YEAR));
        month = Integer.toString(calendar.get(Calendar.MONTH));
        day = Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));

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
