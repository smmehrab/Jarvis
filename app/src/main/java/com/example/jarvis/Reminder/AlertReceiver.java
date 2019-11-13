package com.example.jarvis.Reminder;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.jarvis.R;

public class AlertReceiver extends BroadcastReceiver {

    MediaPlayer mediaPlayer;
    private int notificationID;
    @Override
    public void onReceive(Context context, Intent intent) {
//        NotificationHelper notificationHelper = new NotificationHelper(context);
//        NotificationCompat.Builder nb = notificationHelper.getChannelNotification();
//        notificationHelper.getManager().notify(1, nb.build());

        long[] pattern = {500,500,500,500,500,500,500,500,500};
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


        notificationID = intent.getIntExtra("index", 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "Jarvis")
                .setSmallIcon(R.drawable.icon_activity_reminder)
                .setContentTitle("Alarm")
                .setContentText("Alarm is ringing")
                //.setColor(Color.rgb(100, (float) 64.8, 0))
                .setColor(Color.BLUE);
                //.setLights(Color.BLUE, 500, 500)
                //.setVibrate(pattern)
                //.setStyle(new NotificationCompat.InboxStyle())
                //.setSound(alarmSound)
                //.setAutoCancel(true)
                //.setOnlyAlertOnce(true);
                //.setDefaults(NotificationCompat.DEFAULT_ALL);

        NotificationManagerCompat.from(context).notify(notificationID, builder.build());
        mediaPlayer = MediaPlayer.create(context, Settings.System.DEFAULT_ALARM_ALERT_URI);
        mediaPlayer.start();
    }
}
