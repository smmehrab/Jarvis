package com.example.jarvis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {

    //  Variables for Remote Database
    private static final int RC_SIGN_IN = 1;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth.AuthStateListener mAuthListener;
    GoogleSignInOptions googleSignInOptions;

    // Buttons
    private Button signInBtn;
    private Button signInWithGoogleBtn;
    private Button forgotPassBtn;

    // EditTexts
    private EditText emailEditTxt;
    private EditText passEditTxt;

    // Variable for Local Database
    MyDatabaseHelper myDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        settingUpXmlElements();

        handleLocalDatabase();
        handleRemoteDatabase();
    }

    void settingUpXmlElements(){
        signInBtn = (Button) findViewById(R.id.sign_in_btn);
        signInWithGoogleBtn = (Button) findViewById(R.id.sign_in_with_google_btn);
        forgotPassBtn = (Button) findViewById(R.id.forgot_pass_btn);

        emailEditTxt = (EditText) findViewById(R.id.sign_in_email_edit_txt);
        passEditTxt = (EditText) findViewById(R.id.sign_in_pass_edit_txt);

        setListeners();
    }

    void setListeners(){
        signInBtn.setOnClickListener(this);
        signInWithGoogleBtn.setOnClickListener(this);
        forgotPassBtn.setOnClickListener(this);
    }

    void handleLocalDatabase(){
        myDatabaseHelper = new MyDatabaseHelper(this);
        SQLiteDatabase sqLiteDatabase = myDatabaseHelper.getWritableDatabase();
    }

    void handleRemoteDatabase(){
        initializeGoogleVariable();
    }

    private void initializeGoogleVariable() {
        mAuth = FirebaseAuth.getInstance();

        // Configure Google sign in
        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
    }

    void handleSignIn(){
        String email = emailEditTxt.getText().toString();
        String password = passEditTxt.getText().toString();

        Boolean result = myDatabaseHelper.findUser(email, password);

        if(result == true){
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(intent);
        }
        else{
            Toast.makeText(getApplicationContext(), "Invalid Email Or Password. Try Again!", Toast.LENGTH_SHORT).show();
        }
    }

    void handleSignInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
        //startActivityForResult();
    }

    void handleForgotPass(){
        Intent intent = new Intent(SignInActivity.this, ResetPassActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        if(view == signInBtn){
            handleSignIn();
        }

        else if(view == signInWithGoogleBtn){
            handleSignInWithGoogle();
        }

        else if(view == forgotPassBtn){
            handleForgotPass();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SignInActivity.this, WelcomeActivity.class);
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
                            startActivity(new Intent(SignInActivity.this, HomeActivity.class));
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
