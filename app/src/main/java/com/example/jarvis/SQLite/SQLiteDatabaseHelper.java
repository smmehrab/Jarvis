package com.example.jarvis.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.view.Gravity;
import android.widget.Toast;

import com.example.jarvis.Todo.TodoDetails;
import com.example.jarvis.UserHandling.UserDetails;
import com.example.jarvis.Wallet.WalletDetails;

public class SQLiteDatabaseHelper extends SQLiteOpenHelper {

    // Database
    private static final String DATABASE_NAME = "database_local.db";

    // Others
    private Context context;
    private static int VERSION_NUMBER = 1;
    private static final String DROP_TABLE = "DROP TABLE IF EXISTS ";

    // TABLE USER
    private static final String TABLE_USER = "table_user";
    private static final String USER_ID = "user_id";
    private static final String USER_EMAIL = "user_email";
    private static final String USER_PASSWORD = "user_password";
    private static final String USER_CONFIRM_PASSWORD = "user_confirmPassword";

    private static final String CREATE_TABLE_USER = "CREATE TABLE " + TABLE_USER + "(" +
            USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            USER_EMAIL + " TEXT NOT NULL, " +
            USER_PASSWORD + " TEXT NOT NULL, " +
            USER_CONFIRM_PASSWORD + " TEXT NOT NULL); ";

    // TABLE TODO
    private static final String TABLE_TODO = "table_todo";
    private static final String TODO_TITLE = "todo_title";
    private static final String TODO_DESCRIPTION = "todo_description";
    private static final String TODO_DATE = "todo_date";
    private static final String TODO_REMINDER_STATE = "todo_reminderState";
    private static final String TODO_TIME = "todo_time";

    private static final String CREATE_TABLE_TODO = "CREATE TABLE " + TABLE_TODO + "(" +
            USER_ID + " INTEGER, "  +
            TODO_TITLE + " TEXT NOT NULL, " +
            TODO_DESCRIPTION + " TEXT, " +
            TODO_DATE + " TEXT NOT NULL, " +
            TODO_REMINDER_STATE + " INTEGER, " +
            TODO_TIME + " TEXT, " +
            "FOREIGN KEY(" + USER_ID + ") REFERENCES " + TABLE_USER + "(" + USER_ID + "), " +
            "PRIMARY KEY(" + TODO_TITLE + ", " + TODO_DATE +  ")); ";

    // TABLE WALLET
    private static final String TABLE_WALLET = "table_wallet";
    private static final String WALLET_TITLE = "wallet_title";
    private static final String WALLET_DESCRIPTION = "wallet_description";
    private static final String WALLET_DATE = "wallet_date";
    private static final String WALLET_TYPE = "wallet_type";

    private static final String CREATE_TABLE_WALLET = "CREATE TABLE " + TABLE_WALLET + "(" +
            USER_ID + " INTEGER, " +
            WALLET_TITLE + " TEXT NOT NULL, " +
            WALLET_DESCRIPTION + " TEXT NOT NULL, " +
            WALLET_DATE + " TEXT NOT NULL, " +
            WALLET_TYPE + " INTEGER NOT NULL, " +
            "FOREIGN KEY(" + USER_ID + ") REFERENCES " + TABLE_USER + "(" + USER_ID + "), " +
            "PRIMARY KEY(" + WALLET_TITLE + ", " + WALLET_DATE + ", " + WALLET_TYPE + ")); ";


    public SQLiteDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION_NUMBER);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        try {
            sqLiteDatabase.execSQL(CREATE_TABLE_USER);
            sqLiteDatabase.execSQL(CREATE_TABLE_TODO);
            sqLiteDatabase.execSQL(CREATE_TABLE_WALLET);
        }
        catch (Exception e){
            showToast("Exception : " + e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        try {
            sqLiteDatabase.execSQL(DROP_TABLE+TABLE_USER);
            sqLiteDatabase.execSQL(DROP_TABLE+TABLE_TODO);
            sqLiteDatabase.execSQL(DROP_TABLE+TABLE_WALLET);
            onCreate(sqLiteDatabase);
        } catch (Exception e) {
            showToast("Exception : " + e);
        }
    }

    public long insertUser(UserDetails userDetails){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        String getEmail = userDetails.getEmail();
        String getPassword = userDetails.getPassword();
        String getConfirmPassword = userDetails.getConfirmPassword();

        if(getPassword.equals(getConfirmPassword)){
            contentValues.put(USER_EMAIL, getEmail);
            contentValues.put(USER_PASSWORD, getPassword);
            contentValues.put(USER_CONFIRM_PASSWORD, getConfirmPassword);

            long rowId = sqLiteDatabase.insert(TABLE_USER, null, contentValues);
            return rowId;
        }
        else {
            return 0;
        }
    }

    public Boolean findUser(String mail, String pass){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_USER, null);
        Boolean result = false;

        if(cursor.getCount() == 0){
            showToast("No Data Found");
        }
        else{
            while (cursor.moveToNext()){
                String email = cursor.getString(1);
                String password = cursor.getString(2);

                if(email.equals(mail) && password.equals(pass)){
                    result = true;
                    break;
                }
            }
        }

        return result;
    }

    public Integer getUserId(String mail){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_USER, null);
        Integer result = null;

        if(cursor.getCount() == 0){
            showToast("No Data Found");
        }
        else{
            while (cursor.moveToNext()){
                String email = cursor.getString(1);

                if(email.equals(mail)){
                    result = Integer.parseInt(cursor.getString(0));
                    break;
                }
            }
        }
        return result;
    }


    public long insertTodo(TodoDetails todoDetails){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        Integer user_id = todoDetails.getUserId();
        String title = todoDetails.getTitle();
        String description = todoDetails.getDescription();
        String date = todoDetails.getDate();
        Integer reminderState = todoDetails.getReminderState();
        String time = todoDetails.getTime();

        contentValues.put(USER_ID, user_id);
        contentValues.put(TODO_TITLE, title);
        contentValues.put(TODO_DESCRIPTION, description);
        contentValues.put(TODO_DATE, date);
        contentValues.put(TODO_REMINDER_STATE, reminderState);
        contentValues.put(TODO_TIME, time);

        long rowId = sqLiteDatabase.insert(TABLE_TODO, null, contentValues);
        return rowId;
    }

    public Boolean findTodo(TodoDetails todoDetails){
        return false;
    }

    public long insertWallet(WalletDetails walletDetails){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        Integer user_id = walletDetails.getUserId();
        String title = walletDetails.getTitle();
        String description = walletDetails.getDescription();
        String date = walletDetails.getDate().toString();
        Integer type = walletDetails.getType();

        contentValues.put(USER_ID, user_id);
        contentValues.put(WALLET_TITLE, title);
        contentValues.put(WALLET_DESCRIPTION, description);
        contentValues.put(WALLET_DATE, date);
        contentValues.put(WALLET_TYPE, type);

        long rowId = sqLiteDatabase.insert(TABLE_WALLET, null, contentValues);
        return rowId;
    }

    public Boolean findWallet(WalletDetails walletDetails){
        return false;
    }

    public void showToast(String message){
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
