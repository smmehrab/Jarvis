package com.example.jarvis.Reminder;

import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jarvis.R;
import com.example.jarvis.SQLite.SQLiteDatabaseHelper;
import com.example.jarvis.Util.RecyclerTouchListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class FragmentAlarm extends Fragment implements OnClickListener, View.OnTouchListener, TimePicker.OnTimeChangedListener {

    private ArrayList<Alarm> alarms;

    private RecyclerView alarmRecyclerView;
    private Switch aSwitch;

    private AlarmAdapter alarmAdapter;
    private RecyclerTouchListener alarmTouchListener;

    private ProgressBar alarmProgressBar;
    private ToggleButton alarmVoiceCommandToggleBtn;
    private FloatingActionButton addAlarmFab;

    // Alarm Variables
    private String alarmHour;
    private String alarmMinute;
    private Integer alarmIsEveryday=0;
    private Integer alarmStatus=1;

    private AlertDialog alarmDialog;
    private AlertDialog.Builder alarmDialogBuilder;
    private View alarmDialogView;

    private Switch isEverydaySwitch;
    private TimePicker alarmTimePicker;
    private Button addAlarmBtn;


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
        if(view == addAlarmFab){
            handleAddAction();
        } else if(view == addAlarmBtn){
            SQLiteDatabaseHelper sqLiteDatabaseHelper = new SQLiteDatabaseHelper(getActivity());
            SQLiteDatabase sqLiteDatabase = sqLiteDatabaseHelper.getWritableDatabase();

            sqLiteDatabaseHelper.insertAlarm(new Alarm(alarmHour, alarmMinute, alarmIsEveryday, alarmStatus));

            alarmDialog.cancel();

            FragmentTransaction ft = getFragmentManager().beginTransaction();
            if (Build.VERSION.SDK_INT >= 26) {
                ft.setReorderingAllowed(false);
            }
            ft.detach(this).attach(this).commit();

            showToast("Alarm Added");
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return true;
    }


    public void findXmlElements(View view){
        alarmRecyclerView = (RecyclerView) view.findViewById(R.id.alarm_recycler_view);
        aSwitch = (Switch) view.findViewById(R.id.alarm_item_switch);

        alarmProgressBar = (ProgressBar) view.findViewById(R.id.alarm_progress_bar);
        alarmVoiceCommandToggleBtn = (ToggleButton) view.findViewById(R.id.alarm_voice_command_toggle_btn);
        addAlarmFab = (FloatingActionButton) view.findViewById(R.id.alarm_add_alarm_fab);

        // Alarm Dialog
        alarmDialogBuilder = new AlertDialog.Builder(getActivity());
        alarmDialogView = getLayoutInflater().inflate(R.layout.dialog_alarm, null);
        alarmTimePicker = (TimePicker) alarmDialogView.findViewById(R.id.dialog_alarm_time_picker);
        isEverydaySwitch = (Switch) alarmDialogView.findViewById(R.id.dialog_alarm_is_everyday_switch);
        addAlarmBtn = (Button) alarmDialogView.findViewById(R.id.dialog_alarm_add_btn);

        alarmDialogBuilder.setView(alarmDialogView);
        alarmDialog = alarmDialogBuilder.create();
    }


    public void loadData(){
        SQLiteDatabaseHelper sqLiteDatabaseHelper = new SQLiteDatabaseHelper(getActivity());
        SQLiteDatabase sqLiteDatabase = sqLiteDatabaseHelper.getReadableDatabase();

        alarms = sqLiteDatabaseHelper.loadAlarmItems();

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
                                handleDeleteAction(position);
                                break;
                            case R.id.alarm_item_edit_rl:
                                handleEditAction(position);
                                showToast("Edit Pressed");
                                break;
                        }
                    }
                });

        // RecyclerView Touch Listener
        alarmRecyclerView.addOnItemTouchListener(alarmTouchListener);

        // TimePicker Time Change Listener
        alarmTimePicker.setOnTimeChangedListener(this);

        // isEveryday Switch Checked Change Listener
        isEverydaySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                    alarmIsEveryday=1;
                else
                    alarmIsEveryday=0;
            }
        });

        // Add Alarm Button Listener
        addAlarmBtn.setOnClickListener(this);

        // Add Alarm FAB Listener
        addAlarmFab.setOnClickListener(this);
    }

    public void handleDeleteAction(int position){
        SQLiteDatabaseHelper sqLiteDatabaseHelper = new SQLiteDatabaseHelper(getActivity());
        SQLiteDatabase sqLiteDatabase = sqLiteDatabaseHelper.getReadableDatabase();

        sqLiteDatabaseHelper.deleteAlarm(alarms.get(position).getHour(), alarms.get(position).getMinute());

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (Build.VERSION.SDK_INT >= 26) {
            ft.setReorderingAllowed(false);
        }
        ft.detach(this).attach(this).commit();

        showToast("Alarm Deleted");
    }

    public void handleEditAction(int position){
        alarmDialog.show();
    }

    public void handleAddAction(){
        alarmDialog.show();
    }

    @Override
    public void onTimeChanged(TimePicker timePicker, int hourOfDay, int minute) {
//        Calendar then = Calendar.getInstance();
//
//        then.set(Calendar.HOUR_OF_DAY, hourOfDay);
//        then.set(Calendar.MINUTE, minute);
//        then.set(Calendar.SECOND, 0);
//
//        Toast.makeText(this, then.getTime().toString(), Toast.LENGTH_SHORT)
//                .show();

        alarmHour = timePicker.getCurrentHour().toString();
        alarmMinute = timePicker.getCurrentMinute().toString();
    }
}
