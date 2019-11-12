package com.example.jarvis.Journal;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.jarvis.R;
import com.example.jarvis.SQLite.SQLiteDatabaseHelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Objects;

import jp.wasabeef.richeditor.RichEditor;

public class ShowJournalActivity extends AppCompatActivity implements
        View.OnClickListener, RecognitionListener {

    /** Voice Command Variables */
    private static final int REQUEST_RECORD_PERMISSION = 100;
    private ProgressBar progressBar;
    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;
    private String LOG_TAG = "AddJournalActivity";
    private ToggleButton voiceCommandToggleButton;


    /** For Toolbar */
    private ImageButton toolbarLeftButton,
            toolbardeleteButtton, toobarEditButton, toobarPrintButton;
    TextView toolbarTitle;

    /** For Editors */
    private RichEditor mEditor;
    private TextView mPreview;

    /**
     * For PrintFeature
     * */

    private WebView mWebView;

    /** Form journalVariables */

    String title, description, year, month, day, hour, minute, imageLink="", fileLink="";
    Journal journal = new Journal();
    String check;
    @RequiresApi(api = Build.VERSION_CODES.O)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_journal);
        setUI();
        readFromFile();
    }

    void setUI(){
        findXmlElements();
        setListeners();
        initializeJournal();
        initializeUI();
        setVoiceCommandFeature();
        isVoiceCommandOn();
    }

    private void initializeUI() {
        progressBar.setVisibility(View.INVISIBLE);
        mEditor.setEditorFontSize(16);
        mEditor.setEditorFontColor(Color.BLACK);
        mEditor.setPadding(10, 10, 10, 10);
       // toolbarTitle.setText(journal.getTitle());
        mEditor.setInputEnabled(false);
    }

    private void initializeJournal() {
        Intent intent = getIntent();
        check = intent.getStringExtra("status");
        if(check.equals("show_this_journal")){

            title = intent.getStringExtra("ja_show_title");
            description = intent.getStringExtra("ja_show_description");
            year = intent.getStringExtra("ja_show_year");
            month = intent.getStringExtra("ja_show_month");
            day  = intent.getStringExtra("ja_show_day");
            hour = intent.getStringExtra("ja_show_hour");
            minute = intent.getStringExtra("ja_show_minute");
            imageLink = intent.getStringExtra("ja_show_imageLink");
            fileLink = intent.getStringExtra("ja_show_fileLink");

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

            toolbarTitle.setText(journal.getTitle());

        }
        else{
            showToast("No!!!!!!!!!");
        }


    }

    private void findXmlElements() {
        mEditor = findViewById(R.id.show_editor);

        toolbarTitle = findViewById(R.id.journal_show_toolbar_tv);
        toobarEditButton = findViewById(R.id.journal_show_toolbar_edit);
        toolbardeleteButtton = findViewById(R.id.journal_show_toolbar_delete);
        toolbarLeftButton = findViewById(R.id.journal_show_toolbar_back_arrow);
        toobarPrintButton = findViewById(R.id.journal_show_toolbar_print);
        progressBar = (ProgressBar) findViewById(R.id.add_journal_progress_bar);
        voiceCommandToggleButton = (ToggleButton) findViewById(R.id.add_journal_voice_command_toggle_btn);


    }

    public void handleEditAction(){

        Intent intnt = new Intent(ShowJournalActivity.this, AddJournalActivity.class);
        intnt.putExtra("status", "journal_from_show_activity");
        sendJournalToActivity(journal, intnt);
        startActivity(intnt);
    }


    public void sendJournalToActivity(Journal j, Intent i){

        i.putExtra("sa_edit_title", j.getTitle());
        i.putExtra("sa_edit_description", j.getDescription());
        i.putExtra("sa_edit_year", j.getYear());
        i.putExtra("sa_edit_month", j.getMonth());
        i.putExtra("sa_edit_day", j.getDay());
        i.putExtra("sa_edit_hour", j.getHour());
        i.putExtra("sa_edit_minute", j.getMinute());
        i.putExtra("sa_edit_fileLink", j.getFileLink());
        i.putExtra("sa_edit_imageLink", j.getImageLink());


    }




    private void setListeners() {

        /**
         *
         * Toolbar view id listeners
         */

        toolbarLeftButton.setOnClickListener(this);
        toolbardeleteButtton.setOnClickListener(this);
        toobarEditButton.setOnClickListener(this);
        toobarPrintButton.setOnClickListener(this);

        // Voice Command On/Off
        voiceCommandToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setIndeterminate(true);
                    ActivityCompat.requestPermissions
                            (ShowJournalActivity.this,
                                    new String[]{Manifest.permission.RECORD_AUDIO},
                                    REQUEST_RECORD_PERMISSION);
                } else {
                    progressBar.setIndeterminate(false);
                    progressBar.setVisibility(View.INVISIBLE);
                    speech.stopListening();
                }
            }
        });


    }


    @Override
    public void onClick(View view) {

        if(view == toobarPrintButton){
            doWebViewPrint();
        }

        else if( view == toobarEditButton){
            handleEditAction();
        }

        else if(view == toolbardeleteButtton){
            deleteJournal();
        }

        else if( view == toolbarLeftButton){
            onBackPressed();
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
           // showToast(stringBuffer.toString());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showToast(String message){
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ShowJournalActivity.this, JournalActivity.class);
        startActivity(intent);
        finish();
    }


    private void doWebViewPrint() {

        // Create a WebView object specifically for printing
        WebView webView = new WebView(ShowJournalActivity.this);
        String htmlDocument = mEditor.getHtml();
        webView.loadDataWithBaseURL(null, htmlDocument, "text/HTML", "UTF-8", null);
        webView.setWebViewClient(new WebViewClient() {

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Log.d("Dhuru", "page finished loading " + url);
                createWebPrintJob(view);
                mWebView = null;
            }
        });

        mWebView = webView;

    }

    private void createWebPrintJob(WebView webView) {

        // Get a PrintManager instance
        PrintManager printManager = (PrintManager)ShowJournalActivity.this
                .getSystemService(Context.PRINT_SERVICE);

        String jobName = getString(R.string.app_name) + " Document";

        // Get a print adapter instance
        PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter(jobName);

        // Create a print job with name and adapter instance
        PrintJob printJob = printManager.print(jobName, printAdapter,
                new PrintAttributes.Builder().build());

    }


    /** Delete file option */
    public void deleteJournal(){
        Toast.makeText(this, "This is a error", Toast.LENGTH_LONG).show();

        AlertDialog.Builder alertbuilder = new AlertDialog.Builder(ShowJournalActivity.this);
        alertbuilder.setMessage("Do you want to delete the journal ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SQLiteDatabaseHelper sqLiteDatabaseHelper = new SQLiteDatabaseHelper(ShowJournalActivity.this);
                        SQLiteDatabase sqLiteDatabase = sqLiteDatabaseHelper.getReadableDatabase();
                        new File(journal.getFileLink()).deleteOnExit();
                        sqLiteDatabaseHelper.deleteJournal(journal.getFileLink());
                        onBackPressed();
                    }
                })
                .setNegativeButton("No", null);
        AlertDialog alert = alertbuilder.create();
        alert.show();


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
                    Toast.makeText(ShowJournalActivity.this, "Permission Denied!", Toast
                            .LENGTH_SHORT).show();
                }
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
