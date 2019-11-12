package com.example.jarvis.Journal;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.jarvis.R;
import com.example.jarvis.SQLite.SQLiteDatabaseHelper;
import com.example.jarvis.Util.DatePickerFragment;
import com.example.jarvis.Util.TimePickerFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.checkerframework.checker.units.qual.Time;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import jp.wasabeef.richeditor.RichEditor;
public class AddJournalActivity extends AppCompatActivity implements View.OnClickListener, RecognitionListener,
        DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    /** Voice Command Variables */
    private static final int REQUEST_RECORD_PERMISSION = 100;
    private boolean[] toogleArray = new boolean [11];
    private ProgressBar progressBar;
    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;
    private String LOG_TAG = "AddJournalActivity";
    private ToggleButton voiceCommandToggleButton;

    /** For Toolbar */
    private ImageButton toolbarLeftButton,
    toolbarCalButton;
    TextView toolbarTitle;

    /** For Editors */
    private RichEditor mEditor;
    private TextView mPreview;
    String check;
    private boolean changed = false;
    private static final int REQUEST_GET_SINGLE_FILE = 10;
    /** For Date and Time*/

    private String y, d, nTitle = "", m, h, min;
    SimpleDateFormat titleFormat = new SimpleDateFormat("d MMM h:m a");
    SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM");


    /** Form journalVariables */

    String title, description, year, month, day, hour, minute, imageLink="", fileLink="";
    Journal journal = new Journal();
    Intent intent;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_journal);
         intent = getIntent();
        setUI();
        addFuctionalityInEditor();

       if( !check.equals("new_journal_from_journal_activity")) {
           readFromFile();
       }
    }

    private void addFuctionalityInEditor() {
        findViewById(R.id.action_undo).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.undo();
            }
        });

        findViewById(R.id.action_redo).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.redo();
            }
        });

        findViewById(R.id.action_bold).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override public void onClick(View v) {
                mEditor.setBold();
                if(!toogleArray[0]) {
                    findViewById(R.id.action_bold).setBackground(getResources().getDrawable(R.drawable.icon_color_ash_op));
                    toogleArray[0] = true;
                }
                else{
                    toogleArray[0] = false;
                    findViewById(R.id.action_bold).setBackground(getResources().getDrawable(R.color.colorWhite));
                }

            }
        });

        findViewById(R.id.action_italic).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setItalic();
                if(!toogleArray[1]) {
                    findViewById(R.id.action_italic).setBackground(getResources().getDrawable(R.drawable.icon_color_ash_op));
                    toogleArray[1] = true;
                }

                else{
                    toogleArray[1] = false;
                    findViewById(R.id.action_italic).setBackground(getResources().getDrawable(R.color.colorWhite));
                }
            }
        });

        findViewById(R.id.action_subscript).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setSubscript();
                if(!toogleArray[2]) {
                    findViewById(R.id.action_subscript).setBackground(getResources().getDrawable(R.drawable.icon_color_ash_op));
                    toogleArray[2] = true;
                }

                else{
                    toogleArray[2] = false;
                    findViewById(R.id.action_subscript).setBackground(getResources().getDrawable(R.color.colorWhite));
                }
            }
        });

        findViewById(R.id.action_superscript).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setSuperscript();
                if(!toogleArray[3]) {
                    findViewById(R.id.action_superscript).setBackground(getResources().getDrawable(R.drawable.icon_color_ash_op));
                    toogleArray[3] = true;
                }

                else{
                    toogleArray[3] = false;
                    findViewById(R.id.action_superscript).setBackground(getResources().getDrawable(R.color.colorWhite));
                }
            }
        });

        findViewById(R.id.action_strikethrough).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setStrikeThrough();
                if(!toogleArray[4]) {
                    findViewById(R.id.action_strikethrough).setBackground(getResources().getDrawable(R.drawable.icon_color_ash_op));
                    toogleArray[4] = true;
                }

                else{
                    toogleArray[4] = false;
                    findViewById(R.id.action_strikethrough).setBackground(getResources().getDrawable(R.color.colorWhite));
                }
            }
        });

        findViewById(R.id.action_underline).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setUnderline();
                if(!toogleArray[5]) {
                    findViewById(R.id.action_underline).setBackground(getResources().getDrawable(R.drawable.icon_color_ash_op));
                    toogleArray[5] = true;
                }

                else{
                    toogleArray[5] = false;
                    findViewById(R.id.action_underline).setBackground(getResources().getDrawable(R.color.colorWhite));
                }
            }
        });

        findViewById(R.id.action_heading1).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {

                mEditor.setHeading(1);
                if(!toogleArray[6]) {

                    findViewById(R.id.action_heading1).setBackground(getResources().getDrawable(R.drawable.icon_color_ash_op));
                    toogleArray[6] = true;
                }

                else{
                    toogleArray[6] = false;
                    findViewById(R.id.action_heading1).setBackground(getResources().getDrawable(R.color.colorWhite));
                }
            }
        });

        findViewById(R.id.action_heading2).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setHeading(2);
                if(!toogleArray[7]) {
                    findViewById(R.id.action_heading2).setBackground(getResources().getDrawable(R.drawable.icon_color_ash_op));
                    toogleArray[7] = true;
                }

                else{
                    toogleArray[7] = false;
                    findViewById(R.id.action_heading2).setBackground(getResources().getDrawable(R.color.colorWhite));
                }
            }
        });

        findViewById(R.id.action_heading3).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setHeading(3);
                if(!toogleArray[8]) {
                    findViewById(R.id.action_heading3).setBackground(getResources().getDrawable(R.drawable.icon_color_ash_op));
                    toogleArray[8] = true;
                }

                else{
                    toogleArray[8] = false;
                    findViewById(R.id.action_heading3).setBackground(getResources().getDrawable(R.color.colorWhite));
                }
            }
        });

        findViewById(R.id.action_txt_color).setOnClickListener(new View.OnClickListener() {
            private boolean isChanged;

            @Override public void onClick(View v) {
                mEditor.setTextColor(isChanged ? Color.BLACK : Color.RED);
                isChanged = !isChanged;
            }
        });

        findViewById(R.id.action_bg_color).setOnClickListener(new View.OnClickListener() {
            private boolean isChanged;

            @Override public void onClick(View v) {
                mEditor.setTextBackgroundColor(isChanged ? Color.WHITE : Color.YELLOW);
                isChanged = !isChanged;
            }
        });

        findViewById(R.id.action_indent).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setIndent();
            }
        });

        findViewById(R.id.action_outdent).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setOutdent();
            }
        });

        findViewById(R.id.action_align_left).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setAlignLeft();
            }
        });

        findViewById(R.id.action_align_center).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setAlignCenter();
            }
        });

        findViewById(R.id.action_align_right).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setAlignRight();
            }
        });

        findViewById(R.id.action_blockquote).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setBlockquote();
            }
        });

        findViewById(R.id.action_insert_bullets).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setBullets();
            }
        });

        findViewById(R.id.action_insert_numbers).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setNumbers();
            }
        });

        findViewById(R.id.action_insert_image).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                try {
                    if (ActivityCompat.checkSelfPermission(AddJournalActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(AddJournalActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_GET_SINGLE_FILE);
                    } else {
                        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(galleryIntent, REQUEST_GET_SINGLE_FILE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
//                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                intent.addCategory(Intent.CATEGORY_OPENABLE);
//                intent.setType("image/*");
//               startActivityForResult(Intent.createChooser(intent, "Select Picture"),REQUEST_GET_SINGLE_FILE);
            }
        });

        findViewById(R.id.action_insert_checkbox).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.insertTodo();
            }
        });
    }


    void setUI(){
        findXmlElements();
        setListeners();
        initializeJournal();
        initializeUI();
        setVoiceCommandFeature();
        isVoiceCommandOn();
    }

    public void findXmlElements(){

        mEditor = (RichEditor) findViewById(R.id.editor);
        progressBar = (ProgressBar) findViewById(R.id.add_journal_progress_bar);
        voiceCommandToggleButton = (ToggleButton) findViewById(R.id.add_journal_voice_command_toggle_btn);

        toolbarCalButton = findViewById(R.id.journal_toolbar_calender);
        toolbarLeftButton = findViewById(R.id.journal_toolbar_back_arrow);
        toolbarTitle = findViewById(R.id.journal_toolbar_tv);

    }


    public void setListeners(){

        toolbarLeftButton.setOnClickListener(this);
        toolbarCalButton.setOnClickListener(this);

        // Voice Command On/Off
        voiceCommandToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setIndeterminate(true);
                    ActivityCompat.requestPermissions
                            (AddJournalActivity.this,
                                    new String[]{Manifest.permission.RECORD_AUDIO},
                                    REQUEST_RECORD_PERMISSION);
                } else {
                    progressBar.setIndeterminate(false);
                    progressBar.setVisibility(View.INVISIBLE);
                    speech.stopListening();
                }
            }
        });


        mEditor.setOnTextChangeListener(new RichEditor.OnTextChangeListener() {
            @Override
            public void onTextChange(String text) {
                if(changed == false) {
                    toolbarLeftButton.setImageResource(R.drawable.icon_check);
                    changed = true;
                }
            }
        });
    }

    public void initializeUI() {
        progressBar.setVisibility(View.INVISIBLE);
        mEditor.setEditorFontSize(16);
        mEditor.setEditorFontColor(Color.BLACK);
        mEditor.setEditorWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        //mEditor.focusEditor();
        mEditor.setPadding(10, 10, 10, 10);
        toolbarTitle.setText(journal.getTitle());
        mEditor.loadCSS("file:///android_assets/style.css");
        mEditor.setPlaceholder("Insert text here...");
        for(int i = 0; i < toogleArray.length; ++i){
            toogleArray[i] = false;
        }

        check = intent.getStringExtra("status");
        if(check.equals("journal_from_show_activity") || check.equals("old_journal_from_journal_activity")){
            toolbarCalButton.setClickable(false);
        }
    }


    public void initializeJournal(){

        check = intent.getStringExtra("status");
        if(check.equals("new_journal_from_journal_activity")){

            /**
             * As this a new journal so we use the current time and date to
             * the title set there attributes
             *
             */
            Date date = new Date();
            Calendar calendar = Calendar.getInstance();
            year = Integer.toString(calendar.get(Calendar.YEAR));
            month = Integer.toString(calendar.get(Calendar.MONTH));
            day = Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));
            hour = Integer.toString(calendar.get(Calendar.HOUR));
            minute = Integer.toString(calendar.get(Calendar.MINUTE));
            title = titleFormat.format(date);
            toolbarTitle.setText(title);
            journal.setTitle(title);
            journal.setFileLink(title);
            journal.setYear(year);
            journal.setMonth(month);
            journal.setDay(day);
            journal.setHour(hour);
            journal.setMinute(minute);
           // showToast("New !!!");

        }

        else if(check.equals( "journal_from_show_activity")){

            title = intent.getStringExtra("sa_edit_title");
            description = intent.getStringExtra("sa_edit_description");
            year = intent.getStringExtra("sa_edit_year");
            month = intent.getStringExtra("sa_edit_month");
            day  = intent.getStringExtra("sa_edit_day");
            hour = intent.getStringExtra("sa_edit_hour");
            minute = intent.getStringExtra("sa_edit_minute");
            imageLink = intent.getStringExtra("sa_edit_imageLink");
            fileLink = intent.getStringExtra("sa_edit_fileLink");

            journal.setTitle(title);
            journal.setFileLink(title);
            journal.setYear(year);
            journal.setMonth(month);
            journal.setDay(day);
            journal.setHour(hour);
            journal.setMinute(minute);
            journal.setDescription(description);
            journal.setImageLink(imageLink);
            journal.setFileLink(fileLink);
        }

        else if(check.equals("old_journal_from_journal_activity")){

            title = intent.getStringExtra("ja_edit_title");
            description = intent.getStringExtra("ja_edit_description");
            year = intent.getStringExtra("ja_edit_year");
            month = intent.getStringExtra("ja_edit_month");
            day  = intent.getStringExtra("ja_edit_day");
            hour = intent.getStringExtra("ja_edit_hour");
            minute = intent.getStringExtra("ja_edit_minute");
            imageLink = intent.getStringExtra("ja_edit_imageLink");
            fileLink = intent.getStringExtra("ja_edit_fileLink");

            journal.setTitle(title);
            journal.setFileLink(title);
            journal.setYear(year);
            journal.setMonth(month);
            journal.setDay(day);
            journal.setHour(hour);
            journal.setMinute(minute);
            journal.setDescription(description);
            journal.setImageLink(imageLink);
            journal.setFileLink(fileLink);
        }
    }

    public void writeToFile(String text){

        try {
            FileOutputStream fileOutputStream = openFileOutput(journal.getFileLink(), Context.MODE_PRIVATE);
            fileOutputStream.write(text.getBytes());
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void readFromFile(){
        try {
            FileInputStream fileInputStream = openFileInput(journal.getFileLink());
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line;
            StringBuffer stringBuffer = new StringBuffer();

            while((line = bufferedReader.readLine()) != null){
                stringBuffer.append(line+"\n");
            }
            mEditor.setHtml(String.valueOf(stringBuffer));
          //  showToast(stringBuffer.toString());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /***
        Adding an Image
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == RESULT_OK) {
                if (requestCode == REQUEST_GET_SINGLE_FILE) {
                    Uri selectedImageUri = data.getData();
                    // Get the path from the Uri
                    final String path = getPathFromURI(selectedImageUri);
                    if (path != null) {
                        Log.d("gds", path);
                        mEditor.insertImage(path, "image");

                    } else {
                        Toast.makeText(this, "null", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } catch (Exception e) {
            Log.e("FileSelectorActivity", "File select error", e);
        }
    }


    public String getPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }


    public void showToast(String message){
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AddJournalActivity.this, JournalActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onClick(View view) {
        if(view == toolbarCalButton){

            DatePickerFragment fragment = new DatePickerFragment();
            fragment.show(getSupportFragmentManager(), "tag1");



        } else if( (view == toolbarLeftButton) && changed ){

            journal.setFileLink(journal.getTitle());
            if(mEditor.getHtml().length() >= 30){
                journal.setDescription(mEditor.getHtml().substring(0, 30));
            }
            else{
                journal.setDescription(mEditor.getHtml());
            }

            writeToFile(mEditor.getHtml());
            /**
             * Lot to do :(
             */
            SQLiteDatabaseHelper sqLiteDatabaseHelper = new SQLiteDatabaseHelper(this);
            SQLiteDatabase sqLiteDatabase = sqLiteDatabaseHelper.getWritableDatabase();

            sqLiteDatabaseHelper.insertJournal(journal);
            onBackPressed();
        }
        else if( (view == toolbarLeftButton) && !changed){
            onBackPressed();
        }
    }


    /**
     *For Date and Time picker fragment
     */

    @Override
    public void onDateSet(DatePicker datePicker, int y, int m, int d) {

        /** Formatting Selected Date so that we can set the date on EditText */
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, y);
        calendar.set(Calendar.MONTH, m);
        calendar.set(Calendar.DAY_OF_MONTH, d);
        String selectedDate = dateFormat.format(calendar.getTime());

        /** Setting Selected Date to the EditText */
        nTitle += selectedDate;

        /** Assigning Selected Date to the following variables
         * so that we can use these variables to create task object */
        year = Integer.toString(calendar.get(Calendar.YEAR));
        month = Integer.toString(calendar.get(Calendar.MONTH));
        day = Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));

        TimePickerFragment timePickerFragment = new TimePickerFragment();
        timePickerFragment.show(getSupportFragmentManager(), "tag");

    }

    @Override
    public void onTimeSet(TimePicker timePicker, int h ,int m) {

        hour = Integer.toString(h);
        minute = Integer.toString(m);

        /** Formatting Selected Time so that we can set the time on EditText */
        String amPm = " am";
        if (h >= 12) {
            amPm = " pm";
            h = h - 12;
        }
        nTitle+=" ";
        nTitle += String.format("%02d:%02d", h, m) + amPm;

        title = nTitle;
        toolbarTitle.setText(title);
        journal.setTitle(title);
        journal.setTitle(title);
        journal.setFileLink(title);
        journal.setYear(year);
        journal.setMonth(month);
        journal.setDay(day);
        journal.setHour(hour);
        journal.setMinute(minute);

        nTitle = "";

    }


    /** VOICE COMMAND HANDLING */

    public void setVoiceCommandFeature(){
        speech = SpeechRecognizer.createSpeechRecognizer(this);
        Log.i(LOG_TAG, "isRecognitionAvailable: " + SpeechRecognizer.isRecognitionAvailable(this));
        speech.setRecognitionListener(this);

        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
    }

    public void isVoiceCommandOn(){
        if(getIntent().getExtras()!=null){
            if(getIntent().getExtras().getString("voice_command")!=null ){
                if(Objects.equals(getIntent().getExtras().getString("voice_command"), "true")) {
                    voiceCommandToggleButton.setChecked(true);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_RECORD_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    speech.startListening(recognizerIntent);
                } else {
                    Toast.makeText(AddJournalActivity.this, "Permission Denied!", Toast
                            .LENGTH_SHORT).show();
                }
                break;
            case REQUEST_GET_SINGLE_FILE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(galleryIntent, REQUEST_GET_SINGLE_FILE);
                }
                else {
                    Toast.makeText(AddJournalActivity.this, "Permission Denied!", Toast
                            .LENGTH_SHORT).show();
                }
                break;

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (speech != null) {
            speech.destroy();
            Log.i(LOG_TAG, "destroy");
        }
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.i(LOG_TAG, "onBeginningOfSpeech");
        progressBar.setIndeterminate(false);
        progressBar.setMax(10);
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        Log.i(LOG_TAG, "onBufferReceived: " + buffer);
    }

    @Override
    public void onEndOfSpeech() {
        Log.i(LOG_TAG, "onEndOfSpeech");
        progressBar.setIndeterminate(true);
        voiceCommandToggleButton.setChecked(true);
    }

    @Override
    public void onError(int errorCode) {
        String errorMessage = getErrorText(errorCode);

        if (errorMessage.equals("Client side error")) {
            voiceCommandToggleButton.setChecked(true);
        } else {
            Log.d(LOG_TAG, "FAILED " + errorMessage);
            showToast(errorMessage);
            voiceCommandToggleButton.setChecked(false);
        }
    }

    @Override
    public void onEvent(int arg0, Bundle arg1) {
        Log.i(LOG_TAG, "onEvent");
    }

    @Override
    public void onPartialResults(Bundle arg0) {
        Log.i(LOG_TAG, "onPartialResults");
    }

    @Override
    public void onReadyForSpeech(Bundle arg0) {
        Log.i(LOG_TAG, "onReadyForSpeech");
    }

    @Override
    public void onResults(Bundle results) {
        Log.i(LOG_TAG, "onResults");
        ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

        String text = "";
        for (String result : matches)
            text += result + "\n";

        showToast(matches.get(0));

        if(matches.get(0).equals("close editor")) {
            Intent intent = new Intent(getApplicationContext(), JournalActivity.class);
            intent.putExtra("voice_command", "true");
            startActivity(intent);
        } else if(matches.get(0).equals("close voice command")) {

        } else if(matches.get(0).equals("save journal")) {
            showToast("Journal Saved");
            Intent intent = new Intent(getApplicationContext(), JournalActivity.class);
            intent.putExtra("voice_command", "true");
            startActivity(intent);
        } else if(matches.get(0).equals("show preview")) {
            showToast("Preview Shown");
        }

        else {
            showToast("Didn't Recognize \"" + matches.get(0) + "\"! Try Again!");
            voiceCommandToggleButton.setChecked(false);
        }
    }

    @Override
    public void onRmsChanged(float rmsdB) {
        Log.i(LOG_TAG, "onRmsChanged: " + rmsdB);
        progressBar.setProgress((int) rmsdB);
    }

    public static String getErrorText(int errorCode) {
        String message;
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "Client side error";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "Insufficient permissions";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "Network error";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "Network timeout";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "No match";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "RecognitionService busy";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "error from server";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "No speech input";
                break;
            default:
                message = "Didn't understand, please try again.";
                break;
        }
        return message;
    }


}
