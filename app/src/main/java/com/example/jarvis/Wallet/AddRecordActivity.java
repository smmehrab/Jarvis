package com.example.jarvis.Wallet;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.example.jarvis.R;
import com.example.jarvis.SQLite.SQLiteDatabaseHelper;
import com.example.jarvis.Util.CustomSpinnerAdapter;
import com.example.jarvis.Util.CustomSpinnerItem;
import com.example.jarvis.Util.DatePickerFragment;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class AddRecordActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener, View.OnClickListener, DatePickerDialog.OnDateSetListener{

    /** Spinner */
    private Spinner customSpinner;

    /** Buttons */
    private Button addBtn;
    private Button cancelBtn;

    /** EditTexts */
    private EditText titleEditText;
    private EditText descriptionEditText;
    private EditText dateEditText;
    private EditText amountEditText;

    /** Record Variables */
    private Integer userId;
    private String title;
    private String description;

    private String year, month, day;

    private Integer type = 0;
    // Type 0 - Expense - Red
    // Type 1 - Earning - Green

    private String amount;

    private Integer isDeleted = 0;
    private Integer isIgnored = 0;

    private String updateTimestamp=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_record);

        setUI();
    }

    public void setUI(){
        findXmlElements();
        setSpinner();
        setListeners();
        initializeUI();
    }

    public void findXmlElements(){
        customSpinner = (Spinner) findViewById(R.id.add_record_custom_spinner);
        addBtn = (Button) findViewById(R.id.add_record_add_btn);
        cancelBtn = (Button) findViewById(R.id.add_record_cancel_btn);

        titleEditText = (EditText) findViewById(R.id.add_record_title_editText);
        descriptionEditText = (EditText) findViewById(R.id.add_record_description_editText);
        dateEditText = (EditText) findViewById(R.id.add_record_date_editText);
        amountEditText = (EditText) findViewById(R.id.add_record_amount_editText);
    }

    public void setSpinner(){
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
    }

    public void setListeners(){
        addBtn.setOnClickListener((View.OnClickListener) this);
        cancelBtn.setOnClickListener((View.OnClickListener) this);

        titleEditText.setOnClickListener(this);
        amountEditText.setOnClickListener(this);

        dateEditText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(),"date picker");
            }
        });

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

        amountEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String s = editable.toString();

                try{
                    double d = Double.parseDouble(s);
                    if(d!=0)
                        enableButton(addBtn);
                    else
                        disableButton(addBtn);
                }catch (Exception e){

                }
            }
        });
    }

    public void initializeUI(){
        // Formatting Present Date so that we can set the date on EditText
        Calendar calendar = Calendar.getInstance();
        year = Integer.toString(calendar.get(Calendar.YEAR));
        month = Integer.toString(calendar.get(Calendar.MONTH));
        day = Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));
        String currentDate = DateFormat.getDateInstance().format(calendar.getTime());

        // Setting Present Date to the EditText
        dateEditText.setText(currentDate);

        // Initializing the amount EditText
        amountEditText.setText("0");

        // Disabling addBtn as default
        disableButton(addBtn);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        // Handling Custom Spinner Item Selection Event
        CustomSpinnerItem item = (CustomSpinnerItem) adapterView.getSelectedItem();
        if(item.getSpinnerText().equals("Expense"))
            type = 0;
        else if(item.getSpinnerText().equals("Earning"))
            type = 1;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onClick(View view) {
        if(view == addBtn){
            SQLiteDatabaseHelper sqLiteDatabaseHelper = new SQLiteDatabaseHelper(this);
            SQLiteDatabase sqLiteDatabase = sqLiteDatabaseHelper.getWritableDatabase();

            title = titleEditText.getText().toString();
            description = descriptionEditText.getText().toString();
            amount = amountEditText.getText().toString();

            isDeleted = 0;
            isIgnored = 0;

            Long tsLong = System.currentTimeMillis()/1000;
            String ts = tsLong.toString();

            updateTimestamp = ts;

            Record record = new Record(title, description, year, month, day, type, amount);

            sqLiteDatabaseHelper.insertRecord(record);
            onBackPressed();
        }
        else if(view == cancelBtn){
            onBackPressed();
        }
    }

    @Override
    public void onDateSet(DatePicker datePicker, int y, int m, int d) {
        // Formatting Selected Date so that we can set the date on EditText
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, y);
        calendar.set(Calendar.MONTH, m);
        calendar.set(Calendar.DAY_OF_MONTH, d);
        String selectedDate = DateFormat.getDateInstance().format(calendar.getTime());

        // Setting Selected Date to the EditText
        dateEditText.setText(selectedDate);

        // Assigning Selected Date to the following variables
        // so that we can use these variables to create task object
        year = Integer.toString(calendar.get(Calendar.YEAR));
        month = Integer.toString(calendar.get(Calendar.MONTH));
        day = Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));
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
