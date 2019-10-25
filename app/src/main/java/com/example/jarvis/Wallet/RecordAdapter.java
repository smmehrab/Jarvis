package com.example.jarvis.Wallet;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jarvis.R;

import java.util.ArrayList;

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.RecordViewHolder> {

    Context context;
    ArrayList<Record> records;

    public RecordAdapter(Context context, ArrayList<Record> records){
        this.context = context;
        this.records = records;
    }

    public void setRecords(ArrayList<Record> records) {
        this.records = records;
    }

    @NonNull
    @Override
    public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecordViewHolder(LayoutInflater.from(context).inflate(R.layout.custom_wallet_item , parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecordViewHolder recordViewHolder, int position) {
        recordViewHolder.title.setText(records.get(position).getTitle());
        recordViewHolder.description.setText(records.get(position).getDescription());
        recordViewHolder.amount.setText(records.get(position).getAmount());

        if(records.get(position).getType() == 0) {
            recordViewHolder.amount.setTextColor(Color.parseColor("#F15B40"));
        }
        else if(records.get(position).getType() == 1) {
            recordViewHolder.amount.setTextColor(Color.parseColor("#4CAF50"));
        }
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    class RecordViewHolder extends RecyclerView.ViewHolder{

        TextView title, description, amount;

        public RecordViewHolder(@NonNull View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.wallet_item_title);
            description = (TextView) itemView.findViewById(R.id.wallet_item_description);
            amount = (TextView) itemView.findViewById(R.id.wallet_item_amount);
        }
    }
}
