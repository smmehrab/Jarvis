package com.example.jarvis.Reminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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
import android.widget.TextView;
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
import java.util.Calendar;

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

    private String currentHour;
    private String currentMinute;
    private Integer currentIsEveryday;
    private Integer currentStatus;

    private AlertDialog alarmDialog;
    private AlertDialog.Builder alarmDialogBuilder;
    private View alarmDialogView;

    private Switch isEverydaySwitch;
    private TimePicker alarmTimePicker;
    private Button addAlarmBtn;
    private TextView dialogAlarmTitle;

    private AlarmManager alarmManager;

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
        dialogAlarmTitle = (TextView) alarmDialogView.findViewById(R.id.dialog_alarm_title);

        alarmDialogBuilder.setView(alarmDialogView);
        alarmDialog = alarmDialogBuilder.create();

        alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
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
                        handleToggleAction(position);
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

                if(!alarmIsEveryday.equals(currentIsEveryday)){
                    enableButton(addAlarmBtn);
                }
            }
        });

        // Add Alarm Button Listener
        addAlarmBtn.setOnClickListener(this);

        // Add Alarm FAB Listener
        addAlarmFab.setOnClickListener(this);
    }

    public void handleToggleAction(int position){
        SQLiteDatabaseHelper sqLiteDatabaseHelper = new SQLiteDatabaseHelper(getActivity());
        SQLiteDatabase sqLiteDatabase = sqLiteDatabaseHelper.getWritableDatabase();

        if(alarms.get(position).getStatus()==1) {
            alarms.get(position).setStatus(0);
            // Updating Alarm
            sqLiteDatabaseHelper.updateAlarm(alarms.get(position), alarms.get(position).getHour(), alarms.get(position).getMinute(),alarms.get(position).getIsEveryday(),1);

            int notificationID;
            notificationID = Integer.parseInt(alarms.get(position).getHour()+alarms.get(position).getMinute());
            showToast("notification ID " + notificationID);

            Intent intent = new Intent(getActivity(), AlertReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), notificationID, intent, 0);
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();


        } else if(alarms.get(position).getStatus()==0) {
            alarms.get(position).setStatus(1);
            // Updating Alarm
            sqLiteDatabaseHelper.updateAlarm(alarms.get(position), alarms.get(position).getHour(), alarms.get(position).getMinute(),alarms.get(position).getIsEveryday(),0);

            int notificationID;
            notificationID = Integer.parseInt(alarms.get(position).getHour()+alarms.get(position).getMinute());
            showToast("notification ID " + notificationID);

            Intent intent = new Intent(getActivity(), AlertReceiver.class);
            intent.putExtra("index", notificationID);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), notificationID, intent, 0);

            Calendar c = Calendar.getInstance();
            c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(alarms.get(position).getHour()));
            c.set(Calendar.MINUTE, Integer.parseInt(alarms.get(position).getMinute()));
            c.set(Calendar.SECOND, 0);
            if (c.before(Calendar.getInstance())) {
                c.add(Calendar.DATE, notificationID);
            }
            if(alarmIsEveryday == 1) {
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                        c.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
            }
            else{
                alarmManager.setExact(AlarmManager.RTC, c.getTimeInMillis(), pendingIntent);
            }

        }

        // Refresh Fragment
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (Build.VERSION.SDK_INT >= 26) {
            ft.setReorderingAllowed(false);
        }
        ft.detach(this).attach(this).commit();

        showToast("Toggle Pressed");
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

        //delete alarm
        int notificationID;
        notificationID = Integer.parseInt(alarms.get(position).getHour()+alarms.get(position).getMinute());
        showToast("notification ID " + notificationID);

        Intent intent = new Intent(getActivity(), AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), notificationID, intent, 0);
        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();

    }

    public void handleEditAction(int position){
        currentHour = alarms.get(position).getHour();
        currentMinute = alarms.get(position).getMinute();
        currentIsEveryday = alarms.get(position).getIsEveryday();
        currentStatus = alarms.get(position).getStatus();


        alarmTimePicker.setCurrentHour(Integer.parseInt(currentHour));
        alarmTimePicker.setCurrentMinute(Integer.parseInt(currentMinute));

        if(currentIsEveryday==1)
            isEverydaySwitch.setChecked(true);
        else
            isEverydaySwitch.setChecked(false);

        addAlarmBtn.setText("Update");
        dialogAlarmTitle.setText("Edit Your Alarm");

        disableButton(addAlarmBtn);
        alarmDialog.show();

    }

    public void handleAddAction(){
        addAlarmBtn.setText("Add");
        dialogAlarmTitle.setText("Add New Alarm");

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

        if(!alarmHour.equals(currentHour) || !alarmMinute.equals(currentMinute)){
            enableButton(addAlarmBtn);
        }

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

            if(addAlarmBtn.getText().equals("Add")){
                // Adding Alarm
                sqLiteDatabaseHelper.insertAlarm(new Alarm(alarmHour, alarmMinute, alarmIsEveryday, alarmStatus));


                int notificationID;
                /*
                String hour = null, minute = null;
                int isEveryday = 0, status = 0;
                int rowIndex = 0;
                sqLiteDatabase = sqLiteDatabaseHelper.getReadableDatabase();
                Cursor cursor = sqLiteDatabaseHelper.getAllAlarms(); //query on alarms table
                if(cursor.getCount() == 0){
                    Toast.makeText(getActivity(), "No data is found", Toast.LENGTH_LONG).show();
                }
                else{
                    while (cursor.moveToNext()){
                        rowIndex++;
                        hour = cursor.getString(0);
                        minute = cursor.getString(1);
                        isEveryday = cursor.getInt(2);
                        status = cursor.getInt(3);
                        if(Integer.parseInt(hour) == Integer.parseInt(alarmHour) && Integer.parseInt(minute) == Integer.parseInt(alarmMinute))
                        {
                            break;
                        }

                    }
                }*/
            //    Log.d("alarm hour", alarmHour);
            //    Log.d("alarm minute", alarmMinute);

                notificationID = Integer.parseInt(alarmHour+alarmMinute);


                showToast("notification ID " + notificationID);
                //showToast("index " + rowIndex + " hour " + hour + " minute " + minute);
                //showToast(" Is everyday " + alarmIsEveryday + " status " + alarmStatus);

                Intent intent = new Intent(getActivity(), AlertReceiver.class);
                intent.putExtra("index", notificationID);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), notificationID, intent, 0);

                Calendar c = Calendar.getInstance();
                c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(alarmHour));
                c.set(Calendar.MINUTE, Integer.parseInt(alarmMinute));
                c.set(Calendar.SECOND, 0);
                if (c.before(Calendar.getInstance())) {
                    c.add(Calendar.DATE, notificationID);
                }
                if(alarmIsEveryday == 1) {
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                                c.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
                }
                else{
                    alarmManager.setExact(AlarmManager.RTC, c.getTimeInMillis(), pendingIntent);
                }
                // ////////////////

                // Closing Dialog Box
                alarmDialog.cancel();

                // Show Confirmation Toast
                showToast("Alarm Added");
            } else if(addAlarmBtn.getText().equals("Update")){
                // Updating Alarm
                sqLiteDatabaseHelper.updateAlarm(new Alarm(alarmHour, alarmMinute, alarmIsEveryday, alarmStatus), currentHour, currentMinute, currentIsEveryday, currentStatus);

                //update alarmManager with new time
                int notificationID;
                notificationID = Integer.parseInt(alarmHour+alarmMinute);
                showToast("notification ID " + notificationID);

                Intent intent = new Intent(getActivity(), AlertReceiver.class);
                intent.putExtra("index", notificationID);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), notificationID, intent, 0);

                Calendar c = Calendar.getInstance();
                c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(alarmHour));
                c.set(Calendar.MINUTE, Integer.parseInt(alarmMinute));
                c.set(Calendar.SECOND, 0);
                if (c.before(Calendar.getInstance())) {
                    c.add(Calendar.DATE, notificationID);
                }
                if(currentIsEveryday == 1) {
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                            c.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
                }
                else if(currentIsEveryday == 0){
                    alarmManager.setExact(AlarmManager.RTC, c.getTimeInMillis(), pendingIntent);
                }

                //cancel previous alarm which is edited
                notificationID = Integer.parseInt(currentHour+currentMinute);
                showToast("previous notification ID " + notificationID);

                PendingIntent pendingIntent2 = PendingIntent.getBroadcast(getActivity(), notificationID, intent, 0);
                alarmManager.cancel(pendingIntent2);
                pendingIntent2.cancel();



                // Closing Dialog Box
                alarmDialog.cancel();

                // Show Confirmation Toast
                showToast("Alarm Updated");
            }

            // Refresh Fragment
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            if (Build.VERSION.SDK_INT >= 26) {
                ft.setReorderingAllowed(false);
            }
            ft.detach(this).attach(this).commit();
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return true;
    }

    void disableButton(Button button){
        button.setEnabled(false);
        button.setAlpha(.5f);
        button.setClickable(false);
    }

    void enableButton(Button button){
        button.setEnabled(true);
        button.setAlpha(1f);
        button.setClickable(true);
    }
}
