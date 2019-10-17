package com.example.jarvis.Wallet;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.example.jarvis.Home.HomeActivity;
import com.example.jarvis.R;
import com.example.jarvis.SQLite.SQLiteDatabaseHelper;
import com.example.jarvis.Util.CustomSpinnerAdapter;
import com.example.jarvis.Util.CustomSpinnerItem;
import com.example.jarvis.Util.DatePickerFragment;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class UpdateRecordActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener, View.OnClickListener, CompoundButton.OnCheckedChangeListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private Spinner customSpinner;

    private Button cancelBtn;
    private Button updateBtn;

    private EditText titleEditText;
    private EditText descriptionEditText;
    private EditText dateEditText;
    private EditText amountEditText;

    private String description, title, date, amount;
    private Integer type = 1, userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_record);

        settingUpXmlElements();
    }

    public void settingUpXmlElements(){
        customSpinner = (Spinner) findViewById(R.id.update_record_custom_spinner);

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

        updateBtn = (Button) findViewById(R.id.update_record_update_btn);
        cancelBtn = (Button) findViewById(R.id.update_record_cancel_btn);

        updateBtn.setOnClickListener((View.OnClickListener) this);
        cancelBtn.setOnClickListener((View.OnClickListener) this);

        titleEditText = (EditText) findViewById(R.id.update_record_title_editText);
        descriptionEditText = (EditText) findViewById(R.id.update_record_description_editText);
        dateEditText = (EditText) findViewById(R.id.update_record_date_editText);


        amountEditText = (EditText) findViewById(R.id.add_record_amount_editText);
        amountEditText.setText("0.00");

        if(getIntent().getExtras() != null) {
            userId = Integer.parseInt(Objects.requireNonNull(getIntent().getExtras().getString("user_id")));
            title = getIntent().getExtras().getString("wallet_title");
            description = getIntent().getExtras().getString("wallet_description");
            date = getIntent().getExtras().getString("wallet_date");
            type = Integer.parseInt(Objects.requireNonNull(getIntent().getExtras().getString("wallet_type")));
            amount = getIntent().getExtras().getString("wallet_amount");

            titleEditText.setText(title);
            descriptionEditText.setText(description);
            dateEditText.setText(date);
            amountEditText.setText(amount);

            customSpinner.setSelection(type-1);
        }

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
        if(item.getSpinnerText().equals("Expense"))
            type = 1;
        else if(item.getSpinnerText().equals("Earning"))
            type = 2;

        showToast(type.toString());
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
        if(view == updateBtn){
            description = descriptionEditText.getText().toString();
            title = titleEditText.getText().toString();

            date = dateEditText.getText().toString();

            SQLiteDatabaseHelper sqLiteDatabaseHelper = new SQLiteDatabaseHelper(this);
            SQLiteDatabase sqLiteDatabase = sqLiteDatabaseHelper.getWritableDatabase();

            String currentUser = HomeActivity.getCurrentUser();
            userId = sqLiteDatabaseHelper.getUserId(currentUser);
            amount = amountEditText.getText().toString();

            Record record = new Record(userId, title, description, date, type, amount);

            sqLiteDatabaseHelper.insertRecord(record);
            showToast("Updated");
            onBackPressed();
        }
        else if(view == cancelBtn){
            onBackPressed();
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
