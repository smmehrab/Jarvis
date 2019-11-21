package com.example.jarvis.Todo;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.Settings;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.jarvis.R;
import com.example.jarvis.Reminder.CancelAlarm;

import java.math.BigInteger;

public class todoAlertReceiver extends BroadcastReceiver {

    MediaPlayer mediaPlayer;
    private int todoNotificationId;

    @Override
    public void onReceive(Context context, Intent intent) {
        long[] pattern = {500,500,500,500,500,500,500,500,500};
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        todoNotificationId = (intent.getIntExtra("todoNotification", 0));

        Intent gotoTodo = new Intent(context, TodoActivity.class);
        gotoTodo.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, gotoTodo, PendingIntent.FLAG_ONE_SHOT);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "todoNotificationChannelId")
                .setSmallIcon(R.drawable.icon_activity_reminder)
                .setContentTitle("Jarvis")
                .setContentText("You have a pending task!!!")
                //.setColor(Color.rgb(100, (float) 64.8, 0))
                .setColor(Color.BLUE)
                //.setLights(Color.BLUE, 500, 500)
                .setVibrate(pattern);
                //.setStyle(new NotificationCompat.InboxStyle())
                //.setSound(alarmSound)
                //.setAutoCancel(true)
                //.setOnlyAlertOnce(true);
        //.setDefaults(NotificationCompat.DEFAULT_ALL);

        builder.setContentIntent(pendingIntent);
        NotificationManagerCompat.from(context).notify(todoNotificationId, builder.build());
        mediaPlayer = MediaPlayer.create(context, Settings.System.DEFAULT_NOTIFICATION_URI);
        mediaPlayer.start();

    }

}
