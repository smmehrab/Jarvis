package com.example.jarvis.UserHandling;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.jarvis.Firebase.FirebaseDataRetrieve;
import com.example.jarvis.Home.HomeActivity;
import com.example.jarvis.R;
import com.example.jarvis.SQLite.SQLiteDatabaseHelper;
import com.example.jarvis.Wallet.Record;
import com.example.jarvis.WelcomeScreen.WelcomeActivity;
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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

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
    SQLiteDatabaseHelper sqLiteDatabaseHelper;

    // User user;
    // private int isSignedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        setUI();

        handleLocalDatabase();
        handleRemoteDatabase();
    }

    void setUI(){
        findXmlElements();
        setListeners();
    }

    void findXmlElements(){
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

    void handleLocalDatabase(){
        sqLiteDatabaseHelper = new SQLiteDatabaseHelper(this);
        SQLiteDatabase sqLiteDatabase = sqLiteDatabaseHelper.getWritableDatabase();
    }

    void handleRemoteDatabase(){
        initializeGoogleVariable();
    }

    private void initializeGoogleVariable() {
        mAuth = FirebaseAuth.getInstance();

        // Configuring Google Sign In
        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
    }

//    void handleSignIn() {
//        String email = emailEditTxt.getText().toString();
//        String password = passEditTxt.getText().toString();
//        //change a little
//        Boolean result;
//        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
//            result = sqLiteDatabaseHelper.findUser(email, password);
//
//            if (result) {
//                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
//                intent.putExtra("currentUser", email);
//                startActivity(intent);
//            } else {
//                Toast.makeText(getApplicationContext(), "Invalid Email Or Password. Try Again!", Toast.LENGTH_SHORT).show();
//            }
//        }
//        else
//            Toast.makeText(getApplicationContext(), "Text Field can't be empty!!!", Toast.LENGTH_SHORT).show();
//
//    }

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
//            handleSignIn();
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
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            String deviceId = findDeviceId();

                            retrieveDataFromFirebase();
                            addUserToLocalDatabase(acct, deviceId);

                            enterApp(acct, deviceId);
                        } else {
                            Snackbar.make(findViewById(R.id.sign_in_activity), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void retrieveDataFromFirebase(){
        FirebaseUser user = mAuth.getCurrentUser();

        /**  CHECK & UPDATE DEVICE ID ON FIREBASE */

        // Call Firebase Sync
        FirebaseDataRetrieve updateInstance = new FirebaseDataRetrieve(FirebaseFirestore.getInstance(),mAuth.getUid());

        ArrayList<com.example.jarvis.Todo.Task> tasks = new ArrayList<>();
        ArrayList<Record> records = new ArrayList<>();

        updateInstance.retriveTodoFromFirebase(getApplicationContext());
        updateInstance.retriveWalletFromFirebase(getApplicationContext());
    }

    public void addUserToLocalDatabase(GoogleSignInAccount acct, String deviceId){
        // Inserting that User to TABLE_USER (Local Database)
        SQLiteDatabaseHelper sqLiteDatabaseHelper = new SQLiteDatabaseHelper(getApplicationContext());
        SQLiteDatabase sqLiteDatabase = sqLiteDatabaseHelper.getWritableDatabase();

        // Getting Current Timestamp
        Long tsLong = System.currentTimeMillis()/1000;
        String ts = tsLong.toString();

        // Creating New User
        User currentUser = new User( mAuth.getUid(), acct.getEmail(), acct.getDisplayName(), Objects.requireNonNull(acct.getPhotoUrl()).toString(), deviceId, ts);

        // Inserting to the Local Database
        sqLiteDatabaseHelper.insertUser(currentUser);
    }

    public void enterApp(GoogleSignInAccount acct, String deviceId){
        // Changing Activity
        Intent intent = new Intent(SignInActivity.this, HomeActivity.class);

        // Passing New User Info to the HomeActivity
        intent.putExtra("uid", mAuth.getUid());
        intent.putExtra("email", acct.getEmail());
        intent.putExtra("name", acct.getDisplayName());
        intent.putExtra("photo", Objects.requireNonNull(acct.getPhotoUrl()).toString());
        intent.putExtra("deviceId", deviceId);

        startActivity(intent);
        finish();
    }

    public String findDeviceId(){
        String id = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        return id;
    }

    public void showToast(String message){
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

}
