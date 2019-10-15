package com.example.jarvis.Reminder;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.example.jarvis.R;
import com.example.jarvis.Todo.TodoActivity;
import com.example.jarvis.Util.DatePickerFragment;
import com.example.jarvis.Util.TimePickerFragment;

public class AddReminderActivity extends AppCompatActivity implements TimePicker.OnTimeChangedListener, View.OnClickListener{

    private Button cancelBtn;
    private Button addBtn;
    private Button alarmBtn;
    private Button eventBtn;

    private EditText titleEditText;
    private EditText descriptionEditText;
    private EditText dateEditText;
    private EditText timeEditText;

    private LinearLayout titleLinearLayout;
    private LinearLayout descriptionLinearLayout;
    private LinearLayout dateLinearLayout;

    private TimePicker timePicker;

    private boolean alarm = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);

        settingUpXmlElements();
    }

    void settingUpXmlElements(){
        addBtn = (Button) findViewById(R.id.add_reminder_add_btn);
        cancelBtn = (Button) findViewById(R.id.add_reminder_cancel_btn);
        alarmBtn = (Button) findViewById(R.id.add_reminder_alarm_btn);
        eventBtn = (Button) findViewById(R.id.add_reminder_event_btn);

        addBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
        alarmBtn.setOnClickListener(this);
        eventBtn.setOnClickListener(this);

        titleEditText = (EditText) findViewById(R.id.add_reminder_title_editText);
        descriptionEditText = (EditText) findViewById(R.id.add_reminder_description_editText);
        dateEditText = (EditText) findViewById(R.id.add_reminder_date_editText);

        timePicker = (TimePicker) findViewById(R.id.add_reminder_time_picker);

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

        titleLinearLayout = (LinearLayout) findViewById(R.id.add_reminder_title_linear_layout);
        descriptionLinearLayout = (LinearLayout) findViewById(R.id.add_reminder_description_linear_layout);
        dateLinearLayout = (LinearLayout) findViewById(R.id.add_reminder_date_linear_layout);

        titleLinearLayout.setVisibility(View.GONE);
        descriptionLinearLayout.setVisibility(View.GONE);
        dateLinearLayout.setVisibility(View.GONE);

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
