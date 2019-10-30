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

public class BinRecordAdapter extends RecyclerView.Adapter<BinRecordAdapter.BinRecordViewHolder> {

    Context context;
    ArrayList<Record> records;

    public BinRecordAdapter(Context context, ArrayList<Record> records){
        this.context = context;
        this.records = records;
    }

    public void setRecords(ArrayList<Record> records) {
        this.records = records;
    }

    @NonNull
    @Override
    public BinRecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BinRecordViewHolder(LayoutInflater.from(context).inflate(R.layout.custom_wallet_bin_item , parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BinRecordViewHolder recordViewHolder, int position) {
        // Setting Values to the Title, Description & Amount
        recordViewHolder.title.setText(records.get(position).getTitle());
        recordViewHolder.description.setText(records.get(position).getDescription());
        recordViewHolder.amount.setText(records.get(position).getAmount());

        // Setting Color of the Record Type
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

    class BinRecordViewHolder extends RecyclerView.ViewHolder{
        TextView title, description, amount;
        public BinRecordViewHolder(@NonNull View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.wallet_bin_item_title);
            description = (TextView) itemView.findViewById(R.id.wallet_bin_item_description);
            amount = (TextView) itemView.findViewById(R.id.wallet_bin_item_amount);
        }
    }
}
