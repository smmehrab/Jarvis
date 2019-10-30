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

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder>{

    Context context;
    ArrayList<Alarm> alarms;

    public ArrayList<Alarm> getAlarms() {
        return alarms;
    }

    public AlarmAdapter(Context context, ArrayList<Alarm> alarms){
        this.context = context;
        this.alarms = alarms;
    }

    public void setAlarms(ArrayList<Alarm> alarms) {
        this.alarms = alarms;
    }

    @NonNull
    @Override
    public AlarmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return  new AlarmAdapter.AlarmViewHolder(LayoutInflater.from(context).inflate(R.layout.custom_alarm_item , parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmViewHolder holder, int position) {
        // Set Time
        String hour = alarms.get(position).getHour();
        String minute = alarms.get(position).getMinute();
        String amPm = " AM";

        if(Integer.parseInt(hour)>12){
            hour = Integer.toString(Integer.parseInt(hour)%12);
            amPm = " PM";
        }

        holder.time.setText(hour+":"+minute+amPm);


        // Set isEveryday
        if(alarms.get(position).getIsEveryday()==1) {
            holder.isEveryday.setVisibility(View.VISIBLE);
            holder.statusGap.setVisibility(View.VISIBLE);
            holder.isEveryday.setText("Everyday");

        } else {
            holder.isEveryday.setVisibility(View.INVISIBLE);
            holder.statusGap.setVisibility(View.INVISIBLE);
        }

        // Set Status Text & Switch
        if(alarms.get(position).getStatus()==1) {
            holder.status.setText("On");
            holder.statusSwitch.setChecked(true);
        } else {
            holder.status.setText("Off");
            holder.statusSwitch.setChecked(false);
        }
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class AlarmViewHolder extends RecyclerView.ViewHolder {
        TextView time, isEveryday, status;
        View statusGap;
        Switch statusSwitch;

        public AlarmViewHolder(@NonNull View itemView) {
            super(itemView);
            time = (TextView) itemView.findViewById(R.id.alarm_item_time);
            isEveryday = (TextView) itemView.findViewById(R.id.alarm_item_is_everyday);
            statusGap = (View) itemView.findViewById(R.id.alarm_item_status_gap);
            status = (TextView) itemView.findViewById(R.id.alarm_item_status);
            statusSwitch = (Switch) itemView.findViewById(R.id.alarm_item_switch);
        }
    }
}
