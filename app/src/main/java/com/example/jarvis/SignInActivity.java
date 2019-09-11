package com.example.jarvis;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {

    private Button signInBtn;
    private Button signInWithGoogleBtn;
    private Button forgotPassBtn;

    private EditText emailEditTxt;
    private EditText passEditTxt;

    MyDatabaseHelper myDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        loadXmlElements();
        setListeners();

        myDatabaseHelper = new MyDatabaseHelper(this);
        SQLiteDatabase sqLiteDatabase = myDatabaseHelper.getWritableDatabase();
    }

    void loadXmlElements(){
        signInBtn = (Button) findViewById(R.id.sign_in_btn);
        signInWithGoogleBtn = (Button) findViewById(R.id.sign_in_with_google_btn);
        forgotPassBtn = (Button) findViewById(R.id.forgot_pass_btn);

        emailEditTxt = (EditText) findViewById(R.id.sign_in_email_edit_txt);
        passEditTxt = (EditText) findViewById(R.id.sign_in_pass_edit_txt);
    }

    void setListeners(){
        signInBtn.setOnClickListener(this);
        signInWithGoogleBtn.setOnClickListener(this);
        forgotPassBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        String email = emailEditTxt.getText().toString();
        String password = passEditTxt.getText().toString();

        if(view == signInBtn){
            //here I added

            Boolean result = myDatabaseHelper.findUser(email, password);

            if(result == true){
                Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
            else{
                Toast.makeText(getApplicationContext(), "Email and Password didn't match", Toast.LENGTH_LONG).show();
            }


        }

        else if(view == signInWithGoogleBtn){
            // Here you go
        }

        else if(view == forgotPassBtn){
            Intent intent = new Intent(SignInActivity.this, ForgotPassActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SignInActivity.this, WelcomeActivity.class);
        startActivity(intent);
        finish();
    }
}
