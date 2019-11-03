package com.example.jarvis.Journal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jarvis.R;

import java.util.ArrayList;

import jp.wasabeef.richeditor.RichEditor;

public class JournalAdapter extends RecyclerView.Adapter<JournalAdapter.JournalViewHolder> {
    Context context;



    ArrayList <Journal> journals;

    public ArrayList<Journal> getJournals(){
        return journals;
    }
    public void setJournals(ArrayList<Journal> journals) {
        this.journals = journals;
    }

    public JournalAdapter(Context context, ArrayList<Journal> journals){
        this.context = context;
        this.journals = journals;
    }
    @NonNull
    @Override
    public JournalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new JournalViewHolder(LayoutInflater.from(context).inflate(R.layout.custom_journal_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull JournalViewHolder holder, int position) {

        holder.title.setText(journals.get(position).getTitle());
        holder.description.setHtml(journals.get(position).getDescription());
        // Set Date
        String date = null;
        String[] month = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        date = journals.get(position).getDay() + " " +
                month[Integer.parseInt(journals.get(position).getMonth())] + ", " +
                journals.get(position).getYear();
        holder.date.setText(date);

        // Set Time
        String time = null;
        if(journals.get(position).getHour()!=null && journals.get(position).getMinute()!=null) {
            Integer hour = Integer.parseInt(journals.get(position).getHour());
            String amPm = " am";
            if (hour >= 12) {
                amPm = " pm";
                hour = hour - 12;
            }
            time = hour.toString() + ":" + journals.get(position).getMinute() + amPm;
        }
        holder.time.setText(time);

    }

    @Override
    public int getItemCount() {
        return journals.size();
    }

    public class JournalViewHolder extends RecyclerView.ViewHolder {
        TextView title, date, time;
        RichEditor description;
        public JournalViewHolder(@NonNull View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.journal_item_title);
            description = itemView.findViewById(R.id.journal_item_description);
            date = (TextView) itemView.findViewById(R.id.journal_item_date);
            time = (TextView) itemView.findViewById(R.id.journal_item_time);
            description.setInputEnabled(false);
            description.setEditorFontSize(12);
        }
    }
}
