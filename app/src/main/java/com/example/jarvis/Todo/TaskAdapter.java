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
    ArrayList<TodoDetails> todoDetails;

    public TaskAdapter(Context context, ArrayList<TodoDetails> todoDetails){
        this.context = context;
        this.todoDetails = todoDetails;
    }

    public void setTodoDetails(ArrayList<TodoDetails> todoDetails) {
        this.todoDetails = todoDetails;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TaskViewHolder(LayoutInflater.from(context).inflate(R.layout.custom_todo_item , parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder taskViewHolder, int position) {
        taskViewHolder.title.setText(todoDetails.get(position).getTitle());
        taskViewHolder.description.setText(todoDetails.get(position).getDescription());

        String date = null;
        String[] month = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        date = todoDetails.get(position).getDay() + " " +
                month[Integer.parseInt(todoDetails.get(position).getMonth())] + ", " +
                todoDetails.get(position).getYear();

        taskViewHolder.date.setText(date);

        String time = null;
        if(todoDetails.get(position).getHour()!=null && todoDetails.get(position).getMinute()!=null) {
            Integer hour = Integer.parseInt(todoDetails.get(position).getHour());
            String amPm = " AM";
            if (hour >= 12) {
                amPm = " PM";
                hour = hour - 12;
            }
            time = hour.toString() + ":" + todoDetails.get(position).getMinute() + amPm;
        }


        taskViewHolder.time.setText(time);
    }

    @Override
    public int getItemCount() {
        return todoDetails.size();
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
