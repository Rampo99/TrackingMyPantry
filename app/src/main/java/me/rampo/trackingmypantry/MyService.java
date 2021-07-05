package me.rampo.trackingmypantry;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class MyService extends Service {
    Timer timer;
    TimerTask timerTask;
    int seconds = 86400;
    Context context;
    DBHelper db;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        context = this;
        db = new DBHelper(this);
        createtimer();
        return START_STICKY;
    }
    private void createtimer(){
        timer = new Timer();

        initializeTimerTask();
        timer.schedule(timerTask,seconds,seconds);

    }

    public void initializeTimerTask() {

        timerTask = new TimerTask() {
            public void run() {
                //use a handler to run a toast that shows the current timestamp
                List<PantryProduct> products = db.getProducts();
                Date today = Calendar.getInstance().getTime();
                String notificationtext = "";
                String title = "";
                for(PantryProduct p : products){
                    Date date = p.getDate();
                    if(date != null ) {
                        if (today.after(date)) {
                            title = "Scaduti!!";
                            notificationtext = "Alcuni tuoi prodotti sono scaduti!";
                            break;
                        }
                        long diffInMillies = Math.abs(today.getTime() - date.getTime());
                        long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
                        if(diff <= 7) {
                            notificationtext = "Alcuni tuoi prodotti stanno per scadere!";
                            break;
                        }
                    }
                }
                if(!notificationtext.equals("")) {
                    //send notification
                    Intent i = new Intent();
                    showNotification(context,title,notificationtext,i,1);
                }
            }
        };
    }

    public void showNotification(Context context, String title, String message, Intent intent, int reqCode) {
        PendingIntent pendingIntent = PendingIntent.getActivity(context, reqCode, intent, PendingIntent.FLAG_ONE_SHOT);
        String CHANNEL_ID = "TrackingMyPantry";
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "MyPantry";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            notificationManager.createNotificationChannel(mChannel);
        }
        startForeground(reqCode,notificationBuilder.build());

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
