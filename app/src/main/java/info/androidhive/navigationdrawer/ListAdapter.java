package info.androidhive.navigationdrawer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

import info.androidhive.navigationdrawer.other.Repository;
import info.androidhive.navigationdrawer.other.Task;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewH> {
    List<Task> mas;
    Context context;
    LayoutInflater inflater;

    public ListAdapter(Context context, List<Task> mas) {
        this.mas = mas;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.item, viewGroup, false);
        return new ViewH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewH viewH, final int i) {
        final Task task = mas.get(i);
        viewH.checkBox.setChecked(task.important);
        viewH.aSwitch.setChecked(task.notification);
        viewH.textView.setText(task.day + "." + task.month + "." + task.year + "  " + task.task);
//        viewH.aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                Repository.toggleNotification(context, mas.get(i));
//                mas.get(i).notification = isChecked;
//
//                Repository.updateTask(context, mas.get(i));
//            }
//        });
        viewH.aSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Repository.toggleNotification(context, mas.get(i));
                boolean check=viewH.aSwitch.isChecked();
                mas.get(i).notification = check;
//
                Repository.updateTask(context, mas.get(i));
            }
        });
        viewH.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mas.get(i).important = isChecked;
                Repository.updateTask(context, mas.get(i));
            }
        });
        viewH.g.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, TaskActivity.class);
                intent.putExtra("year", task.year);
                intent.putExtra("month", task.month);
                intent.putExtra("day", task.day);
                intent.putExtra("task", task.task);
                intent.putExtra("not", task.notification);
                intent.putExtra("imp", task.important);
                intent.putExtra("id", task.id);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
        viewH.g.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showPopupMenu(viewH.g, i);
                return true;
            }
        });

    }

    private void showPopupMenu(View v, final int pos) {
        PopupMenu popupMenu = new PopupMenu(context, v, Gravity.TOP);
        popupMenu.inflate(R.menu.popupmenu);

        popupMenu
                .setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.bottomPopupClear:
                                Repository.deleteTask(context, mas.get(pos));
//                                notifyDataSetChanged();
                                return true;
                            default:
                                return false;
                        }
                    }
                });
        popupMenu.show();
    }

    @Override
    public int getItemCount() {
        return mas.size();
    }

    public class ViewH extends RecyclerView.ViewHolder {
        public CheckBox checkBox;
        public Switch aSwitch;
        public TextView textView;
        public View g;

        public ViewH(@NonNull View itemView) {
            super(itemView);
            g = itemView.findViewById(R.id.gen);
            checkBox = itemView.findViewById(R.id.checkBox);
            aSwitch = itemView.findViewById(R.id.switch3);
            textView = itemView.findViewById(R.id.tvText);
        }
    }
}
