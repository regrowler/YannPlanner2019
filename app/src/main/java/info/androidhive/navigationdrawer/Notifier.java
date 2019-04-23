package info.androidhive.navigationdrawer;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import info.androidhive.navigationdrawer.other.Repository;
import info.androidhive.navigationdrawer.other.Task;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class Notifier extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    public static final String ACTION_MYINTENTSERVICE = "ru.ikosmov.intentservice.RESPONSE";
    private static final String ACTION_FOO = "info.androidhive.navigationdrawer.action.FOO";
    private static final String ACTION_BAZ = "info.androidhive.navigationdrawer.action.BAZ";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "info.androidhive.navigationdrawer.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "info.androidhive.navigationdrawer.extra.PARAM2";

    public Notifier() {
        super("Notifier");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionFoo(Context context, String param1, String param2) {
        Intent intent = new Intent(context, Notifier.class);
        intent.setAction(ACTION_FOO);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, Notifier.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final int comma=intent.getIntExtra("comma",-1);
            if(comma==1) {
                final long param1 = intent.getLongExtra("time", 0);
                final int param2 = intent.getIntExtra("notid",0);
                final int id=intent.getIntExtra("id",0);
                final String task=intent.getStringExtra("task");
                handleActionFoo(param1, param2,id,task);
            }else if(comma==2){
                final long param1 = intent.getLongExtra("time", 0);
                final int param2 = intent.getIntExtra("notid",0);
                NotificationManager manager=(NotificationManager) getApplication().getSystemService(NOTIFICATION_SERVICE);
                manager.cancel(param2);
            }
            else if(comma==3){
                final long param1 = intent.getLongExtra("time", 0);
                final int param2 = intent.getIntExtra("notid",0);
                final int id=intent.getIntExtra("id",0);
                NotificationManager manager=(NotificationManager) getApplication().getSystemService(NOTIFICATION_SERVICE);

//                Repository.offTask(id,getApplicationContext());
                try {
                    SQLiteDatabase database = getApplicationContext().openOrCreateDatabase("app.db", Context.MODE_PRIVATE, null);
                    database.execSQL("CREATE TABLE IF NOT EXISTS tasks (" +
                            "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "year INTEGER," +
                            "month INTEGER," +
                            "day INTEGER," +
                            "noti INTEGER," +
                            "en INTEGER," +
                            "task TEXT);");
                    ContentValues values=new ContentValues();
                    values.put("noti",0);
                    database.update("tasks",values,"ID=?",new String[]{id+""});
                    Cursor q = database.rawQuery("SELECT * FROM tasks;", new String[]{});
                    if (q.moveToFirst()) {
                        do {
                            Task task = new Task();
                            task.year = q.getInt(1);
                            task.month = q.getInt(2);
                            task.day = q.getInt(3);
                            task.task = q.getString(6);
                            int u = q.getInt(4);
                            if (u == 1) {
                                task.notification = true;
                            } else task.notification = false;
                            u = q.getInt(5);
                            if (u == 1) task.important = true;
                            else task.important = false;
                            task.id = q.getInt(0);
//                        tasks.add(task);
//                        if (task.important) importantTasks.add(task);
//                        if (task.notification) notificationTasks.add(task);
                        } while (q.moveToNext());
                    }
                }catch (Exception e){
                    Log.d("data",e.toString());
                }

                Intent responseIntent = new Intent();
                manager.cancel(param2);
                responseIntent.setAction(ACTION_MYINTENTSERVICE);
                responseIntent.addCategory(Intent.CATEGORY_DEFAULT);
//                responseIntent.putExtra(EXTRA_KEY_OUT, extraOut);
                sendBroadcast(responseIntent);
            }


        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    @RequiresApi(20)
    private void handleActionFoo(long param1, int param2,int id,String task) {
        // TODO: Handle action Foo
        Intent deleteIntent = new Intent(this, Notifier.class);
        deleteIntent.putExtra("comma",3);
        deleteIntent.putExtra("id",id);
        deleteIntent.putExtra("notid",param2);
//        deleteIntent.setAction("ru.startandroid.notifications.action_delete");
        PendingIntent deletePendingIntent = PendingIntent.getService(this, 0, deleteIntent,  PendingIntent.FLAG_UPDATE_CURRENT);
//        deleteIntent.putExtra("comma",0);
        Notification.Builder builder = new Notification.Builder(this).setContentTitle("напоминание")
                .setContentText(task).setSmallIcon(R.drawable.ic_notifications_black_24dp).addAction(new
                        Notification.Action(R.drawable.ic_notifications_black_24dp,"Ок",deletePendingIntent));
        Notification notification;
        if (Build.VERSION.SDK_INT < 16)
            notification = builder.getNotification();
        else
            notification = builder.build();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startMyOwnForeground(param2,id,task);
        else {
            NotificationManager manager=(NotificationManager)getApplication().getSystemService(NOTIFICATION_SERVICE);
            manager.notify(param2,notification);
        }
//            startForeground(1, notification);
//        throw new UnsupportedOperationException("Not yet implemented");
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private void startMyOwnForeground(int param2,int id,String task){
        String NOTIFICATION_CHANNEL_ID = "com.e.yan.resttest";
        String channelName = "maService";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);
        Intent deleteIntent = new Intent(this, Notifier.class);
//        deleteIntent.setAction("ru.startandroid.notifications.action_delete");
        deleteIntent.putExtra("id",id);
        deleteIntent.putExtra("comma",3);
        deleteIntent.putExtra("notid",param2);
        PendingIntent deletePendingIntent = PendingIntent.getService(this, 0, deleteIntent, 0);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder
                .setContentTitle("Напоминание")
                .setSubText(task)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .addAction(new NotificationCompat.Action(R.drawable.ic_notifications_black_24dp,"Ок",deletePendingIntent))
                .build();
//        NotificationManager manager1=(NotificationManager) getApplication().getSystemService(NOTIFICATION_SERVICE);
        manager.notify(param2,notification);
//        notification.notify();
    }


    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(long param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
