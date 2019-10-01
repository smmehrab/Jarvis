package com.example.jarvis;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class CustomSpinnerAdapter extends ArrayAdapter<CustomSpinnerItem> {
    public CustomSpinnerAdapter(Context context, ArrayList<CustomSpinnerItem> customSpinnerItems) {
        super(context, 0, customSpinnerItems);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return customSpinnerView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return customSpinnerView(position, convertView, parent);
    }

    public View customSpinnerView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.spinner_custom, parent, false);
        }

        CustomSpinnerItem items = getItem(position);
        ImageView spinnerImage = convertView.findViewById(R.id.spinner_custom_image_view);
        TextView spinnerText = convertView.findViewById(R.id.spinner_custom_text_view);

        if(items != null){
            spinnerImage.setImageResource(items.getSpinnerImage());
            spinnerText.setText(items.getSpinnerText());
        }

        return convertView;
    }
}
