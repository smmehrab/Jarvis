package com.example.jarvis.Reminder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jarvis.R;

import java.util.ArrayList;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder> {

    Context context;
    ArrayList<Alarm> alarms;

    public ArrayList<Alarm> getTasks() {
        return alarms;
    }

    public AlarmAdapter(Context context, ArrayList<Alarm> alarms){
        this.context = context;
        this.alarms = alarms;
    }

    public void setTasks(ArrayList<Alarm> alarms) {
        this.alarms = alarms;
    }

    @NonNull
    @Override
    public AlarmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AlarmViewHolder(LayoutInflater.from(context).inflate(R.layout.custom_alarm_item , parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmViewHolder holder, int position) {
        // Set Time
        String time = "null";
        if(alarms.get(position).getHour()!=null && alarms.get(position).getMinute()!=null) {
            Integer hour = Integer.parseInt(alarms.get(position).getHour());
            String amPm = " AM";
            if (hour >= 12) {
                amPm = " PM";
                hour = hour - 12;
            }
            time = hour.toString() + ":" + alarms.get(position).getMinute() + amPm;
        }
        holder.time.setText(time);


        // Set isEveryday & StatusGap
        if(alarms.get(position).getIsEveryday()==1){
            holder.isEveryday.setVisibility(View.VISIBLE);
            holder.statusGap.setVisibility(View.VISIBLE);
        } else{
            holder.isEveryday.setVisibility(View.GONE);
            holder.statusGap.setVisibility(View.GONE);
        }

        // Set Status
        if(alarms.get(position).getStatus()==1){
            holder.aSwitch.setChecked(true);
            holder.status.setText("On");
        } else{
            holder.aSwitch.setChecked(false);
            holder.status.setText("Off");
        }
    }

    @Override
    public int getItemCount() {
        return alarms.size();
    }

    class AlarmViewHolder extends RecyclerView.ViewHolder{

        TextView time, isEveryday, status;
        View statusGap;
        Switch aSwitch;

        public AlarmViewHolder(@NonNull View itemView) {
            super(itemView);
            time = (TextView) itemView.findViewById(R.id.alarm_item_time);
            isEveryday = (TextView) itemView.findViewById(R.id.alarm_item_is_everyday);
            status = (TextView) itemView.findViewById(R.id.alarm_item_status);
            statusGap = (View) itemView.findViewById(R.id.alarm_item_status_gap);

            aSwitch = (Switch) itemView.findViewById(R.id.alarm_item_switch);
        }
    }
}
