package com.example.jarvis.Reminder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jarvis.R;

import java.util.ArrayList;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    Context context;
    ArrayList<Event> events;

    public ArrayList<Event> getTasks() {
        return events;
    }

    public EventAdapter(Context context, ArrayList<Event> events){
        this.context = context;
        this.events = events;
    }

    public void setTasks(ArrayList<Event> events) {
        this.events = events;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new EventViewHolder(LayoutInflater.from(context).inflate(R.layout.custom_event_item , parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        // Set Title & Description
        holder.title.setText(events.get(position).getTitle());
        holder.description.setText(events.get(position).getDescription());

        // Set Date
        String date = "null";
        String[] month = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        date = events.get(position).getDay() + " " +
                month[Integer.parseInt(events.get(position).getMonth())] + ", " +
                events.get(position).getYear();
        holder.date.setText(date);

        // Set Time
        String time = "null";
        if(events.get(position).getHour()!=null && events.get(position).getMinute()!=null) {
            Integer hour = Integer.parseInt(events.get(position).getHour());
            String amPm = " AM";
            if (hour >= 12) {
                amPm = " PM";
                hour = hour - 12;
            }
            time = hour.toString() + ":" + events.get(position).getMinute() + amPm;
        }
        holder.time.setText(time);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    class EventViewHolder extends RecyclerView.ViewHolder{

        TextView title, description, type, date, time;
        LinearLayout descriptionGap;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.event_item_title);
            description = (TextView) itemView.findViewById(R.id.event_item_description);
            type = (TextView) itemView.findViewById(R.id.event_item_type);
            date = (TextView) itemView.findViewById(R.id.event_item_date);
            time = (TextView) itemView.findViewById(R.id.event_item_time);

            descriptionGap = (LinearLayout) itemView.findViewById(R.id.event_item_description_gap);
        }
    }
}
