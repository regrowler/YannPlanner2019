package info.androidhive.navigationdrawer.other;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import info.androidhive.navigationdrawer.ListAdapter;
import info.androidhive.navigationdrawer.Notifier;
import info.androidhive.navigationdrawer.fragment.HomeFragment;

public class Repository {
    public static List<Task> tasks;
    public static ListAdapter dealsAdapter;

    public static List<Task> importantTasks;
    public static ListAdapter importantAdapter;

    public static List<Task> notificationTasks;
    public static ListAdapter notificationAdapter;

    public static RecyclerView taskView;
    public static RecyclerView notificationView;
    public static RecyclerView importantView;

    public static HomeFragment homeFragment;

    public static CompactCalendarView calendarView;

    public static void loadCalendar() {
        for (int i = 0; i < tasks.size(); i++) {
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.set(Calendar.YEAR, tasks.get(i).year);
            calendar.set(Calendar.DAY_OF_MONTH, tasks.get(i).day);
            calendar.set(Calendar.MONTH, tasks.get(i).month);
            calendarView.addEvent(new Event(Color.WHITE, calendar.getTimeInMillis()));
        }
    }
    public static void offTask(int id,Context context){
        SQLiteDatabase database = context.openOrCreateDatabase("app.db", Context.MODE_PRIVATE, null);
        database.execSQL("CREATE TABLE IF NOT EXISTS tasks (" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "year INTEGER," +
                "month INTEGER," +
                "day INTEGER," +
                "noti INTEGER," +
                "en INTEGER," +
                "task TEXT);");
        ContentValues values=new ContentValues();
        values.put("en",0);
        database.update("tasks",values,"ID=?",new String[]{id+""});
    }
    public static void toggleNotification(Context context, Task task) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.set(Calendar.YEAR, task.year);
        calendar.set(Calendar.MONTH, task.month);
        calendar.set(Calendar.DAY_OF_MONTH, task.day);
        calendar.set(Calendar.HOUR, 12);
        Intent intent = new Intent(context, Notifier.class);
        if (task.notification) {
            intent.putExtra("comma", 2);
            intent.putExtra("notid", (task.year % 10) + task.day * 1000 + task.month * 10);
            intent.putExtra("time", calendar.getTimeInMillis());
            intent.putExtra("task",task.task);
            intent.putExtra("id", task.id);
        } else {
            intent.putExtra("comma", 1);
            intent.putExtra("notid", (task.year % 10) + task.day * 1000 + task.month * 10);
            intent.putExtra("time", calendar.getTimeInMillis());
            intent.putExtra("id", task.id);
            intent.putExtra("task",task.task);
        }
        context.startService(intent);
    }


    public static void init(Context context) {
        tasks = new ArrayList<>();
        importantTasks = new ArrayList<>();
        notificationTasks = new ArrayList<>();
        SQLiteDatabase database = context.openOrCreateDatabase("app.db", Context.MODE_PRIVATE, null);
        database.execSQL("CREATE TABLE IF NOT EXISTS tasks (" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "year INTEGER," +
                "month INTEGER," +
                "day INTEGER," +
                "noti INTEGER," +
                "en INTEGER," +
                "task TEXT);");
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
                tasks.add(task);
                if (task.important) importantTasks.add(task);
                if (task.notification) notificationTasks.add(task);
            } while (q.moveToNext());
        }
        if(taskView!=null){
            taskView.post(new Runnable()
            {
                @Override
                public void run() {
                    dealsAdapter.notifyDataSetChanged();
                }
            });
        }else {
            dealsAdapter = new ListAdapter(context, tasks);
        }
        if(importantView!=null){
            importantView.post(new Runnable()
            {
                @Override
                public void run() {
                    importantAdapter.notifyDataSetChanged();
                }
            });
        }else {
            importantAdapter = new ListAdapter(context, importantTasks);
        }
        if(notificationView!=null){
            notificationView.post(new Runnable()
            {
                @Override
                public void run() {
                    notificationAdapter.notifyDataSetChanged();
                }
            });

        }else {
            notificationAdapter = new ListAdapter(context, notificationTasks);
        }

    }

    public static void deleteTask(Context context, Task task) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.set(Calendar.YEAR, task.year);
        calendar.set(Calendar.MONTH, task.month);
        calendar.set(Calendar.DAY_OF_MONTH, task.day);
        calendar.set(Calendar.HOUR, 12);
        SQLiteDatabase database = context.openOrCreateDatabase("app.db", Context.MODE_PRIVATE, null);
        database.execSQL("CREATE TABLE IF NOT EXISTS tasks (" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "year INTEGER," +
                "month INTEGER," +
                "day INTEGER," +
                "noti INTEGER," +
                "en INTEGER," +
                "task TEXT);");
        Intent intent = new Intent(context, Notifier.class);
        intent.putExtra("comma", 2);
        intent.putExtra("notid", (task.year % 10) + task.day * 1000 + task.month * 10);
        intent.putExtra("time", calendar.getTimeInMillis());
        intent.putExtra("task",task.task);
        intent.putExtra("id", task.id);
        context.startService(intent);
        database.delete("tasks", "ID=?", new String[]{task.id + ""});
//        database.rawQuery("DELETE FROM tasks WHERE ID=?;",new String[]{task.id+""});
        update(context);
    }

    public static void update(Context context) {
        if (tasks != null && importantAdapter != null &&notificationAdapter != null &&notificationTasks!=null&&importantTasks!=null){
            tasks.clear();
            importantTasks.clear();
            notificationTasks.clear();
            SQLiteDatabase database = context.openOrCreateDatabase("app.db", Context.MODE_PRIVATE, null);
            database.execSQL("CREATE TABLE IF NOT EXISTS tasks (" +
                    "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "year INTEGER," +
                    "month INTEGER," +
                    "day INTEGER," +
                    "noti INTEGER," +
                    "en INTEGER," +
                    "task TEXT);");
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
                    tasks.add(task);
                    if (task.important) importantTasks.add(task);
                    if (task.notification) notificationTasks.add(task);
                } while (q.moveToNext());
            }
            if(taskView!=null){
                taskView.post(new Runnable()
                {
                    @Override
                    public void run() {
                        dealsAdapter.notifyDataSetChanged();
                    }
                });
            }else {
                dealsAdapter = new ListAdapter(context, tasks);
            }
            if(importantView!=null){
                importantView.post(new Runnable()
                {
                    @Override
                    public void run() {
                        importantAdapter.notifyDataSetChanged();
                    }
                });
            }else {
                importantAdapter = new ListAdapter(context, importantTasks);
            }
            if(notificationView!=null){
                notificationView.post(new Runnable()
                {
                    @Override
                    public void run() {
                        notificationAdapter.notifyDataSetChanged();
                    }
                });

            }else {
                notificationAdapter = new ListAdapter(context, notificationTasks);
            }
        }

    }

    public static long add(Context context, Task task) {
        SQLiteDatabase database = context.openOrCreateDatabase("app.db", Context.MODE_PRIVATE, null);
        database.execSQL("CREATE TABLE IF NOT EXISTS tasks (" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "year INTEGER," +
                "month INTEGER," +
                "day INTEGER," +
                "noti INTEGER," +
                "en INTEGER," +
                "task TEXT);");
        ContentValues values = new ContentValues();
        values.put("year", task.year);
        values.put("month", task.month);
        values.put("day", task.day);
        if (task.notification) {
            values.put("noti", 1);
        } else {
            values.put("noti", 0);
        }
        if (task.important) {
            values.put("en", 1);
        } else {
            values.put("en", 0);
        }
        values.put("task", task.task);
        long y = database.insert("tasks", null, values);
        update(context);
        return y;
    }

    public static void updateTask(Context context, Task task) {
        SQLiteDatabase database = context.openOrCreateDatabase("app.db", Context.MODE_PRIVATE, null);
        database.execSQL("CREATE TABLE IF NOT EXISTS tasks (" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "year INTEGER," +
                "month INTEGER," +
                "day INTEGER," +
                "noti INTEGER," +
                "en INTEGER," +
                "task TEXT);");
        ContentValues values = new ContentValues();
        values.put("year", task.year);
        values.put("month", task.month);
        values.put("day", task.day);
        if (task.notification) {
            values.put("noti", 1);
        } else {
            values.put("noti", 0);
        }
        if (task.important) {
            values.put("en", 1);
        } else {
            values.put("en", 0);
        }
        values.put("task", task.task);
        database.update("tasks", values, "ID=?", new String[]{task.id + ""});
        update(context);
    }
}
