package com.example.jarvis.UserHandling;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.jarvis.Firebase.FirebaseDataRetrieve;
import com.example.jarvis.Home.HomeActivity;
import com.example.jarvis.R;
import com.example.jarvis.SQLite.SQLiteDatabaseHelper;
import com.example.jarvis.Util.TodoAlertReceiver;
import com.example.jarvis.Wallet.Record;
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
import java.util.Calendar;
import java.util.Objects;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {
    /** Network Variables */
    private BroadcastReceiver networkReceiver = null;

    /** Firebase Variables */
    private static final int RC_SIGN_IN = 1;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth.AuthStateListener mAuthListener;
    GoogleSignInOptions googleSignInOptions;

    /** Button */
    private Button signInWithGoogleBtn;


    /** SQLite Variable */
    SQLiteDatabaseHelper sqLiteDatabaseHelper;

    /** Others */
    private boolean doubleBackToExitPressedOnce = false;
    private AlarmManager alarmManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        initializeGoogleVariable();
        initializeSQLiteVariable();
        setUI();
    }

    @Override
    protected void onStart() {
        mAuth.addAuthStateListener(mAuthListener);
        super.onStart();
    }

    void setUI(){
        findXmlElements();
        setListeners();
        broadcastIntent();
    }

    void findXmlElements(){
        signInWithGoogleBtn = (Button) findViewById(R.id.sign_in_with_google_btn);
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
    }

    void setListeners(){
        signInWithGoogleBtn.setOnClickListener(this);
    }

    void initializeSQLiteVariable(){
        sqLiteDatabaseHelper = new SQLiteDatabaseHelper(this);
        SQLiteDatabase sqLiteDatabase = sqLiteDatabaseHelper.getWritableDatabase();
        /**may be here should call the alarmManager**/
        setAlarm();
    }

    public void setAlarm(){

        ///////////////////////////////////////////////////////////////////////
        Calendar c = Calendar.getInstance();
        SQLiteDatabase sqLiteDatabase;
        sqLiteDatabase = sqLiteDatabaseHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabaseHelper.getAllTodo();
        int rowIndex=0;
        Integer todoNotificationID;

        String mYear, mMonth, mDay, mHour, mMinute;
        int mReminderState, mIsComplete;
        if(cursor.getCount() == 0){
            showToast("No Data Found");
        }
        else{
            while(cursor.moveToNext()){
                mYear = cursor.getString(2);
                mMonth = cursor.getString(3);
                mDay = cursor.getString(4);
                mHour = cursor.getString(5);
                mMinute = cursor.getString(6);
                mReminderState = cursor.getInt(7);
                mIsComplete = cursor.getInt(8);

                if(mIsComplete == 0) {
                    if(mReminderState == 1){
                        c.set(Calendar.MONTH, Integer.parseInt(mMonth));
                        c.set(Calendar.YEAR, Integer.parseInt(mYear));
                        c.set(Calendar.DAY_OF_MONTH, Integer.parseInt(mDay));
                        c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(mHour));
                        c.set(Calendar.MINUTE, Integer.parseInt(mMinute));
                        c.set(Calendar.SECOND, 0);

                        todoNotificationID = (Integer.parseInt(mYear)+Integer.parseInt(mMonth)+Integer.parseInt(mDay)+Integer.parseInt(mHour)+Integer.parseInt(mMinute));
                //        showToast(todoNotificationID.toString());

                        long reminderTime = c.getTimeInMillis();
                        long currentTime= System.currentTimeMillis();

                        if(reminderTime > currentTime){
                            Intent intent = new Intent(this, TodoAlertReceiver.class);
                            intent.putExtra("todoNotification", todoNotificationID);
                            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, todoNotificationID, intent, 0);
                            alarmManager.setExact(AlarmManager.RTC, reminderTime, pendingIntent);
                        }
                    }
                }
            }
        }
        /////////////////////////////////////////////////////////

    }

    private void initializeGoogleVariable() {
        mAuth = FirebaseAuth.getInstance();
        checkIfAlreadySignIn();

        // Configuring Google Sign In
        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

    }

    private void checkIfAlreadySignIn() {
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() != null){
                    enterApp();
                }
            }
        };
    }

    void handleSignInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onClick(View view) {
        if(view == signInWithGoogleBtn) {
            if (!isConnectedToInternet())
                Snackbar.make(findViewById(R.id.sign_in_activity), "Can't Sign In Without Internet Access!", Snackbar.LENGTH_SHORT).show();
            else {
                handleSignInWithGoogle();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.finish();
            moveTaskToBack(true);
            return;
        }

        this.doubleBackToExitPressedOnce = true;

        showToast("Press Once Again to EXIT");

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
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
                // Google Sign In Successful | Authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {

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
                            enterApp();
                        } else {
                            Snackbar.make(findViewById(R.id.sign_in_activity), "Authentication Failed! Try Again!", Snackbar.LENGTH_SHORT).show();
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
        updateInstance.retriveAlarmFromFirebase(getApplicationContext());
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

    public void enterApp(){
        Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
        startActivity(intent);
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

    /** For Checking Network Connection */
    public void broadcastIntent() {
        registerReceiver(networkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    public boolean isConnectedToInternet(){
        ConnectivityManager cm =
                (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }
}
