package com.example.jarvis;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private Button signUpBtn;
    private Button signUpWithGoogleBtn;

    private EditText emailEditTxt;
    private EditText passEditTxt;
    private EditText confirmPassEditTxt;

    //here I added
    MyDatabaseHelper myDatabaseHelper;
    UserDetails userDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        loadXmlElements();
        setListeners();

        //here I added
        myDatabaseHelper = new MyDatabaseHelper(this);
        userDetails = new UserDetails();
        SQLiteDatabase sqLiteDatabase = myDatabaseHelper.getWritableDatabase();
    }

    void loadXmlElements(){
        signUpBtn = (Button) findViewById(R.id.sign_up_btn);
        signUpWithGoogleBtn = (Button) findViewById(R.id.sign_up_with_google_btn);

        emailEditTxt = (EditText) findViewById(R.id.sign_up_email_edit_txt);
        passEditTxt = (EditText) findViewById(R.id.sign_up_pass_edit_txt);
        confirmPassEditTxt = (EditText) findViewById(R.id.sign_up_confirm_pass_edit_txt);
    }

    void setListeners(){
        signUpBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        //here I added
        String email = emailEditTxt.getText().toString();
        String password = passEditTxt.getText().toString();
        String confirmPassword = confirmPassEditTxt.getText().toString();

        userDetails.setEmail(email);
        userDetails.setPassword(password);
        userDetails.setConfirmPassword(confirmPassword);

        long rowId = myDatabaseHelper.insertData(userDetails);

        //

        if(view == signUpBtn){
            //here I added

            if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(confirmPassword)) {
                if(rowId > 0){
                    Toast.makeText(getApplicationContext(), "Row "+ rowId +" is Successfully inserted",  Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    Toast.makeText(getApplicationContext(), "Password didn't match!!!",  Toast.LENGTH_LONG).show();
                }
            }

            else{
                Toast.makeText(getApplicationContext(), "Text Fields shouldn't be empty",  Toast.LENGTH_LONG).show();

            }



            //
        }
        else if(view == signUpWithGoogleBtn){
            // Here you go
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SignUpActivity.this, WelcomeActivity.class);
        startActivity(intent);
        finish();
    }
}
