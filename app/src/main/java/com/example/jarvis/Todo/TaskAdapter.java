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

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TaskViewHolder(LayoutInflater.from(context).inflate(R.layout.custom_todo_item , parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder taskViewHolder, int position) {
        taskViewHolder.title.setText(todoDetails.get(position).getTitle());
        taskViewHolder.desc.setText(todoDetails.get(position).getDescription());
        taskViewHolder.date.setText(todoDetails.get(position).getDate().toString());
    }

    @Override
    public int getItemCount() {
        return todoDetails.size();
    }

    class TaskViewHolder extends RecyclerView.ViewHolder{

        TextView title, desc, date;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.todo_item_title);
            desc = (TextView) itemView.findViewById(R.id.todo_item_desc);
            date = (TextView) itemView.findViewById(R.id.todo_item_date);
        }
    }
}
