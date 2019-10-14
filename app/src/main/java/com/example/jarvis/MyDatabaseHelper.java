package com.example.jarvis;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.example.jarvis.UserHandling.UserDetails;

public class MyDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "user_details.db";
    private static final String TABLE_NAME = "user_details";
    private static final String ID = "_id";
    private static final String EMAIL = "Email";
    private static final String PASSWORD = "Password";
    private static final String CONFIRM_PASSWORD = "Confirm_Password";
    private static int VERSION_NUMBER= 1;

    private Context context;

    private  static final String CREATE_TABLE = "CREATE TABLE "+TABLE_NAME+"("+ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+EMAIL+" VARCHAR(255) NOT NULL, "+PASSWORD+ " VARCHAR(255) NOT NULL, "+CONFIRM_PASSWORD+ " VARCHAR(255) NOT NULL); ";
    private static final String DROP_TABLE = " DROP TABLE IF EXISTS " + TABLE_NAME;

    public MyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION_NUMBER);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        try {
            Toast.makeText(context, "onCreate is called", Toast.LENGTH_LONG).show();
            sqLiteDatabase.execSQL(CREATE_TABLE);
        }
        catch (Exception e){
            Toast.makeText(context, "Exception : " + e, Toast.LENGTH_LONG).show();

        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {


        try {
            Toast.makeText(context, "onUpgrade is called", Toast.LENGTH_LONG).show();
            sqLiteDatabase.execSQL(DROP_TABLE);
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

            long rowId = sqLiteDatabase.insert(TABLE_NAME, null, contentValues);
            return rowId;

        }
        else
            return 0;
    }

    public Boolean findUser(String mail, String pass){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_NAME, null);
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


}
