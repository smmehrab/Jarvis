package com.example.jarvis;

import androidx.annotation.NonNull;
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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    //  Variables for SignUpWithGoogle
    private static final int RC_SIGN_IN = 1;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth.AuthStateListener mAuthListener;
    GoogleSignInOptions googleSignInOptions;

    // Buttons
    private Button signUpBtn;
    private Button signUpWithGoogleBtn;

    // EditTexts
    private EditText emailEditTxt;
    private EditText passEditTxt;
    private EditText confirmPassEditTxt;

    // Variable for Local Database
    MyDatabaseHelper myDatabaseHelper;
    UserDetails userDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        settingUpXmlElements();

        handleLocalDatabase();
        handleRemoteDatabase();
    }

    void settingUpXmlElements(){
        signUpBtn = (Button) findViewById(R.id.sign_up_btn);
        signUpWithGoogleBtn = (Button) findViewById(R.id.sign_up_with_google_btn);

        emailEditTxt = (EditText) findViewById(R.id.sign_up_email_edit_txt);
        passEditTxt = (EditText) findViewById(R.id.sign_up_pass_edit_txt);
        confirmPassEditTxt = (EditText) findViewById(R.id.sign_up_confirm_pass_edit_txt);

        setListeners();
    }

    void setListeners(){
        signUpBtn.setOnClickListener(this);
        signUpWithGoogleBtn.setOnClickListener(this);
    }

    void handleLocalDatabase(){
        myDatabaseHelper = new MyDatabaseHelper(this);
        userDetails = new UserDetails();
        SQLiteDatabase sqLiteDatabase = myDatabaseHelper.getWritableDatabase();
    }

    void handleRemoteDatabase(){
        initializeGoogleVariable();
    }

    void initializeGoogleVariable() {
        mAuth = FirebaseAuth.getInstance();

        // Configure Google sign in
        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
    }

    void handleSignUpWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
        //startActivityForResult();
    }

    void handleSignUp(){
        String email = emailEditTxt.getText().toString();
        String password = passEditTxt.getText().toString();
        String confirmPassword = confirmPassEditTxt.getText().toString();

        userDetails.setEmail(email);
        userDetails.setPassword(password);
        userDetails.setConfirmPassword(confirmPassword);

        long rowId = myDatabaseHelper.insertData(userDetails);

        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(confirmPassword)) {
            if(rowId > 0){
                Toast.makeText(getApplicationContext(), "Successfully Signed Up!",  Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
                startActivity(intent);
            }
            else{
                Toast.makeText(getApplicationContext(), "Password Didn't Match!",  Toast.LENGTH_LONG).show();
            }
        }

        else{
            Toast.makeText(getApplicationContext(), "Invalid Email Or Password. Try Again!",  Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View view) {
        if(view == signUpBtn){
            handleSignUp();
        }

        else if(view == signUpWithGoogleBtn){
            handleSignUpWithGoogle();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SignUpActivity.this, WelcomeActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                // Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        // Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());


        // Ekhan theke ekta progressbar diye dis vala hoibo
        ///showProgressDialog();

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            // Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            startActivity(new Intent(SignUpActivity.this, HomeActivity.class));
                            finish();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            // Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Snackbar.make(findViewById(R.id.sign_in_activity), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            // updateUI(null);
                        }


                        // Eikhane progress bar tare shesh korbi Ok?

                        // ...
                    }
                });
    }
}
