package com.kelompok9.musicplayer.Notification;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.kelompok9.musicplayer.Activity.MainActivity;
import com.kelompok9.musicplayer.Model.AudioListModel;
import com.kelompok9.musicplayer.R;

import androidx.core.app.NotificationCompat;


public class NotificationGenerator {
    public static final String NOTIFY_PREVIOUS = "io.sample.musicism.previous";
    public static final String NOTIFY_NEXT = "io.sample.musicism.next";
    public static final String NOTIFY_PLAY = "io.sample.musicism.play";
    public static final String NOTIFY_PAUSE = "io.sample.musicism.pause";
    public static final String NOTIFY_DELETE = "io.sample.musicism.delete";


    private static final int N = 9;




    @SuppressLint("RestrictedApi")
    public static void customBigNotification(Context context, AudioListModel dataObject)
    {


        RemoteViews expandedView = new RemoteViews(context.getPackageName(), R.layout.music_notification);
        NotificationCompat.Builder nc = new NotificationCompat.Builder(context);
        NotificationManager nm = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

        Intent notifyingIntent = new Intent(context, MainActivity.class);
        notifyingIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notifyingIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        nc.setContentIntent(pendingIntent);
        nc.setSmallIcon(R.drawable.play_24dp);
        nc.setAutoCancel(true);
        nc.setCustomBigContentView(expandedView);
        nc.setContentTitle("Musicism");
        nc.setContentText("Control Audio");
        nc.getBigContentView().setTextViewText(R.id.song_name_notification,dataObject.getTrack());
        nc.getBigContentView().setImageViewUri(R.id.album_art_notification, dataObject.getAlbumArtUri());


        setListeners(expandedView, context);

        nm.notify(N, nc.build());

    }
    private static void setListeners(RemoteViews views, Context context)
    {
        Intent previous = new Intent(NOTIFY_PREVIOUS);
        Intent delete = new Intent(NOTIFY_DELETE);
        Intent play = new Intent(NOTIFY_PLAY);
        Intent pause = new Intent(NOTIFY_PAUSE);
        Intent next = new Intent(NOTIFY_NEXT);

        PendingIntent pPrevious = PendingIntent.getBroadcast(context, 0, previous, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.btn_previous, pPrevious);

        PendingIntent pNext = PendingIntent.getBroadcast(context, 0, next, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.btn_next, pNext);

        PendingIntent pDelete = PendingIntent.getBroadcast(context, 0, delete, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.delete_notification, pDelete);

        PendingIntent pPlay = PendingIntent.getBroadcast(context, 0, play, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.btn_play, pPlay);

        PendingIntent pPause = PendingIntent.getBroadcast(context, 0, pause, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.btn_pause, pPause);




    }
}
