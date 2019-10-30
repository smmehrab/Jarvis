package com.example.jarvis.Reminder;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jarvis.R;

import java.util.ArrayList;

public class FragmentAlarm extends Fragment {

    View view;

    private RecyclerView recyclerView;
    private ArrayList<Alarm> alarms;
    private Alarm alarm;


    public FragmentAlarm(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_fragment_alarm, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.alarm_recycler_view);

        alarms = new ArrayList<>();

        alarms.add(new Alarm("08", "10", 1, 1));
        alarms.add(new Alarm("09", "10", 1, 1));
        alarms.add(new Alarm("10", "10", 1, 1));
        alarms.add(new Alarm("11", "10", 1, 1));
        alarms.add(new Alarm("11", "20", 1, 1));
        alarms.add(new Alarm("12", "10", 1, 1));
        alarms.add(new Alarm("00", "10", 1, 1));

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        AlarmAdapter alarmAdapter = new AlarmAdapter(getContext(),alarms);

        recyclerView.setAdapter(alarmAdapter);

        return view;
    }
}
