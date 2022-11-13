package com.rigadev.nutricapps.Firebase;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


import com.rigadev.nutricapps.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class NotificationUtils {
    public static final int NOTIFICATION_ID = 200;
    public static final String PUSH_NOTIFICATION = "pushNotification";
    public static final String CHANNEL_ID = "HEADS_UP_NOTIFICATION";
    public static final String CHANNEL_NAME = "Heads Up Notification";

    public static final String URL = "url";
    public static final String ACTIVITY = "activity";
    Map<String, Class> activityMap = new HashMap<>();
    private Context mContext;

    public NotificationUtils(Context mContext) {
        this.mContext = mContext;
        //Populate activity map
        /*activityMap.put("Home", MainActivity.class);
        activityMap.put("merchant", RiwayatPesananActivity.class);
        activityMap.put("kasir", HomeKasirActivity.class);*/
    }

    PendingIntent resultPendingIntent;


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void displayNotification(NotificationModel notificationVO, Intent resultIntent) {
        {
            String message = notificationVO.getMessage();
            String title = notificationVO.getTitle();
            String iconUrl = notificationVO.getIconUrl();
            String action = notificationVO.getAction();
            String destination = notificationVO.getActionDestination();
            String type = notificationVO.getType();
            String status = notificationVO.getStatus();

            Bitmap iconBitMap = null;
            if (iconUrl != null) {
                iconBitMap = getBitmapFromURL(iconUrl);
            }
            final int icon = R.mipmap.ic_launcher_nutric;


            if (URL.equals(action)) {
                /*Intent notificationIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(destination));
                notificationIntent.putExtra("status", status);
                notificationIntent.putExtra("type", type);
                notificationIntent.putExtra("title", title);
                notificationIntent.putExtra("from", "server");
                resultPendingIntent = PendingIntent.getActivity(mContext, 0, notificationIntent, 0);*/
            } else if (ACTIVITY.equals(action) && activityMap.containsKey(destination)) {
                resultIntent = new Intent(mContext, activityMap.get(destination));
                resultIntent.putExtra("status", status);
                resultIntent.putExtra("type", type);
                resultIntent.putExtra("nama", title);
                resultIntent.putExtra("from", "server");
                resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                resultPendingIntent = PendingIntent.getActivity(
                                mContext,
                                0,
                                resultIntent,
                                0);
            } else {
                resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                resultPendingIntent = PendingIntent.getActivity(
                                mContext,
                                0,
                                resultIntent,
                                0);
            }


            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Heads Up Notification",
                    NotificationManager.IMPORTANCE_HIGH
            );

            mContext.getSystemService(NotificationManager.class).createNotificationChannel(channel);


            final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                    mContext, CHANNEL_ID);

//            Notification notification;

            if (iconBitMap == null) {

               /* NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
                inboxStyle.addLine(message);
                notification = mBuilder.setSmallIcon(icon).setTicker(title).setWhen(System.currentTimeMillis())
                        .setAutoCancel(true)
                        .setContentTitle(title)
                        .setContentIntent(resultPendingIntent)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(message))
                        .setSmallIcon(R.mipmap.ic_launcher_fc)
                        .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon))
                        .setContentText(message)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setPriority(NotificationCompat.PRIORITY_MAX)

                        .build();*/

                Notification.Builder notification =
                        new Notification.Builder(mContext, CHANNEL_ID)
                                .setContentTitle(title)
                                .setContentText(message)
                                .setSmallIcon(R.mipmap.ic_launcher_nutric)
                                .setContentIntent(resultPendingIntent)
                                .setAutoCancel(true);
                NotificationManagerCompat.from(mContext).notify(1, notification.build());


            } else {

                Notification.Builder notification =
                        new Notification.Builder(mContext, CHANNEL_ID)
                                .setContentTitle(title)
                                .setContentText(message)
                                .setSmallIcon(R.mipmap.ic_launcher_nutric)
                                .setContentIntent(resultPendingIntent)
                                .setAutoCancel(true);
                NotificationManagerCompat.from(mContext).notify(1, notification.build());


                /*NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
                bigPictureStyle.setBigContentTitle(title);
                bigPictureStyle.setSummaryText(Html.fromHtml(message).toString());
                bigPictureStyle.bigPicture(iconBitMap);
                notification = mBuilder.setSmallIcon(icon).setTicker(title).setWhen(System.currentTimeMillis())
                        .setAutoCancel(true)
                        .setContentTitle(title)
                        .setContentIntent(resultPendingIntent)
                        *//*.setStyle(bigPictureStyle)*//*
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(message))
                        .setSmallIcon(R.mipmap.ic_launcher_fc)
                        .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon))
                        .setContentText(message)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .build();*/
            }

        }
    }

    private Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Playing notification sound
     */
    public void playNotificationSound() {
        try {
            Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + mContext.getPackageName() + "/raw/text_notification");
            Ringtone r = RingtoneManager.getRingtone(mContext, alarmSound);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
