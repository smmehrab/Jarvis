package com.example.jarvis;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

    // Variable for Local Database
    MyDatabaseHelper myDatabaseHelper;
    WalletDetails walletDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_record);

        settingUpXmlElements();
        handleLocalDatabase();
    }

    /*****functions for local database*****/
    void handleLocalDatabase(){
        myDatabaseHelper = new MyDatabaseHelper(this);
        walletDetails = new WalletDetails();
        SQLiteDatabase sqLiteDatabase = myDatabaseHelper.getWritableDatabase();
    }

    void handleAddingInSQLite(){
        String title = titleEditText.getText().toString();
        String description = descriptionEditText.getText().toString();
        String date = dateEditText.getText().toString();

        walletDetails.setTitle(title);
        walletDetails.setDesc(description);
        walletDetails.setDate(date);

        long rowId = myDatabaseHelper.insertDataWallet(walletDetails);
        if(rowId > 0){
            Toast.makeText(getApplicationContext(), "Successfully added!",  Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getApplicationContext(), "Addition failed!",  Toast.LENGTH_LONG).show();
        }

    }
    /*******/


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

        /****set selected spinner text into wallet details****/
        String expenseType = item.getSpinnerText();
        walletDetails.setExpenseType(expenseType);
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
            handleAddingInSQLite();
            //showToast("Added");

            /****Just Checking****/
            Cursor cursor = myDatabaseHelper.displayAllDataWallet(); //result set
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
                stringBuffer.append("Expense Type: " + cursor.getString(4) + "\n\n");

            }
            showData("ResultSet for Wallet", stringBuffer.toString());
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

    /****Just Checking****/
    public void showData(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(true);
        builder.show();
    }
}
