package com.example.yahia.todoreminer;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.UserHandle;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.net.URI;
import java.util.Calendar;


public class MyReceiver extends BroadcastReceiver {
    public static String[] todotext;
    @Override
    public void onReceive(Context context, Intent intent) {
        Calendar calendar = Calendar.getInstance();
        Long currentTime = calendar.getTimeInMillis();
        String aaa = intent.getStringExtra("location_adress");
        String bbb = intent.getStringExtra("todo_discrip");
        String trans = intent.getStringExtra("trans");
        String ccc = intent.getStringExtra("list_name");
        RemoteViews myRemoteView = new RemoteViews(context.getPackageName(), R.layout.my_notification_layout);

        myRemoteView.setTextViewText(R.id.status_text, aaa);
        myRemoteView.setTextViewText(R.id.details_text, bbb);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        Uri ring = Uri.parse("android.resource://com.example.yahia.todoreminer/raw/alarmtone");
        mBuilder.setSmallIcon(R.drawable.logo)
                .setContent(myRemoteView)
                .setWhen(System.currentTimeMillis())
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000})
                .setSound(ring)
                .setContentIntent(PendingIntent.getActivity(context, 0, new Intent(), 0))
                .setAutoCancel(true);



        String svc = Context.NOTIFICATION_SERVICE;
        NotificationManager notificationManager
                = (NotificationManager) context.getSystemService(svc);

        int NOTIFICATION_REF = 1;
        if (trans != null) {
            todotext = trans.split("---");

            if((todotext.length == 6))
            {Intent u = new Intent(context, MapsActivity.class);
                u.putExtra("trans", trans);
                myRemoteView.setImageViewResource(R.id.notifImage, R.drawable.map_icon);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, Integer.parseInt(todotext[5]), u, 0);
                mBuilder.setTicker(bbb);
                mBuilder.setContentIntent(pendingIntent);
                Notification notification = mBuilder.build();
                notification.flags |= Notification.FLAG_AUTO_CANCEL;
                notification.contentView = myRemoteView;


                notificationManager.notify(NOTIFICATION_REF, notification);
                }
            else if (todotext.length > 6 && currentTime >= Long.decode(todotext[6]) && currentTime <= Long.decode(todotext[7]))
            {Intent u = new Intent(context, MapsActivity.class);
                u.putExtra("trans", trans);
                myRemoteView.setImageViewResource(R.id.notifImage, R.drawable.map_icon);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, Integer.parseInt(todotext[5]), u, 0);
                mBuilder.setTicker(bbb);
                mBuilder.setContentIntent(pendingIntent);
                Notification notification = mBuilder.build();
                notification.contentView = myRemoteView;
                notificationManager.notify(NOTIFICATION_REF, notification);}

        } else {
            Intent u = new Intent(context, Item_Details.class);
            myRemoteView.setImageViewResource(R.id.notifImage, R.drawable.date_icon);
            FreeTimeToDo.selectedItem = aaa;
            FreeTimeToDo.chosenList = ccc;
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, u, 0);
            mBuilder.setTicker(aaa);
            mBuilder.setContentIntent(pendingIntent);
            Notification notification = mBuilder.build();

            notification.contentView = myRemoteView;
            notificationManager.notify(NOTIFICATION_REF, notification);
        }






    }}


