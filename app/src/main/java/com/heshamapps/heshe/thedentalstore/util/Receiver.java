package com.heshamapps.heshe.thedentalstore.util;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.legacy.content.WakefulBroadcastReceiver;

import com.heshamapps.heshe.thedentalstore.MainActivity;
import com.heshamapps.heshe.thedentalstore.R;

import static android.content.Context.NOTIFICATION_SERVICE;
import static androidx.legacy.content.WakefulBroadcastReceiver.startWakefulService;

public class Receiver extends BroadcastReceiver {
    public static final int REQUEST_CODE = 12345;
    public static Ringtone ringtone;
    @Override
    public void onReceive(final Context context, Intent intent) {


        // using service class
        Intent i = new Intent(context, AlarmService.class);
        context.startService(i);

        createNotification(context);

    }

    public void createNotification(Context context) {


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentTitle("Product Has beed expired")
                .setContentText("Online Dental Store")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setSubText("Tab to cancel the ringtone")
                .setPriority(NotificationCompat.PRIORITY_HIGH);


        //To add a dismiss button
        Intent dismissIntent = new Intent(context, AlarmService.class);
        dismissIntent.setAction(AlarmService.ACTION_DISMISS);

        PendingIntent pendingIntent = PendingIntent.getService(context,
                123, dismissIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Action action = new NotificationCompat.Action
                (android.R.drawable.ic_lock_idle_alarm, "DISMISS", pendingIntent);
        builder.addAction(action);
        // end of setting action button to notification


        Intent intent1 = new Intent(context, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(context, 123, intent1,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pIntent);


        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(NOTIFICATION_SERVICE);
        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(123, notification);

    }
    }
