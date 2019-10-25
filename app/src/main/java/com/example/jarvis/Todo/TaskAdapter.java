package com.example.jarvis.Todo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jarvis.R;

import java.util.ArrayList;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    Context context;
    ArrayList<Task> tasks;

    public TaskAdapter(Context context, ArrayList<Task> tasks){
        this.context = context;
        this.tasks = tasks;
    }

    public void setTodoDetails(ArrayList<Task> tasks) {
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
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    class TaskViewHolder extends RecyclerView.ViewHolder{

        TextView title, description, date, time;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.todo_item_title);
            description = (TextView) itemView.findViewById(R.id.todo_item_description);
            date = (TextView) itemView.findViewById(R.id.todo_item_date);
            time = (TextView) itemView.findViewById(R.id.todo_item_time);
        }
    }
}
