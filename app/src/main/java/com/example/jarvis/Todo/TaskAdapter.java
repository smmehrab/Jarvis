package com.example.jarvis.Todo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jarvis.R;

import java.util.ArrayList;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    Context context;
    ArrayList<Task> tasks;

    public ArrayList<Task> getTasks() {
        return tasks;
    }

    public TaskAdapter(Context context, ArrayList<Task> tasks){
        this.context = context;
        this.tasks = tasks;
    }

    public void setTasks(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TaskViewHolder(LayoutInflater.from(context).inflate(R.layout.custom_todo_item , parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder taskViewHolder, int position) {
        taskViewHolder.title.setText(tasks.get(position).getTitle());
        taskViewHolder.description.setText(tasks.get(position).getDescription());

        String date = null;
        String[] month = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        date = tasks.get(position).getDay() + " " +
                month[Integer.parseInt(tasks.get(position).getMonth())] + ", " +
                tasks.get(position).getYear();
        taskViewHolder.date.setText(date);

        String time = null;
        if(tasks.get(position).getHour()!=null && tasks.get(position).getMinute()!=null) {
            Integer hour = Integer.parseInt(tasks.get(position).getHour());
            String amPm = " AM";
            if (hour >= 12) {
                amPm = " PM";
                hour = hour - 12;
            }
            time = hour.toString() + ":" + tasks.get(position).getMinute() + amPm;
        }
        taskViewHolder.time.setText(time);

        taskViewHolder.checkBox.setOnCheckedChangeListener(null);

        if(tasks.get(position).getIsCompleted() == 0)
            taskViewHolder.checkBox.setChecked(false);
        else
            taskViewHolder.checkBox.setChecked(true);

//        taskViewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if(isChecked) {
//                    tasks.get(taskViewHolder.getAdapterPosition()).setIsCompleted(1);
//                }
//                else {
//                    tasks.get(taskViewHolder.getAdapterPosition()).setIsCompleted(0);
//                }
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    class TaskViewHolder extends RecyclerView.ViewHolder{

        TextView title, description, date, time;
        CheckBox checkBox;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.todo_item_title);
            description = (TextView) itemView.findViewById(R.id.todo_item_description);
            date = (TextView) itemView.findViewById(R.id.todo_item_date);
            time = (TextView) itemView.findViewById(R.id.todo_item_time);
            checkBox = (CheckBox) itemView.findViewById(R.id.todo_item_checkbox);
        }
    }
}
