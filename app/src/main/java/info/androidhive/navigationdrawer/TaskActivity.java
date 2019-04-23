package info.androidhive.navigationdrawer;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.github.sundeepk.compactcalendarview.domain.Event;

import java.util.Calendar;
import java.util.GregorianCalendar;

import info.androidhive.navigationdrawer.other.Repository;
import info.androidhive.navigationdrawer.other.Task;

public class TaskActivity extends AppCompatActivity {
    boolean mode =
            false;
    int id=-1;
    int year=0;
    int month=0;
    int day=0;
    String tasks="";

    boolean not=false;
    boolean imp=false;

    TextView data;
    Switch important;
    Switch notify;
    EditText task;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Задача");
        setContentView(R.layout.activity_task);
        Bundle bundle = getIntent().getExtras();
        task=findViewById(R.id.editText);
        data=findViewById(R.id.textView3);
        important=findViewById(R.id.switch1);
        notify=findViewById(R.id.switch2);
        if (bundle != null) {
            tasks = bundle.getString("task");
            if (tasks != null) {
                mode = true;
                year=bundle.getInt("year");
                month=bundle.getInt("month");
                day=bundle.getInt("day");
                not=bundle.getBoolean("not");
                imp=bundle.getBoolean("imp");
                id=bundle.getInt("id");
                data.setText(day+"."+month+"."+year);
                important.setChecked(imp);
                notify.setChecked(not);
                task.setText(tasks);
            }else {
                year=bundle.getInt("year");
                month=bundle.getInt("month");
                day=bundle.getInt("day");
                data.setText(day+"."+month+"."+year);
            }
        }
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mode){
                    Task task1=new Task();
                    task1.id=id;
                    task1.important=important.isChecked();
                    task1.notification=notify.isChecked();
                    task1.year=year;
                    task1.month=month;
                    task1.day=day;
                    task1.task=task.getText().toString();
                    if(task1.notification){
                        task1.notification=false;
                        Repository.toggleNotification(getApplicationContext(),task1);
                        task1.notification=true;

                    }
                    Repository.updateTask(getApplicationContext(),task1);
                }else {
                    Task task1=new Task();
                    task1.important=important.isChecked();
                    task1.notification=notify.isChecked();
                    task1.year=year;
                    task1.month=month;
                    task1.day=day;
                    task1.task=task.getText().toString();
                    Repository.add(getApplicationContext(),task1);
                    Task task2=Repository.tasks.get(Repository.tasks.size()-1);
                    if(task2.notification){
                        task2.notification=false;
                        Repository.toggleNotification(getApplicationContext(),task2);
                    }
                    int y=0;
                }
                Repository.update(getApplicationContext());
                GregorianCalendar calendar=new GregorianCalendar();
                calendar.set(Calendar.DAY_OF_MONTH,day);
                calendar.set(Calendar.MONTH,month);
                calendar.set(Calendar.YEAR,year);
                Repository.calendarView.removeEvent(new Event(Color.WHITE,calendar.getTimeInMillis()));
                Repository.calendarView.addEvent(new Event(Color.WHITE,calendar.getTimeInMillis()));
                finish();
            }
        });
    }
}
