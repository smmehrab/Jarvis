package com.example.jarvis.Reminder;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.jarvis.R;

public class CancelAlarm extends AppCompatActivity {

    private Button cancelAlarm;
    private AlertReceiver alertReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel_alarm);

        Toast.makeText(getApplicationContext(), "Cancel Alarm", Toast.LENGTH_SHORT).show();
        //alertReceiver.mediaPlayer.stop();
        cancelAlarm = findViewById(R.id.cancel_alarm_button);

       cancelAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertReceiver.mediaPlayer.stop();
                finish();
            }
        });
    }
}
