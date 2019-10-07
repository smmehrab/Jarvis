package com.example.jarvis;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class MyDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "user_details.db";

    /****FOR USER DETAILS WHILE SIGNING UP*****/
    private static final String USERTABLENAME = "user_details";
    private static final String ID = "_id";
    private static final String EMAIL = "Email";
    private static final String PASSWORD = "Password";
    private static final String CONFIRM_PASSWORD = "Confirm_Password";

    /****FOR TO DO DETAILS****/
    private static final String TODOTABLENAME = "todo_details";
    private static final String TITLE = "title";
    private static final String DESCRIPTION = "description";
    private static final String TIME = "time";
    private static final String DATE = "date";

    /****FOR WALLET****/
    private static final String WALLET_TABLENAME = "wallet";
    private static final String WALLET_TITLE = "title";
    private static final String WALLET_DESCRIPTION = "description";
    private static final String WALLET_DATE = "date";
    private static final String WALLET_EXPENSE_TYPE = "expensetype";


    private static int VERSION_NUMBER= 4;

    private Context context;

    /***Create and drop table for sign up***/
    private  static final String CREATE_TABLE_FOR_USER = "CREATE TABLE "+USERTABLENAME+"("+ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+EMAIL+" VARCHAR(255) NOT NULL, "+PASSWORD+ " VARCHAR(255) NOT NULL, "+CONFIRM_PASSWORD+ " VARCHAR(255) NOT NULL); ";
    private static final String DROP_TABLE_FOR_USER = " DROP TABLE IF EXISTS " + USERTABLENAME;

    /***Create and drop table for TO DO DETAILS****/
    private  static final String CREATE_TABLE_FOR_TODO = "CREATE TABLE "+TODOTABLENAME+"("+ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+TITLE+" VARCHAR(255) NOT NULL, "+DESCRIPTION+ " VARCHAR(255) NOT NULL, "+DATE+ " DATE, "+TIME+ " TIME); ";
    private static final String DROP_TABLE_FOR_TODO = " DROP TABLE IF EXISTS " + TODOTABLENAME;

    /***Create and drop table for WALLET***/
    private  static final String CREATE_TABLE_FOR_WALLET = "CREATE TABLE "+WALLET_TABLENAME+"("+ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+WALLET_TITLE+" VARCHAR(255) NOT NULL, "+WALLET_DESCRIPTION+ " VARCHAR(255) NOT NULL, "+WALLET_DATE+ " DATE, "+WALLET_EXPENSE_TYPE+ " VARCHAR(255) NOT NULL); ";
    private static final String DROP_TABLE_FOR_WALLET = " DROP TABLE IF EXISTS " + WALLET_TABLENAME;


    public MyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION_NUMBER);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        try {
            Toast.makeText(context, "onCreate is called", Toast.LENGTH_LONG).show();
            sqLiteDatabase.execSQL(CREATE_TABLE_FOR_USER);
            sqLiteDatabase.execSQL(CREATE_TABLE_FOR_TODO);
            sqLiteDatabase.execSQL(CREATE_TABLE_FOR_WALLET);
        }
        catch (Exception e){
            Toast.makeText(context, "Exception : " + e, Toast.LENGTH_LONG).show();

        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {


        try {
            Toast.makeText(context, "onUpgrade is called", Toast.LENGTH_LONG).show();
            sqLiteDatabase.execSQL(DROP_TABLE_FOR_USER);
            sqLiteDatabase.execSQL(DROP_TABLE_FOR_TODO);
            sqLiteDatabase.execSQL(DROP_TABLE_FOR_WALLET);
            onCreate(sqLiteDatabase);

        } catch (Exception e) {
            Toast.makeText(context, "Exception : " + e, Toast.LENGTH_LONG).show();


        }
    }

    public long insertData(UserDetails userDetails){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        String getEmail = userDetails.getEmail();
        String getPassword = userDetails.getPassword();
        String getConfirmPassword = userDetails.getConfirmPassword();

        if(getPassword.equals(getConfirmPassword)){

            contentValues.put(EMAIL, getEmail);
            contentValues.put(PASSWORD, getPassword);
            contentValues.put(CONFIRM_PASSWORD, getConfirmPassword);

            long rowId = sqLiteDatabase.insert(USERTABLENAME, null, contentValues);
            return rowId;

        }
        else
            return 0;
    }

    public Boolean findUser(String mail, String pass){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + USERTABLENAME, null);
        Boolean result = false;

        if(cursor.getCount() == 0){
            Toast.makeText(context, "No data is found", Toast.LENGTH_LONG).show();

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

    //Insert data for To Do
    public long insertDataTodo(TodoDetails todoDetails) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        String getTitle = todoDetails.getTitle();
        String getDesc = todoDetails.getDesc();
        String getTime = todoDetails.getTime();
        String getDate = todoDetails.getDate();

        contentValues.put(TITLE, getTitle);
        contentValues.put(DESCRIPTION, getDesc);
        contentValues.put(DATE, getDate);
        contentValues.put(TIME, getTime);

        long rowId = sqLiteDatabase.insert(TODOTABLENAME, null, contentValues);
        return rowId;
    }

    //Insert data for Wallet
    public long insertDataWallet(WalletDetails walletDetails) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        String getTitle = walletDetails.getTitle();
        String getDesc = walletDetails.getDesc();
        String getDate = walletDetails.getDate();
        String expense = walletDetails.getExpenseType();

        contentValues.put(TITLE, getTitle);
        contentValues.put(DESCRIPTION, getDesc);
        contentValues.put(DATE, getDate);
        contentValues.put(WALLET_EXPENSE_TYPE, expense);

        long rowId = sqLiteDatabase.insert(WALLET_TABLENAME, null, contentValues);
        return rowId;
    }


    /****JUST FOR CHECK****/
    private static final String SELECT_ALL_TODO = "SELECT * FROM " + TODOTABLENAME;
    public Cursor displayAllDataToDo(){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(SELECT_ALL_TODO, null);
        return cursor;
    }

    private static final String SELECT_ALL_WALLET = "SELECT * FROM " + WALLET_TABLENAME;
    public Cursor displayAllDataWallet(){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(SELECT_ALL_WALLET, null);
        return cursor;
    }
}
