package com.example.jarvis.Reminder;

import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
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

public class FragmentEvent extends Fragment implements View.OnClickListener, View.OnTouchListener, TimePicker.OnTimeChangedListener, DatePicker.OnDateChangedListener{


    private ArrayList<Event> events;

    private RecyclerView eventRecyclerView;
    private Switch eventSwitch;

//    private EventAdapter eventAdapter;
    private RecyclerTouchListener eventTouchListener;

    private ProgressBar eventProgressBar;
    private ToggleButton eventVoiceCommandToggleBtn;
    private FloatingActionButton addEventFab;

    // Event Variables
    private String eventDay, eventMonth, eventYear;
    private String eventHour, eventMinute;
    private String title, description;

    private int isDeleted, isIgnored;

    private String currentDay, currentMonth, currentYear;
    private String currentHour, currentMinute;
    private int currentIsDeleted, currentIsIgnored;
    private String currentTitle, currentDescription;

    private AlertDialog eventDialog;
    private AlertDialog.Builder eventDialogBuilder;
    private View eventDialogView;

    private TimePicker eventTimePicker;
    private Button addEventBtn;
    private TextView dialogEventTitle;
    private TextView dialogEventDescription;


    View view;
    public FragmentEvent(){

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_fragment_event, container, false);

        findXmlElements(view);
        setListeners();
        loadData();

        return view;
    }

    public void findXmlElements(View view){
//        eventRecyclerView = (RecyclerView) view.findViewById(R.id.event_recycler_view);
//        eventSwitch = (Switch) view.findViewById(R.id.event_item_switch);

//        eventProgressBar = (ProgressBar) view.findViewById(R.id.event_progress_bar);
//        eventVoiceCommandToggleBtn = (ToggleButton) view.findViewById(R.id.event_voice_command_toggle_btn);
//        addEventFab = (FloatingActionButton) view.findViewById(R.id.event_add_event_fab);

        // Event Dialog
        eventDialogBuilder = new AlertDialog.Builder(getActivity());
//        eventDialogView = getLayoutInflater().inflate(R.layout.dialog_event, null);
//        eventTimePicker = (TimePicker) eventDialogView.findViewById(R.id.dialog_event_time_picker);

//        addEventBtn = (Button) eventDialogView.findViewById(R.id.dialog_event_add_btn);
//        dialogEventTitle = (TextView) eventDialogView.findViewById(R.id.dialog_event_title);
//        dialogEventDescription = (TextView) eventDialogView.findViewById(R.id.dialog_event_description);

        eventDialogBuilder.setView(eventDialogView);
        eventDialog = eventDialogBuilder.create();
    }


    public void loadData(){
        SQLiteDatabaseHelper sqLiteDatabaseHelper = new SQLiteDatabaseHelper(getActivity());
        SQLiteDatabase sqLiteDatabase = sqLiteDatabaseHelper.getReadableDatabase();

        events = sqLiteDatabaseHelper.loadEventItems();

        setRecyclerView(events);
    }

    public void setRecyclerView(ArrayList<Event> events){
        // Set RecyclerView
        eventRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //eventAdapter = new AlarmAdapter(getActivity(),events);
        //eventRecyclerView.setAdapter(eventAdapter);
        //eventAdapter.notifyDataSetChanged();
    }

    public void setListeners(){
        // Swipe Options
     /*   eventTouchListener = new RecyclerTouchListener(getActivity(),eventRecyclerView);
        eventTouchListener
                .setClickable(new RecyclerTouchListener.OnRowClickListener() {
                    @Override
                    public void onRowClicked(int position) {
                        handleToggleAction(position);
                    }

                    @Override
                    public void onIndependentViewClicked(int independentViewID, int position) {

                    }
                })
                .setSwipeOptionViews(R.id.event_item_delete_rl, R.id.event_item_edit_rl)
                .setSwipeable(R.id.evet_item_fg, R.id.event_item_bg_end, new RecyclerTouchListener.OnSwipeOptionsClickListener() {
                    @Override
                    public void onSwipeOptionClicked(int viewID, int position) {
                        switch (viewID){
                            case R.id.event_item_delete_rl:
                                handleDeleteAction(position);
                                break;
                            case R.id.event_item_edit_rl:
                                handleEditAction(position);
                                showToast("Edit Pressed");
                                break;
                        }
                    }
                });

        // RecyclerView Touch Listener
        eventRecyclerView.addOnItemTouchListener(eventTouchListener);

        // TimePicker Time Change Listener
        eventTimePicker.setOnTimeChangedListener(this);

        // Add Event Button Listener
        addEventBtn.setOnClickListener(this);

        // Add Event FAB Listener
        addEventFab.setOnClickListener(this); */
    }


    public void handleToggleAction(int position){
        SQLiteDatabaseHelper sqLiteDatabaseHelper = new SQLiteDatabaseHelper(getActivity());
        SQLiteDatabase sqLiteDatabase = sqLiteDatabaseHelper.getWritableDatabase();

        /*
        if(events.get(position).getStatus()==1) {
            alarms.get(position).setStatus(0);
            // Updating Alarm
            sqLiteDatabaseHelper.updateAlarm(alarms.get(position), alarms.get(position).getHour(), alarms.get(position).getMinute(),alarms.get(position).getIsEveryday(),1);
        } else if(alarms.get(position).getStatus()==0) {
            alarms.get(position).setStatus(1);
            // Updating Alarm
            sqLiteDatabaseHelper.updateAlarm(alarms.get(position), alarms.get(position).getHour(), alarms.get(position).getMinute(),alarms.get(position).getIsEveryday(),0);
        }

        // Refresh Fragment
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (Build.VERSION.SDK_INT >= 26) {
            ft.setReorderingAllowed(false);
        }
        ft.detach(this).attach(this).commit();

        showToast("Toggle Pressed"); */
    }

    public void handleDeleteAction(int position){
        SQLiteDatabaseHelper sqLiteDatabaseHelper = new SQLiteDatabaseHelper(getActivity());
        SQLiteDatabase sqLiteDatabase = sqLiteDatabaseHelper.getReadableDatabase();

       sqLiteDatabaseHelper.deleteEvent(events.get(position).getDay(), events.get(position).getMonth(), events.get(position).getYear(), events.get(position).getHour(), events.get(position).getMinute(), events.get(position).getTitle(), events.get(position).getDescription());

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (Build.VERSION.SDK_INT >= 26) {
            ft.setReorderingAllowed(false);
        }
        ft.detach(this).attach(this).commit();

        showToast("Event Deleted");
    }

    public void handleEditAction(int position){

        currentDay = events.get(position).getDay();
        currentMonth = events.get(position).getMonth();
        currentYear = events.get(position).getYear();

        currentHour = events.get(position).getHour();
        currentMinute = events.get(position).getMinute();

        eventTimePicker.setCurrentHour(Integer.parseInt(currentHour));
        eventTimePicker.setCurrentMinute(Integer.parseInt(currentMinute));

        addEventBtn.setText("Update");
        dialogEventTitle.setText("Edit Your Event");

        disableButton(addEventBtn);
//        EventDialog.show();
    }

    public void handleAddAction(){
        addEventBtn.setText("Add");
        dialogEventTitle.setText("Add New Event");

        eventDialog.show();
    }

    public void showToast(String message){
        Toast toast = Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    @Override
    public void onClick(View view) {

        if(view == addEventFab){
            handleAddAction();
        } else if(view == addEventBtn){
            SQLiteDatabaseHelper sqLiteDatabaseHelper = new SQLiteDatabaseHelper(getActivity());
            SQLiteDatabase sqLiteDatabase = sqLiteDatabaseHelper.getWritableDatabase();

            if(addEventBtn.getText().equals("Add")){
                // Adding event
                sqLiteDatabaseHelper.insertEvent(new Event(eventDay, eventMonth, eventYear, eventHour, eventMinute, isDeleted, isIgnored, title, description));

                // Closing Dialog Box
                eventDialog.cancel();

                // Show Confirmation Toast
                showToast("Event Added");
            } else if(addEventBtn.getText().equals("Update")){
                // Updating Event
                sqLiteDatabaseHelper.updateEvent(new Event(eventDay, eventMonth, eventYear, eventHour, eventMinute, isDeleted, isIgnored, title, description),
                        currentDay, currentMonth, currentYear, currentHour, currentMinute, currentTitle, currentDescription);

                // Closing Dialog Box
                eventDialog.cancel();

                // Show Confirmation Toast
                showToast("Event Updated");
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
        return false;
    }

    @Override
    public void onDateChanged(DatePicker datePicker, int i, int i1, int i2) {

        eventDay = Integer.toString(datePicker.getDayOfMonth());
        eventMonth = Integer.toString(datePicker.getMonth());
        eventYear = Integer.toString(datePicker.getYear());

        if(!eventDay.equals(currentDay) || !eventMonth.equals(currentMonth) || !eventYear.equals(currentYear)){
            enableButton(addEventBtn);
        }


    }

    @Override
    public void onTimeChanged(TimePicker timePicker, int i, int i1) {
//        Calendar then = Calendar.getInstance();
//
//        then.set(Calendar.HOUR_OF_DAY, hourOfDay);
//        then.set(Calendar.MINUTE, minute);
//        then.set(Calendar.SECOND, 0);
//
//        Toast.makeText(this, then.getTime().toString(), Toast.LENGTH_SHORT)
//                .show();
        eventHour = timePicker.getCurrentHour().toString();
        eventMinute = timePicker.getCurrentMinute().toString();

        if(!eventHour.equals(currentHour) || !eventMinute.equals(currentMinute)){
            enableButton(addEventBtn);
        }
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
