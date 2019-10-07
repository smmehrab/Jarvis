package com.example.jarvis;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import databases.firebase.FirebaseDataAdd;

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

    // Variable for Local Database
    MyDatabaseHelper myDatabaseHelper;
    TodoDetails todoDetails;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_todo);

        settingUpXmlElements();
        handleLocalDatabase();
    }

    /*******changed*****/
    void handleLocalDatabase(){
        myDatabaseHelper = new MyDatabaseHelper(this);
        todoDetails = new TodoDetails();
        SQLiteDatabase sqLiteDatabase = myDatabaseHelper.getWritableDatabase();
    }

    void handleAddingInSQLite(){
        String title = titleEditText.getText().toString();
        String description = descriptionEditText.getText().toString();
        String date = dateEditText.getText().toString();
        String time = timeEditText.getText().toString();

        todoDetails.setTitle(title);
        todoDetails.setDesc(description);
        todoDetails.setDate(date);
        todoDetails.setTime(time);

        long rowId = myDatabaseHelper.insertDataTodo(todoDetails);
        if(rowId > 0){
                Toast.makeText(getApplicationContext(), "Successfully added!",  Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(getApplicationContext(), "Addition failed!",  Toast.LENGTH_LONG).show();
            }

    }

    /*******/
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

        timeLinearLayout.setVisibility(View.GONE);
        timeGapLinearLayout.setVisibility(View.VISIBLE);
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
            handleAddingInSQLite();
            //showToast("Added");

            /****Just Checking****/
            Cursor cursor = myDatabaseHelper.displayAllDataToDo(); //result set
            if(cursor.getCount() == 0){
                //there is no data
                showData("Error","No data Found");
                return;
            }
            StringBuffer stringBuffer = new StringBuffer();
            while (cursor.moveToNext()){
                stringBuffer.append("ID: " + cursor.getString(0) + "\n");
                stringBuffer.append("Title: " + cursor.getString(1) + "\n");
                stringBuffer.append("Description: " + cursor.getString(2) + "\n");
                stringBuffer.append("Date: " + cursor.getString(3) + "\n");
                stringBuffer.append("Time: " + cursor.getString(4) + "\n\n");

            }
            showData("ResultSet", stringBuffer.toString());

            /***********/

        }
        else if(view == cancelBtn){
            onBackPressed();
        }
        else if(view == dateEditText){
            DialogFragment datePicker = new DatePickerFragment();
            datePicker.show(getSupportFragmentManager(),"date picker");
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        if(isChecked == true){
            timeLinearLayout.setVisibility(View.VISIBLE);
            timeGapLinearLayout.setVisibility(View.GONE);
        }
        else{
            timeLinearLayout.setVisibility(View.GONE);
            timeGapLinearLayout.setVisibility(View.VISIBLE);
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

    /****Just Checking****/
    public void showData(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(true);
        builder.show();
    }
}
