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
import android.widget.DatePicker;
import android.widget.ProgressBar;
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

public class FragmentEvent extends Fragment implements View.OnClickListener, View.OnTouchListener, TimePicker.OnTimeChangedListener, DatePicker.OnDateChangedListener{

    private ArrayList<Event> events;

    private RecyclerView eventRecyclerView;

    private EventAdapter eventAdapter;
    private RecyclerTouchListener eventTouchListener;

    private ProgressBar eventProgressBar;
    private ToggleButton eventVoiceCommandToggleBtn;
    private FloatingActionButton addEventFab;

    // Event Variables
    private String title, description, type;
    private String year, month, day;
    private String hour, minute;
    private int isDeleted, isIgnored;

    private String currentTitle, currentDescription, currentType;
    private String currentYear, currentMonth, currentDay;
    private String currentHour, currentMinute;
    private int currentIsDeleted, currentIsIgnored;

    // Alert Dialog Variables
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
//        loadData();

        return view;
    }

    public void findXmlElements(View view){
        eventRecyclerView = (RecyclerView) view.findViewById(R.id.event_recycler_view);

        eventProgressBar = (ProgressBar) view.findViewById(R.id.event_progress_bar);
        eventVoiceCommandToggleBtn = (ToggleButton) view.findViewById(R.id.event_voice_command_toggle_btn);
        addEventFab = (FloatingActionButton) view.findViewById(R.id.event_add_event_fab);

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
        eventAdapter = new EventAdapter(getActivity(),events);
        eventRecyclerView.setAdapter(eventAdapter);
        eventAdapter.notifyDataSetChanged();
    }

    public void setListeners(){
        // Swipe Options
//     eventTouchListener = new RecyclerTouchListener(getActivity(),eventRecyclerView);
//        eventTouchListener
//                .setClickable(new RecyclerTouchListener.OnRowClickListener() {
//                    @Override
//                    public void onRowClicked(int position) {
//                        handleEventDetailsAction(position);
//                    }
//
//                    @Override
//                    public void onIndependentViewClicked(int independentViewID, int position) {
//
//                    }
//                })
//                .setSwipeOptionViews(R.id.event_item_delete_rl, R.id.event_item_edit_rl)
//                .setSwipeable(R.id.event_item_fg, R.id.event_item_bg_end, new RecyclerTouchListener.OnSwipeOptionsClickListener() {
//                    @Override
//                    public void onSwipeOptionClicked(int viewID, int position) {
//                        switch (viewID){
//                            case R.id.event_item_delete_rl:
//                                handleDeleteAction(position);
//                                break;
//                            case R.id.event_item_edit_rl:
//                                handleEditAction(position);
//                                showToast("Edit Pressed");
//                                break;
//                        }
//                    }
//                });
//
//        // RecyclerView Touch Listener
//        eventRecyclerView.addOnItemTouchListener(eventTouchListener);
//
////        // TimePicker Time Change Listener
////        eventTimePicker.setOnTimeChangedListener(this);
//
//        // Add Event Button Listener
//        addEventBtn.setOnClickListener(this);
//
//        // Add Event FAB Listener
//        addEventFab.setOnClickListener(this);
    }


    public void handleEventDetailsAction(int position){
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

        sqLiteDatabaseHelper.deleteEvent(events.get(position).getTitle(), events.get(position).getYear(), events.get(position).getMonth(), events.get(position).getDay());

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (Build.VERSION.SDK_INT >= 26) {
            ft.setReorderingAllowed(false);
        }
        ft.detach(this).attach(this).commit();

        showToast("Event Deleted");
    }

    public void handleEditAction(int position){

        currentYear = events.get(position).getYear();
        currentMonth = events.get(position).getMonth();
        currentDay = events.get(position).getDay();

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
                sqLiteDatabaseHelper.insertEvent(new Event(title, description, type, year, month, day, hour, minute, isDeleted, isIgnored));

                // Closing Dialog Box
                eventDialog.cancel();

                // Show Confirmation Toast
                showToast("Event Added");
            } else if(addEventBtn.getText().equals("Update")){
                // Updating Event
                sqLiteDatabaseHelper.updateEvent(new Event(title, description, type, year, month, day, hour, minute, isDeleted, isIgnored),
                        currentTitle, currentYear, currentMonth, currentDay);

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

        day = Integer.toString(datePicker.getDayOfMonth());
        month = Integer.toString(datePicker.getMonth());
        year = Integer.toString(datePicker.getYear());

        if(!day.equals(currentDay) || !month.equals(currentMonth) || !year.equals(currentYear)){
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
        hour = timePicker.getCurrentHour().toString();
        minute = timePicker.getCurrentMinute().toString();

        if(!hour.equals(currentHour) || !minute.equals(currentMinute)){
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
