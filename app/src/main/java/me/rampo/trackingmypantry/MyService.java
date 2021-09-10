package me.rampo.trackingmypantry;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleService;
import androidx.room.Room;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class MyService extends Service {
    Timer timer;
    TimerTask timerTask;
    int minute = 1000*60;
    int day = minute*60*24;
    Context context;
    SharedPreferences pref;
    AppDatabase db;
    ProductDao products;
    @Nullable
    @Override
    public IBinder onBind(@NotNull Intent intent) {
        return null;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        context = this;
        pref = getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "pantry-db").allowMainThreadQueries().build();
        products = db.productDao();
        createtimer();
        return START_STICKY;
    }
    private void createtimer(){
        timer = new Timer();

        initializeTimerTask();
        timer.schedule(timerTask,day,day);

    }

    public void initializeTimerTask() {

        timerTask = new TimerTask() {
            public void run() {

                Set<String> categories = pref.getStringSet("categories",null);
                int categorycount = 0;

                if(categories != null){
                    for(String s: categories){
                        categorycount = products.getByCategory(s);
                    }
                    if(categorycount > 0){
                        Intent i = new Intent();
                        showNotification(context,"Importante!","Alcuni prodotti nelle tue categorie importanti sono finiti o stanno per finire!!",i,2);
                    }
                }

                Date today = Calendar.getInstance().getTime();
                long DAY_IN_MS = 1000 * 60 * 60 * 24;
                Date oneweekmore = new Date(today.getTime() + (7 * DAY_IN_MS));
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                String strDate = dateFormat.format(oneweekmore);
                int datecount = products.getByDate(strDate);

                if(datecount > 0){
                    Intent i = new Intent();
                    showNotification(context,"Attenzione!","Alcuni tuoi prodotti sono scaduti o stanno per scadere!",i,1);
                } else {
                    int i = 0;
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
