package com.example.jarvis;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class AddRecordActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener, View.OnClickListener, CompoundButton.OnCheckedChangeListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private Spinner customSpinner;

    private Button cancelBtn;
    private Button addBtn;

    private EditText titleEditText;
    private EditText descriptionEditText;
    private EditText dateEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_record);

        settingUpXmlElements();
    }

    public void settingUpXmlElements(){
        customSpinner = (Spinner) findViewById(R.id.add_record_custom_spinner);

        // Creating Spinner Item List for Spinner

        ArrayList<CustomSpinnerItem> customSpinnerList = new ArrayList<>();
        customSpinnerList.add(new CustomSpinnerItem("Expense", R.drawable.icon_color_red));
        customSpinnerList.add(new CustomSpinnerItem("Earning", R.drawable.icon_color_green));

        // Creating Adapter for Spinner

        CustomSpinnerAdapter customSpinnerAdapter = new CustomSpinnerAdapter(this, customSpinnerList);

        if(customSpinner != null){
            customSpinner.setAdapter(customSpinnerAdapter);
            customSpinner.setOnItemSelectedListener(this);
        }

        addBtn = (Button) findViewById(R.id.add_record_add_btn);
        cancelBtn = (Button) findViewById(R.id.add_record_cancel_btn);

        addBtn.setOnClickListener((View.OnClickListener) this);
        cancelBtn.setOnClickListener((View.OnClickListener) this);

        titleEditText = (EditText) findViewById(R.id.add_record_title_editText);
        descriptionEditText = (EditText) findViewById(R.id.add_record_description_editText);
        dateEditText = (EditText) findViewById(R.id.add_record_date_editText);

        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(),"date picker");
            }
        });
    }

    public void showToast(String message){
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        CustomSpinnerItem item = (CustomSpinnerItem) adapterView.getSelectedItem();
        showToast(item.getSpinnerText());
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), WalletActivity.class);
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
    public void onTimeSet(TimePicker timePicker, int i, int i1) {

    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

    }
}
