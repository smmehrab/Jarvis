package com.example.jarvis.Reminder;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jarvis.R;
import com.example.jarvis.Util.RecyclerTouchListener;

import java.util.ArrayList;

public class FragmentAlarm extends Fragment implements View.OnClickListener, View.OnTouchListener {

    private ArrayList<Alarm> alarms;

    private RecyclerView alarmRecyclerView;
    private Switch aSwitch;

    private AlarmAdapter alarmAdapter;
    private RecyclerTouchListener alarmTouchListener;

    public FragmentAlarm(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_fragment_alarm, container, false);

        findXmlElements(view);
        setListeners();
        loadData();

        return view;
    }

    public void showToast(String message){
        Toast toast = Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return true;
    }


    public void findXmlElements(View view){
        alarmRecyclerView = (RecyclerView) view.findViewById(R.id.alarm_recycler_view);
        aSwitch = (Switch) view.findViewById(R.id.alarm_item_switch);
    }


    public void loadData(){
        alarms = new ArrayList<>();

        Alarm alarm = new Alarm("8","30",0,1);
        alarms.add(alarm);
        alarms.add(alarm);
        alarms.add(alarm);
        alarms.add(alarm);
        alarms.add(alarm);
        alarms.add(alarm);
        alarms.add(alarm);
        alarms.add(alarm);
        alarms.add(alarm);
        alarms.add(alarm);
        alarms.add(alarm);
        alarms.add(alarm);
        alarms.add(alarm);
        alarms.add(alarm);
        alarms.add(alarm);
        alarms.add(alarm);
        alarms.add(alarm);
        alarms.add(alarm);
        alarms.add(alarm);
        alarms.add(alarm);

        setRecyclerView(alarms);
    }

    public void setRecyclerView(ArrayList<Alarm> alarms){
        // Set RecyclerView
        alarmRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        alarmAdapter = new AlarmAdapter(getActivity(),alarms);
        alarmRecyclerView.setAdapter(alarmAdapter);
        alarmAdapter.notifyDataSetChanged();
    }

    public void setListeners(){
        // Swipe Options
        alarmTouchListener = new RecyclerTouchListener(getActivity(),alarmRecyclerView);
        alarmTouchListener
                .setClickable(new RecyclerTouchListener.OnRowClickListener() {
                    @Override
                    public void onRowClicked(int position) {
//                        handleEditAction(position);
                        showToast("Toggle Pressed");
//                        aSwitch.toggle();
                    }

                    @Override
                    public void onIndependentViewClicked(int independentViewID, int position) {

                    }
                })
                .setSwipeOptionViews(R.id.alarm_item_delete_rl, R.id.alarm_item_edit_rl)
                .setSwipeable(R.id.alarm_item_fg, R.id.alarm_item_bg_end, new RecyclerTouchListener.OnSwipeOptionsClickListener() {
                    @Override
                    public void onSwipeOptionClicked(int viewID, int position) {
                        switch (viewID){
                            case R.id.alarm_item_delete_rl:
//                                handleDeleteAction(position);
                                showToast("Delete Pressed");
                                break;
                            case R.id.alarm_item_edit_rl:
//                                handleDeleteAction(position);
                                showToast("Edit Pressed");
                                break;
                        }
                    }
                });

        alarmRecyclerView.addOnItemTouchListener(alarmTouchListener);
    }
}
