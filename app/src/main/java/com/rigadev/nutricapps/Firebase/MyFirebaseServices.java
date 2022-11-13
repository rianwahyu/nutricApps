package com.rigadev.nutricapps.Firebase;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.rigadev.nutricapps.R;
import com.rigadev.nutricapps.SplashSceenActivity;


import java.util.Map;

public class MyFirebaseServices extends FirebaseMessagingService {

    private static final String TAG = "PushNotification";
    private static final String CHANNEL_ID ="101" ;

    private static final String TITLE = "title";
    private static final String EMPTY = "";
    private static final String MESSAGE = "message";
    private static final String IMAGE = "image";
    private static final String ACTION = "action";
    private static final String DATA = "data";
    private static final String ACTION_DESTINATION = "action_destination";
    private static final String TYPE = "type";
    private static final String STATUS = "status";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            Map<String, String> data = remoteMessage.getData();
            handleData(data);

        } else if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            showNotif(remoteMessage);
        }

        //showNotif(remoteMessage);
        super.onMessageReceived(remoteMessage);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    void showNotif(RemoteMessage remoteMessage){
        String title = remoteMessage.getNotification().getTitle();
        String text = remoteMessage.getNotification().getBody();

        Intent intent = new Intent(this, SplashSceenActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        final String CHANNEL_ID = "HEADS_UP_NOTIFICATION";

        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Heads Up Notification",
                NotificationManager.IMPORTANCE_HIGH
        );

        getSystemService(NotificationManager.class).createNotificationChannel(channel);
        Notification.Builder notification =
                new Notification.Builder(this, CHANNEL_ID)
                        .setContentTitle(title)
                        .setContentText(text)
                        .setSmallIcon(R.mipmap.ic_launcher_nutric)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);

        NotificationManagerCompat.from(this).notify(1, notification.build());
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void handleData(Map<String, String> data) {
        String title = data.get(TITLE);
        String message = data.get(MESSAGE);
        String iconUrl = data.get(IMAGE);
        String action = data.get(ACTION);
        String actionDestination = data.get(ACTION_DESTINATION);
        String type = data.get(TYPE);
        String status = data.get(STATUS);

        NotificationModel notificationModelVO = new NotificationModel();
        notificationModelVO.setTitle(title);
        notificationModelVO.setMessage(message);
        notificationModelVO.setIconUrl(iconUrl);
        notificationModelVO.setAction(action);
        notificationModelVO.setActionDestination(actionDestination);
        notificationModelVO.setType(type);
        notificationModelVO.setStatus(status);
        notificationModelVO.setStatus(status);

        Intent resultIntent = new Intent(getApplicationContext(), SplashSceenActivity.class);
        resultIntent.putExtra("id", "");
        resultIntent.putExtra("title", title);
        resultIntent.putExtra("message", message);;
        resultIntent.putExtra("type", type);
        resultIntent.putExtra("action", action);
        resultIntent.putExtra("status", status);
        resultIntent.putExtra("from", "server");
        resultIntent.putExtra("destination", "clicked");

        NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
        notificationUtils.displayNotification(notificationModelVO, resultIntent);
        //notificationUtils.playNotificationSound();

    }
}
