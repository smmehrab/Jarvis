package com.example.jarvis.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.view.Gravity;
import android.widget.Toast;

import com.example.jarvis.Todo.Task;
import com.example.jarvis.UserHandling.User;
import com.example.jarvis.Wallet.Record;

import java.util.ArrayList;

public class SQLiteDatabaseHelper extends SQLiteOpenHelper {

    /*** Database ***/
    private static final String DATABASE_NAME = "database_local.db";

    /*** Others ***/
    private Context context;
    private static int VERSION_NUMBER = 5;
    private static final String DROP_TABLE = "DROP TABLE IF EXISTS ";

    /*** TABLE USER ***/
    private static final String TABLE_USER = "table_user";
    private static final String USER_ID = "user_id";
    private static final String USER_EMAIL = "user_email";
    private static final String USER_PASSWORD = "user_password";
    private static final String USER_CONFIRM_PASSWORD = "user_confirmPassword";

    private static final String CREATE_TABLE_USER = "CREATE TABLE " + TABLE_USER + "(" +
            USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            USER_EMAIL + " TEXT NOT NULL, " +
            USER_PASSWORD + " TEXT NOT NULL); ";


    /*** TABLE TODO ***/
    private static final String TABLE_TODO = "table_todo";
    private static final String TODO_TITLE = "todo_title";
    private static final String TODO_DESCRIPTION = "todo_description";

    private static final String TODO_YEAR = "todo_year";
    private static final String TODO_MONTH = "todo_month";
    private static final String TODO_DAY = "todo_day";

    private static final String TODO_HOUR = "todo_hour";
    private static final String TODO_MINUTE = "todo_minute";

    private static final String TODO_REMINDER_STATE = "todo_reminderState";
    private static final String TODO_IS_COMPLETED = "todo_isCompleted";

    private static final String CREATE_TABLE_TODO = "CREATE TABLE " + TABLE_TODO + "(" +
            USER_ID + " INTEGER, "  +
            TODO_TITLE + " TEXT NOT NULL, " +
            TODO_DESCRIPTION + " TEXT, " +

            TODO_YEAR + " TEXT NOT NULL, " +
            TODO_MONTH + " TEXT NOT NULL, " +
            TODO_DAY + " TEXT NOT NULL, " +

            TODO_HOUR + " TEXT, " +
            TODO_MINUTE + " TEXT, " +

            TODO_REMINDER_STATE + " INTEGER, " +
            TODO_IS_COMPLETED + " INTEGER, " +
            "FOREIGN KEY(" + USER_ID + ") REFERENCES " + TABLE_USER + "(" + USER_ID + "), " +
            "PRIMARY KEY(" + TODO_TITLE + ", " + TODO_YEAR + ", " + TODO_MONTH + ", " + TODO_DAY +  ")); ";


    /*** TABLE WALLET ***/
    private static final String TABLE_WALLET = "table_wallet";
    private static final String WALLET_TITLE = "wallet_title";
    private static final String WALLET_DESCRIPTION = "wallet_description";

    private static final String WALLET_YEAR = "wallet_year";
    private static final String WALLET_MONTH = "wallet_month";
    private static final String WALLET_DAY = "wallet_day";

    private static final String WALLET_TYPE = "wallet_type";
    private static final String WALLET_AMOUNT = "wallet_amount";

    private static final String CREATE_TABLE_WALLET = "CREATE TABLE " + TABLE_WALLET + "(" +
            USER_ID + " INTEGER, " +
            WALLET_TITLE + " TEXT NOT NULL, " +
            WALLET_DESCRIPTION + " TEXT, " +

            WALLET_YEAR + " TEXT NOT NULL, " +
            WALLET_MONTH + " TEXT NOT NULL, " +
            WALLET_DAY + " TEXT NOT NULL, " +

            WALLET_TYPE + " INTEGER NOT NULL, " +
            WALLET_AMOUNT + " TEXT, " +
            "FOREIGN KEY(" + USER_ID + ") REFERENCES " + TABLE_USER + "(" + USER_ID + "), " +
            "PRIMARY KEY(" + WALLET_TITLE + ", " + WALLET_YEAR  + ", " + WALLET_MONTH + ", " + WALLET_DAY + ", " + WALLET_TYPE + ")); ";


    /*** Constructor ***/
    public SQLiteDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION_NUMBER);
        this.context = context;
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        try {
            /*** To Create Tables ***/
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
            /*** To Drop & Recreate Tables ***/
            sqLiteDatabase.execSQL(DROP_TABLE+TABLE_USER);
            sqLiteDatabase.execSQL(DROP_TABLE+TABLE_TODO);
            sqLiteDatabase.execSQL(DROP_TABLE+TABLE_WALLET);
            onCreate(sqLiteDatabase);
        } catch (Exception e) {
            showToast("Exception : " + e);
        }
    }

    /*** To Insert New User ***/
    public long insertUser(User user){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        String getEmail = user.getEmail();
        String getPassword = user.getPassword();

        contentValues.put(USER_EMAIL, getEmail);
        contentValues.put(USER_PASSWORD, getPassword);

        long rowId = sqLiteDatabase.insert(TABLE_USER, null, contentValues);
        return rowId;
    }

    /*** To Find User ***/
    public Boolean findUser(String givenEmail, String givenPassword){
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

                if(email.equals(givenEmail) && password.equals(givenPassword)){
                    result = true;
                    break;
                }
            }
        }
        return result;
    }

    /*** To Get UserId from User Email ***/
    public Integer getUserId(String givenEmail){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_USER, null);
        Integer result = null;

        if(cursor.getCount() == 0){
            showToast("No Data Found");
        }
        else{
            while (cursor.moveToNext()){
                String email = cursor.getString(1);

                if(email.equals(givenEmail)){
                    result = Integer.parseInt(cursor.getString(0));
                    break;
                }
            }
        }
        return result;
    }

    public long insertTodo(Task task){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        Integer userId = task.getUserId();
        String title = task.getTitle();
        String description = task.getDescription();

        String year = task.getYear();
        String month = task.getMonth();
        String day = task.getDay();

        String hour = task.getHour();
        String minute = task.getMinute();

        Integer reminderState = task.getReminderState();
        Integer isCompleted = task.getIsCompleted();

        contentValues.put(USER_ID, userId);
        contentValues.put(TODO_TITLE, title);
        contentValues.put(TODO_DESCRIPTION, description);

        contentValues.put(TODO_YEAR, year);
        contentValues.put(TODO_MONTH, month);
        contentValues.put(TODO_DAY, day);

        contentValues.put(TODO_HOUR, hour);
        contentValues.put(TODO_MINUTE, minute);

        contentValues.put(TODO_REMINDER_STATE, reminderState);
        contentValues.put(TODO_IS_COMPLETED, isCompleted);

        long rowId = sqLiteDatabase.insert(TABLE_TODO, null, contentValues);
        return rowId;
    }

    public void updateTodo(Task task, String oldYear, String oldMonth, String oldDay, String oldTitle){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        Integer userId = task.getUserId();
        String title = task.getTitle();
        String description = task.getDescription();

        String year = task.getYear();
        String month = task.getMonth();
        String day = task.getDay();

        String hour = task.getHour();
        String minute = task.getMinute();

        Integer reminderState = task.getReminderState();
        Integer isCompleted = task.getIsCompleted();

        contentValues.put(USER_ID, userId);
        contentValues.put(TODO_TITLE, title);
        contentValues.put(TODO_DESCRIPTION, description);

        contentValues.put(TODO_YEAR, year);
        contentValues.put(TODO_MONTH, month);
        contentValues.put(TODO_DAY, day);

        contentValues.put(TODO_HOUR, hour);
        contentValues.put(TODO_MINUTE, minute);

        contentValues.put(TODO_REMINDER_STATE, reminderState);
        contentValues.put(TODO_IS_COMPLETED, isCompleted);

        sqLiteDatabase.update(TABLE_TODO, contentValues,
                USER_ID + " = ? AND " + TODO_YEAR + " = ? AND " + TODO_MONTH + " = ? AND " + TODO_DAY + " = ? AND " + TODO_TITLE + " = ?",
                new String[] {userId+"", oldYear, oldMonth, oldDay, oldTitle});
    }

    public void updateTodoDatabase(ArrayList<Task> tasks){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        for(int i=0; i<tasks.size(); i++){
            ContentValues contentValues = new ContentValues();

            Integer userId = tasks.get(i).getUserId();
            String title =  tasks.get(i).getTitle();
            String description =  tasks.get(i).getDescription();

            String year =  tasks.get(i).getYear();
            String month =  tasks.get(i).getMonth();
            String day =  tasks.get(i).getDay();

            String hour =  tasks.get(i).getHour();
            String minute =  tasks.get(i).getMinute();

            Integer reminderState =  tasks.get(i).getReminderState();

            contentValues.put(USER_ID, userId);
            contentValues.put(TODO_TITLE, title);
            contentValues.put(TODO_DESCRIPTION, description);

            contentValues.put(TODO_YEAR, year);
            contentValues.put(TODO_MONTH, month);
            contentValues.put(TODO_DAY, day);

            contentValues.put(TODO_HOUR, hour);
            contentValues.put(TODO_MINUTE, minute);

            contentValues.put(TODO_REMINDER_STATE, reminderState);

            sqLiteDatabase.update(TABLE_TODO, contentValues,
                    USER_ID + " = ? AND " + TODO_YEAR + " = ? AND " + TODO_MONTH + " = ? AND " + TODO_DAY + " = ? AND " + TODO_TITLE + " = ?",
                    new String[] {userId+"", year, month, day, title});
        }
    }

    public Task findTodo(Integer userId, String year, String month, String day, String title){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        Cursor cursor = sqLiteDatabase.query(TABLE_TODO,
                null,
                USER_ID + " = ? AND " + TODO_YEAR + " = ? AND " + TODO_MONTH + " = ? AND " + TODO_DAY + " = ? AND " + TODO_TITLE + " = ?",
                new String[] {userId+"", year, month, day, title},
                null,
                null,
                TODO_YEAR + " ASC, " + TODO_MONTH  + " ASC, " + TODO_DAY  + " ASC");


        cursor.moveToPosition(0);
        sqLiteDatabase.close();

        return new Task(Integer.parseInt(cursor.getString(cursor.getColumnIndex(USER_ID))),
                cursor.getString(cursor.getColumnIndex(TODO_TITLE)),
                cursor.getString(cursor.getColumnIndex(TODO_DESCRIPTION)),

                cursor.getString(cursor.getColumnIndex(TODO_YEAR)),
                cursor.getString(cursor.getColumnIndex(TODO_MONTH)),
                cursor.getString(cursor.getColumnIndex(TODO_DAY)),

                cursor.getString(cursor.getColumnIndex(TODO_HOUR)),
                cursor.getString(cursor.getColumnIndex(TODO_MINUTE)),

                Integer.parseInt(cursor.getString(cursor.getColumnIndex(TODO_REMINDER_STATE))),
                Integer.parseInt(cursor.getString(cursor.getColumnIndex(TODO_IS_COMPLETED))));
    }

    public void deleteTodo(Integer userId, String year, String month, String day, String title){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        int i = sqLiteDatabase.delete(TABLE_TODO,
                USER_ID + " = ? AND " + TODO_YEAR + " = ? AND " + TODO_MONTH + " = ? AND " + TODO_DAY + " = ? AND " + TODO_TITLE + " = ?",
                new String[] {userId+"", year, month, day, title});

        sqLiteDatabase.close();
    }

    public ArrayList<Task> loadTodoItems(){
        ArrayList<Task> tasks = new ArrayList<Task>();

        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_TODO +
                " ORDER BY " + TODO_YEAR + ", " + TODO_MONTH + ", " + TODO_DAY + ", " + TODO_IS_COMPLETED + ", " + TODO_TITLE + ";", null);
        cursor.moveToPosition(0);

        if(cursor.getCount() == 0){
          showToast("No Data Found");
        }
        else{
            while (cursor.moveToNext()){
                Integer userId = Integer.parseInt(cursor.getString(0));
                String title = cursor.getString(1);
                String description = cursor.getString(2);

                String year = cursor.getString(3);
                String month = cursor.getString(4);
                String day = cursor.getString(5);

                String hour = cursor.getString(6);
                String minute = cursor.getString(7);

                Integer reminderState = Integer.parseInt(cursor.getString(8));
                Integer isCompleted = Integer.parseInt(cursor.getString(9));

                tasks.add(new Task(userId, title, description, year, month, day, hour, minute, reminderState, isCompleted));
            }
        }

        return tasks;
    }

    public Record findRecord(Integer userId, String year, String month, String day, String title, Integer type) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        Cursor cursor = sqLiteDatabase.query(TABLE_WALLET,
                null,
                USER_ID + " = ? AND " + WALLET_YEAR + " = ? AND " + WALLET_MONTH + " = ? AND " + WALLET_DAY + " = ? AND " + WALLET_TITLE + " = ? AND " + WALLET_TYPE + " = ?",
                new String[]{userId + "", year, month, day, title, type + ""},
                null,
                null,
                WALLET_YEAR + " ASC, " + WALLET_MONTH  + " ASC, " + WALLET_DAY  + " ASC");

        cursor.moveToPosition(0);
        sqLiteDatabase.close();


        return new Record(
                Integer.parseInt(cursor.getString(cursor.getColumnIndex(USER_ID))),
                cursor.getString(cursor.getColumnIndex(WALLET_TITLE)),
                cursor.getString(cursor.getColumnIndex(WALLET_DESCRIPTION)),

                cursor.getString(cursor.getColumnIndex(WALLET_YEAR)),
                cursor.getString(cursor.getColumnIndex(WALLET_MONTH)),
                cursor.getString(cursor.getColumnIndex(WALLET_DAY)),

                Integer.parseInt(cursor.getString(cursor.getColumnIndex(WALLET_TYPE))),
                cursor.getString(cursor.getColumnIndex(WALLET_AMOUNT)));

    }

    public long insertRecord(Record record){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        Integer user_id = record.getUserId();
        String title = record.getTitle();
        String description = record.getDescription();

        String year = record.getYear();
        String month = record.getMonth();
        String day = record.getDay();

        Integer type = record.getType();
        String amount = record.getAmount();

        contentValues.put(USER_ID, user_id);
        contentValues.put(WALLET_TITLE, title);
        contentValues.put(WALLET_DESCRIPTION, description);

        contentValues.put(WALLET_YEAR, year);
        contentValues.put(WALLET_MONTH, month);
        contentValues.put(WALLET_DAY, day);

        contentValues.put(WALLET_TYPE, type);
        contentValues.put(WALLET_AMOUNT, amount);

        long rowId = sqLiteDatabase.insert(TABLE_WALLET, null, contentValues);
        return rowId;
    }

    public void updateRecord(Record record, String oldYear, String oldMonth, String oldDay, String oldTitle, Integer oldType){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        Integer userId = record.getUserId();
        String title = record.getTitle();
        String description = record.getDescription();

        String year = record.getYear();
        String month = record.getMonth();
        String day = record.getDay();

        Integer type = record.getType();
        String amount = record.getAmount();

        contentValues.put(USER_ID, userId);
        contentValues.put(WALLET_TITLE, title);
        contentValues.put(WALLET_DESCRIPTION, description);

        contentValues.put(WALLET_YEAR, year);
        contentValues.put(WALLET_MONTH, month);
        contentValues.put(WALLET_DAY, day);

        contentValues.put(WALLET_TYPE, type);
        contentValues.put(WALLET_AMOUNT, amount);

        sqLiteDatabase.update(TABLE_WALLET, contentValues,
                USER_ID + " = ? AND " + WALLET_YEAR + " = ? AND " + WALLET_MONTH + " = ? AND " + WALLET_DAY + " = ? AND " + WALLET_TITLE + " = ? AND " + WALLET_TYPE + " = ?",
                new String[] {userId+"", oldYear, oldMonth, oldDay, oldTitle, oldType+""});

        sqLiteDatabase.close();
    }

    public void deleteRecord(Integer userId, String title, String year, String month, String day, Integer type){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        int i = sqLiteDatabase.delete(TABLE_WALLET,
                USER_ID + " = ? AND " + WALLET_YEAR + " = ? AND " + WALLET_MONTH + " = ? AND " + WALLET_DAY + " = ? AND " + WALLET_TITLE + " = ? AND " + WALLET_TYPE + " = ?",
                new String[]{userId + "", year, month, day, title, type + ""});

        sqLiteDatabase.close();
    }

    public ArrayList<Record> loadWalletItems(){
        ArrayList<Record> records = new ArrayList<Record>();

        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_WALLET +
                " ORDER BY " + WALLET_YEAR + ", " + WALLET_MONTH + ", " + WALLET_DAY + ";", null);

        cursor.moveToPosition(0);
        sqLiteDatabase.close();

        if(cursor.getCount() == 0){
            showToast("No Data Found");
        }
        else{
            while (cursor.moveToNext()){
                Integer userId = Integer.parseInt(cursor.getString(0));
                String title = cursor.getString(1);
                String description = cursor.getString(2);

                String year = cursor.getString(3);
                String month = cursor.getString(4);
                String day = cursor.getString(5);

                Integer type = Integer.parseInt(cursor.getString(6));
                String amount = cursor.getString(7);

                records.add(new Record(userId, title, description, year, month, day, type, amount));
            }
        }

        return records;
    }

    public void showToast(String message){
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
